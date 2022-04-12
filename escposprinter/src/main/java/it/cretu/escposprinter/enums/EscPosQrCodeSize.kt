package it.cretu.escposprinter.enums

/**
 * Represents Size of Printed QRCode
 * Goes from 1 to 16 by Default
 */
enum class EscPosQrCodeSize {
    /**
     * Small Size, will correspond to Size 5
     */
    SMALL,

    /**
     * Medium Size, will correspond to Size 10
     */
    MEDIUM,

    /**
     * Biggest Size, will correspond to Size 15
     */
    BIG
}