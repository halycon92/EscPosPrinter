package it.cretu.escposprinter.model

import it.cretu.escposprinter.enums.EscPosLineType
import it.cretu.escposprinter.enums.EscPosSeparatorType

class EscPosSeparatorLine : EscPosLine(){
    init{
        type = EscPosLineType.SEPARATOR
    }
    var separatorType : EscPosSeparatorType = EscPosSeparatorType.DASH
    var isBold : Boolean = false

    companion object{
        fun build(separatorType: EscPosSeparatorType, isBold : Boolean = false) : EscPosSeparatorLine{
            val separatorLine = EscPosSeparatorLine()

            separatorLine.separatorType = separatorType
            separatorLine.isBold = isBold

            return separatorLine
        }
    }
}