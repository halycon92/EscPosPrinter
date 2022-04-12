package it.cretu.escposprinter

import it.cretu.escposprinter.enums.*
import it.cretu.escposprinter.model.*
import java.io.OutputStream
import java.net.Socket

/**
 * Represents EscPosPrinter
 * @param ipAddress IP Address of Printer
 * @param port Port on which Printer is listening, DEFAULT: 9100
 * @param connTimeout Specifies MAX Connection Timeout
 */
class EscPosPrinter(private val ipAddress: String, private val port: Int = 9100, private val connTimeout: Int = 1000) {
    //We'll use a socket in order to connect to Printer
    private var socket: Socket? = null

    //We'll use an OutputStream in order to send Data to Printer
    private var outStream: OutputStream? = null

    //Using Internal EscPosReceipt in order to store EscPos lines
    private val receipt = EscPosReceipt()

    //Using Print Queue as List<ByteArray>
    private val queueBytes: ArrayList<ByteArray> = ArrayList()

    /**
     * Connection Method, Checks if Printer is reachable and available
     */
    private fun connect(): ConnectionResult {
        val connResult = ConnectionResult()

        try {
            //If not already connected
            if (socket == null || socket?.isConnected == false) {
                //Connecting Socket
                socket = Socket(ipAddress, port)
                socket?.soTimeout = connTimeout
                //Retrieving its OutputStream
                outStream = socket?.getOutputStream()
            }
        } catch (ex: Exception) {
            connResult.connectionOk = false
            connResult.connectionError = ex.message ?: ex.toString()
        }

        return connResult
    }

    /**
     * Disconnects from Printer, releasing Connection
     */
    private fun disconnect() {
        outStream?.close()
        if (socket != null && socket?.isConnected == true)
            socket?.close()

    }

    /**
     * Instructs Printer to Align Content
     * @param alignment Content's alignment
     */
    private fun setAlignment(alignment: EscPosAlignment) {
        when (alignment) {
            EscPosAlignment.LEFT -> queueBytes.add(EscPosCommands.TXT_ALIGN_LT)
            EscPosAlignment.CENTER -> queueBytes.add(EscPosCommands.TXT_ALIGN_CT)
            EscPosAlignment.RIGHT -> queueBytes.add(EscPosCommands.TXT_ALIGN_RT)
        }
    }

    /**
     * Adds a Separator Line to Print Queue
     */
    fun addLine(line: EscPosSeparatorLine) {
        receipt.addLine(line)
    }

    /**
     * Simply adds an EscPos Text Line to Print Queue
     */
    fun addLine(line: EscPosTextLine) {
        receipt.addLine(line)
    }

    /**
     * Adds an EscPos Barcode to Print Queue
     */
    fun addLine(line: EscPosBarcodeLine) {
        receipt.addLine(line)
    }

    /**
     * Adds an EscPos QrCode to Print Queue
     */
    fun addLine(line : EscPosQrCodeLine){
        receipt.addLine(line)
    }

    fun addLine(){
        receipt.addLine(EscPosLine())
    }

    fun feed(numFeeds : Int = 1){
        for(i in 0..numFeeds)
            queueBytes.add(EscPosCommands.CTL_LF)
    }


