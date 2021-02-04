package com.mercadolibre.android.andesui.tooltip.style

/**
 * Utility class that does two things: Defines the possible styles an [AndesTooltip] can take because it's an enum, as you can see.
 * But as a bonus it gives you the proper implementation so you don't have to make any mapping.
 *
 * You ask me with, let's say 'LIGHT', and then I'll give you a proper implementation of that style.
 *
 * @property type Possible styles that an [AndesTooltip] may take.
 */
enum class AndesTooltipStyle {
    LIGHT,
    DARK,
    HIGHLIGHT;

    companion object {
        fun fromString(value: String): AndesTooltipStyle = valueOf(value.toUpperCase())
    }

    internal val type get() = getAndesTooltipStyle()

    private fun getAndesTooltipStyle(): AndesTooltipStyleInterface {
        return when (this) {
            LIGHT -> AndesTooltipLightStyle
            else -> throw IllegalStateException("Other style is not available yet")
        }
    }
}
