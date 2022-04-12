package it.cretu.escposprinter.enums

/**
 * Specifies EscPos Line Type
 */
enum class EscPosLineType {
    /**
     * NewLine Type, Provides Spacing Between lines
     */
    NEWLINE,

    /**
     * Simple Text Line
     */
    TEXT,

    /**
     * Separator Line, Provides Spacing between lines by printing additional line containing custom char
     */
    SEPARATOR,

    /**
     * EscPos Line Containing Image
     */
    IMAGE,

    /**
     * EscPos line containing Barcode
     */
    BARCODE,

    /**
     * EscPos line containing QRCode
     */
    QRCODE
}