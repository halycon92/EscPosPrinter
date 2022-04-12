package it.cretu.escposprinter.model

import it.cretu.escposprinter.enums.*

class EscPosQrCodeLine : EscPosLine(){
    init{
        type = EscPosLineType.QRCODE
    }

    var text : String = ""
    var alignment : EscPosAlignment = EscPosAlignment.CENTER
    var correctionLevel : EscPosQrCodeCorrectionLevel = EscPosQrCodeCorrectionLevel.M
    var qrCodeSize : EscPosQrCodeSize = EscPosQrCodeSize.MEDIUM
    var showLabel : Boolean = false

    companion object{
        fun build(text : String, correctionLevel: EscPosQrCodeCorrectionLevel = EscPosQrCodeCorrectionLevel.M, size : EscPosQrCodeSize = EscPosQrCodeSize.MEDIUM,
                  alignment : EscPosAlignment = EscPosAlignment.CENTER, showLabel : Boolean = false) : EscPosQrCodeLine{
            val qrCodeLine = EscPosQrCodeLine()
            qrCodeLine.alignment = alignment
            qrCodeLine.showLabel = showLabel
            qrCodeLine.qrCodeSize = size
            qrCodeLine.correctionLevel = correctionLevel
            qrCodeLine.text = text
            return qrCodeLine
        }
    }
}