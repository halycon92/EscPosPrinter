package it.cretu.escposprinter.model

import it.cretu.escposprinter.enums.*

class EscPosBarcodeLine : EscPosLine(){
    init{
        type = EscPosLineType.BARCODE
    }
    var value : String = ""
    var barcodeType : EscPosBarcodeType = EscPosBarcodeType.CODE128
    var barcodeLabelPosition : EscPosBarcodeLabelPosition = EscPosBarcodeLabelPosition.NONE
    var barcodeWidth : EscPosBarcodeWidth = EscPosBarcodeWidth.THIN
    var barcodeFont : EscPosBarcodeFont = EscPosBarcodeFont.FONT_A
    var alignment : EscPosAlignment = EscPosAlignment.CENTER
    var barcodeHeight : Int = 100

    fun validate(){
        if(value.isEmpty())
            throw Exception("Barcode value must be specified!")
        if(barcodeHeight < 1 || barcodeHeight > 255)
            throw Exception("Barcode's Height must be higher than 1 and lower than 255")

        if(barcodeType == EscPosBarcodeType.EAN13 && value.length != 13)
            throw Exception("EAN13 Barcodes must have value 13 characters long!")
        if(barcodeType == EscPosBarcodeType.EAN8 && value.length != 8)
            throw Exception("EAN8 Barcodes must have value 8 characters long!")

    }

    companion object{
        fun build(value : String, height : Int, width : EscPosBarcodeWidth = EscPosBarcodeWidth.THIN, barcodeType : EscPosBarcodeType = EscPosBarcodeType.UPC_A,
                  align : EscPosAlignment = EscPosAlignment.CENTER, barcodeFont : EscPosBarcodeFont = EscPosBarcodeFont.FONT_A,
                  labelPosition : EscPosBarcodeLabelPosition = EscPosBarcodeLabelPosition.NONE) : EscPosBarcodeLine{
            val barcodeLine = EscPosBarcodeLine()

            barcodeLine.value = value
            barcodeLine.barcodeHeight = height
            barcodeLine.alignment = align
            barcodeLine.barcodeWidth = width
            barcodeLine.barcodeType = barcodeType
            barcodeLine.barcodeLabelPosition = labelPosition
            barcodeLine.barcodeFont = barcodeFont

            return barcodeLine
        }
    }
}