    /**
     * Connects to Printer and Prints pending Print queue
     */
    fun print(clearQueue: Boolean = true) {
        //Connecting to Printer
        val connResult: ConnectionResult = connect()

        //If Connection was successfull
        if (connResult.connectionOk) {
            //Converting EscPos lines into ByteArrays
            for (line in receipt.getLines()) {
                //Changing Behaviour base on line's Type
                if (line is EscPosTextLine) {
                    //Setting Alignment
                    setAlignment(line.alignment)

                    //Setting Text Size
                    when (line.textSize) {
                        EscPosLineSize.REGULAR -> queueBytes.add(EscPosCommands.TXT_NORMAL)
                        EscPosLineSize.DOUBLE_HEIGHT -> queueBytes.add(EscPosCommands.TXT_2HEIGHT)
                        EscPosLineSize.DOUBLE_WIDTH -> queueBytes.add(EscPosCommands.TXT_2WIDTH)
                        EscPosLineSize.DOUBLE_WIDTH_HEIGHT -> queueBytes.add(EscPosCommands.TXT_4SQUARE)
                    }

                    //Setting Font Style
                    when (line.fontStyle) {
                        EscPosFontStyle.REGULAR -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_OFF)
                            queueBytes.add(EscPosCommands.TXT_UNDERL_OFF)
                        }
                        EscPosFontStyle.BOLD_DOUBLE_UNDERLINED -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_ON)
                            queueBytes.add(EscPosCommands.TXT_UNDERL2_ON)
                        }
                        EscPosFontStyle.BOLD_UNDERLINED -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_ON)
                            queueBytes.add(EscPosCommands.TXT_UNDERL_ON)
                        }
                        EscPosFontStyle.DOUBLE_UNDERLINED -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_OFF)
                            queueBytes.add(EscPosCommands.TXT_UNDERL2_ON)
                        }
                        EscPosFontStyle.UNDERLINED -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_OFF)
                            queueBytes.add(EscPosCommands.TXT_UNDERL_ON)
                        }
                        EscPosFontStyle.BOLD -> {
                            queueBytes.add(EscPosCommands.TXT_BOLD_ON)
                            queueBytes.add(EscPosCommands.TXT_UNDERL_OFF)
                        }
                    }

                    //Adding Supplied Text and a Blank Line
                    queueBytes.add(line.text.toByteArray())
                    queueBytes.add(EscPosCommands.CTL_LF)
                }
                else if (line is EscPosBarcodeLine) {
                    //Validating Barcode Line
                    line.validate()

                    addBarcodeToQueue(line.value, line.alignment, line.barcodeHeight, line.barcodeType, line.barcodeLabelPosition, line.barcodeWidth, line.barcodeFont)
                }
                else if(line is EscPosQrCodeLine){
                    addQrCodeToQueue(line.text, line.correctionLevel, line.qrCodeSize, line.showLabel, line.alignment)
                }
                else if (line is EscPosSeparatorLine) {
                    //Enabling Bold Text if Requested
                    if (line.isBold) {
                        queueBytes.add(EscPosCommands.TXT_BOLD_ON)
                        queueBytes.add(EscPosCommands.TXT_UNDERL_OFF)
                    }

                    //Building and Adding Separator Line to Print Queue
                    var separatorChars = "------------------------------------------"
                    if (line.separatorType == EscPosSeparatorType.UNDERSCORE)
                        separatorChars = separatorChars.replace('-', '_')
                    setAlignment(EscPosAlignment.CENTER)
                    queueBytes.add(separatorChars.toByteArray())
                    queueBytes.add(EscPosCommands.CTL_LF)
                }
            }

            //Printing EscPos Lines
            for (byteArr in queueBytes)
                outStream?.write(byteArr)

            //Adding 6 blank lines
            for (i in 0..6)
                outStream?.write(EscPosCommands.CTL_LF)

            //Cutting Paper
            outStream?.write(EscPosCommands.PAPER_FULL_CUT)

            //Disconnecting From Printer
            disconnect()

            //If specified, Print queue is cleared
            if (clearQueue)
                receipt.clear()
        } else
            throw Exception("Connection to Printer with IP: $ipAddress on Port: $port Failed due to Error:\n${connResult.connectionError}")
    }

    private fun addBarcodeToQueue(value : String, alignment : EscPosAlignment, barcodeHeight : Int, barcodeType : EscPosBarcodeType, labelPosition: EscPosBarcodeLabelPosition, barcodeWidth : EscPosBarcodeWidth,
                                  barcodeFont : EscPosBarcodeFont){
        setAlignment(alignment)
        
        //GS H = HRI position
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("H".toByteArray())

        val pos : Int = when(labelPosition){
            EscPosBarcodeLabelPosition.NONE -> 0
            EscPosBarcodeLabelPosition.ABOVE -> 1
            EscPosBarcodeLabelPosition.BELOW -> 2
            EscPosBarcodeLabelPosition.BOTH -> 3
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(pos)) //0=no print, 1=above, 2=below, 3=above & below

        //GS f = set barcode characters
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("f".toByteArray())

        val font : Int = when(barcodeFont){
            EscPosBarcodeFont.FONT_A -> 0
            EscPosBarcodeFont.FONT_B -> 1
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(font))

        //GS h = sets barcode height
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("h".toByteArray())
        queueBytes.add(EscPosCommands.byteArrayOfInts(barcodeHeight))

        //GS w = sets barcode width
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("w".toByteArray())

        val width : Int = when(barcodeWidth){
            EscPosBarcodeWidth.THICKEST -> 2
            EscPosBarcodeWidth.THICK -> 3
            EscPosBarcodeWidth.THIN -> 4
            EscPosBarcodeWidth.THINNEST -> 5
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(width))//module = 1-6

        //GS k
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D)) //GS
        queueBytes.add("k".toByteArray()) //k

        val type : Int = when(barcodeType){
            EscPosBarcodeType.CODE39 -> 69
            EscPosBarcodeType.EAN13 -> 67
            EscPosBarcodeType.UPC_A -> 65
            EscPosBarcodeType.ITF -> 70
            EscPosBarcodeType.EAN8 -> 68
            EscPosBarcodeType.CODABAR -> 71
            EscPosBarcodeType.CODE93 -> 72
            EscPosBarcodeType.UPC_E -> 66
            EscPosBarcodeType.CODE128 -> 73
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(type))//m = barcode type 0-6
        queueBytes.add(EscPosCommands.byteArrayOfInts(value.length)) //length of encoded string
        queueBytes.add(value.toByteArray())//d1-dk
        queueBytes.add(EscPosCommands.byteArrayOfInts(0))//print barcode

        queueBytes.add(EscPosCommands.CTL_LF)
    }

    /**
     * Adds a QRCode to Print Queue
     * @param value QRCode's value
     * @param correctionLevel QRCode Correction Level
     * @see EscPosQrCodeCorrectionLevel
     * @param size QrCode's size
     * @see EscPosQrCodeSize
     * @param showLabel Specifies if QRCode will show it content below
     * @param alignment QRCode's alignment, by default will be CENTER
     */
    private fun addQrCodeToQueue(value : String, correctionLevel: EscPosQrCodeCorrectionLevel, size : EscPosQrCodeSize, showLabel : Boolean, alignment : EscPosAlignment){
        setAlignment(alignment)

        //save data function 80
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))//init
        queueBytes.add("(k".toByteArray())//adjust height of barcode
        queueBytes.add(EscPosCommands.byteArrayOfInts(value.length + 3)) //pl
        queueBytes.add(EscPosCommands.byteArrayOfInts(0)) //ph
        queueBytes.add(EscPosCommands.byteArrayOfInts(49)) //cn
        queueBytes.add(EscPosCommands.byteArrayOfInts(80)) //fn
        queueBytes.add(EscPosCommands.byteArrayOfInts(48)) //
        queueBytes.add(value.toByteArray())

        //error correction function 69
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("(k".toByteArray())
        queueBytes.add(EscPosCommands.byteArrayOfInts(3)) //pl
        queueBytes.add(EscPosCommands.byteArrayOfInts(0)) //ph
        queueBytes.add(EscPosCommands.byteArrayOfInts(49)) //cn
        queueBytes.add(EscPosCommands.byteArrayOfInts(69)) //fn

        val correction : Int = when(correctionLevel){
            EscPosQrCodeCorrectionLevel.M -> 49
            EscPosQrCodeCorrectionLevel.H -> 51
            EscPosQrCodeCorrectionLevel.L -> 48
            EscPosQrCodeCorrectionLevel.Q -> 50
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(correction)) //48<= n <= 51

        //size function 67
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("(k".toByteArray())
        queueBytes.add(EscPosCommands.byteArrayOfInts(3)) //pl
        queueBytes.add(EscPosCommands.byteArrayOfInts(0)) //ph
        queueBytes.add(EscPosCommands.byteArrayOfInts(49)) //cn
        queueBytes.add(EscPosCommands.byteArrayOfInts(67))

        val qrCodeSize : Int = when(size){
            EscPosQrCodeSize.SMALL -> 5
            EscPosQrCodeSize.MEDIUM -> 10
            EscPosQrCodeSize.BIG -> 15
        }
        queueBytes.add(EscPosCommands.byteArrayOfInts(qrCodeSize))//1<= n <= 16

        //print function 81
        queueBytes.add(EscPosCommands.byteArrayOfInts(0x1D))
        queueBytes.add("(k".toByteArray())
        queueBytes.add(EscPosCommands.byteArrayOfInts(3)) //pl
        queueBytes.add(EscPosCommands.byteArrayOfInts(0)) //ph
        queueBytes.add(EscPosCommands.byteArrayOfInts(49)) //cn
        queueBytes.add(EscPosCommands.byteArrayOfInts(81)) //fn
        queueBytes.add(EscPosCommands.byteArrayOfInts(48)) //m

        if(showLabel)
            queueBytes.add(value.toByteArray())

        queueBytes.add(EscPosCommands.CTL_LF)
    }

    /**
     * Class containing All used Byte commands to be sent to printer
     */
    private class EscPosCommands {
        companion object {
            fun byteArrayOfInts(vararg ints: Int) =
                ByteArray(ints.size) { pos -> ints[pos].toByte() }

            //Empty Line, basically used for spacing
            val CTL_LF: ByteArray = byteArrayOfInts(0x0a)

            //Orders Printer to Cut Paper
            val PAPER_FULL_CUT: ByteArray = byteArrayOfInts(0x1d, 0x56, 0x00)

            // Left Alignment
            val TXT_ALIGN_LT: ByteArray = byteArrayOfInts(0x1b, 0x61, 0x00)

            // Center Alignment
            val TXT_ALIGN_CT: ByteArray = byteArrayOfInts(0x1b, 0x61, 0x01)

            // Right Alignment
            val TXT_ALIGN_RT: ByteArray = byteArrayOfInts(0x1b, 0x61, 0x02)

            //Regular Text Size
            val TXT_NORMAL: ByteArray = byteArrayOfInts(0x1b, 0x21, 0x00)

            //Double Height Text Size
            val TXT_2HEIGHT: ByteArray = byteArrayOfInts(0x1b, 0x21, 0x10)

            //Double Width Text Size
            val TXT_2WIDTH: ByteArray = byteArrayOfInts(0x1b, 0x21, 0x20)

            //Double Width and Height Text Size
            val TXT_4SQUARE: ByteArray = byteArrayOfInts(0x1b, 0x21, 0x30)

            // Bold font OFF
            val TXT_BOLD_OFF: ByteArray = byteArrayOfInts(0x1b, 0x45, 0x00)

            // Bold font ON
            val TXT_BOLD_ON: ByteArray = byteArrayOfInts(0x1b, 0x45, 0x01)

            // Underline font OFF
            val TXT_UNDERL_OFF: ByteArray = byteArrayOfInts(0x1b, 0x2d, 0x00)

            // Underline font 1-dot ON
            val TXT_UNDERL_ON: ByteArray = byteArrayOfInts(0x1b, 0x2d, 0x01)

            // Underline font 2-dot ON
            val TXT_UNDERL2_ON: ByteArray = byteArrayOfInts(0x1b, 0x2d, 0x02)
        }
    }
}