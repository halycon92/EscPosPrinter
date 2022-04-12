package it.cretu.escposprinter.model

import it.cretu.escposprinter.enums.EscPosAlignment
import it.cretu.escposprinter.enums.EscPosFontStyle
import it.cretu.escposprinter.enums.EscPosLineSize
import it.cretu.escposprinter.enums.EscPosLineType

/**
 * Represents EscPos Text Line
 */
class EscPosTextLine : EscPosLine(){
    /**
     * Overriding Base Member Type
     */
    init{
        type = EscPosLineType.TEXT
    }

    var alignment : EscPosAlignment = EscPosAlignment.LEFT
    var textSize : EscPosLineSize = EscPosLineSize.REGULAR
    var fontStyle : EscPosFontStyle = EscPosFontStyle.REGULAR
    var text : String = ""

    companion object{
        fun build(text : String, alignment: EscPosAlignment = EscPosAlignment.LEFT, textSize: EscPosLineSize = EscPosLineSize.REGULAR, fontStyle: EscPosFontStyle = EscPosFontStyle.REGULAR ) : EscPosTextLine{
            val line = EscPosTextLine()

            line.text = text
            line.alignment = alignment
            line.textSize = textSize
            line.fontStyle = fontStyle

            return line
        }
    }
}