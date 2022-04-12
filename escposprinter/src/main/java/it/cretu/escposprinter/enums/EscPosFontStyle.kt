package it.cretu.escposprinter.enums

/**
 * Provides Font Style Capabilities to EscPos Line
 */
enum class EscPosFontStyle {
    /**
     * Regular Font Style, No Bold and No Underline
     */
    REGULAR,

    /**
     * Bold Enabled and Double Underline
     */
    BOLD_DOUBLE_UNDERLINED,

    /**
     * Bold Enabled and Underline Enabled
     */
    BOLD_UNDERLINED,

    /**
     * Double Underline Enabled
     */
    DOUBLE_UNDERLINED,

    /**
     * Underline Enabled
     */
    UNDERLINED,

    /**
     * Bold Text Enabled
     */
    BOLD
}