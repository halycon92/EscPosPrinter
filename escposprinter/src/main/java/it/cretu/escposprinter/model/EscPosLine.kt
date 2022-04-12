package it.cretu.escposprinter.model

import it.cretu.escposprinter.enums.EscPosLineType

/**
 * Represents Basic EscPos Line
 * If not overriden, will be NEWLINE Type
 */
open class EscPosLine {
    var type : EscPosLineType = EscPosLineType.NEWLINE
}