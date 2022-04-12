package it.cretu.escposprinter.enums

/**
 * Specifies where to put Barcode's Label
 */
enum class EscPosBarcodeLabelPosition {
    /**
     * Above Barcode
     */
    ABOVE,
    /**
     * BELOW Barcode
     */
    BELOW,
    /**
     * Both Above and Below Barcode
     */
    BOTH,
    /**
     * No Label at all
     */
    NONE
}