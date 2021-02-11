package com.mercadolibre.android.andesui.tooltip.style

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
