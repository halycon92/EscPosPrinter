package it.cretu.escposprinter.enums

/**
 * Degree of Error Correction in QRCodes
 */
enum class EscPosQrCodeCorrectionLevel {
    /**
     * Low Correction Level, 7%
     */
    L,

    /**
     * Medium Correction Level, 15%
     */
    M,

    /**
     * Quartile Correction Level, 25%
     */
    Q,

    /**
     * High Correction Level, 30%
     */
    H
}