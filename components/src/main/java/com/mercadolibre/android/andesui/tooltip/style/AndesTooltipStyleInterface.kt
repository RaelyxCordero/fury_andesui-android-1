package com.mercadolibre.android.andesui.tooltip.style

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.hierarchy.BackgroundColorConfig
import com.mercadolibre.android.andesui.button.hierarchy.createBackgroundColorConfigLoud
import com.mercadolibre.android.andesui.button.hierarchy.createBackgroundColorConfigQuiet
import com.mercadolibre.android.andesui.button.hierarchy.createBackgroundColorConfigTransparent
import com.mercadolibre.android.andesui.color.AndesColor
import com.mercadolibre.android.andesui.color.toAndesColor
import com.mercadolibre.android.andesui.icons.IconProvider
import com.mercadolibre.android.andesui.message.hierarchy.AndesMessageHierarchyInterface
import com.mercadolibre.android.andesui.message.type.AndesMessageTypeInterface
import com.mercadolibre.android.andesui.typeface.getFontOrDefault
import com.mercadolibre.android.andesui.utils.buildColoredAndesBitmapDrawable
import com.mercadolibre.android.andesui.utils.buildColoredCircularShapeWithIconDrawable

internal sealed class AndesTooltipStyleInterface {

    fun titleTypeface(context: Context): Typeface = context.getFontOrDefault(R.font.andes_font_semibold)

    fun bodyTypeface(context: Context): Typeface = context.getFontOrDefault(R.font.andes_font_regular)

    fun dismissibleIcon(context: Context) =
            buildColoredAndesBitmapDrawable(
                    IconProvider(context).loadIcon("andes_ui_close_20") as BitmapDrawable,
                    context,
                    color = dismissIconColor()
            )

    abstract fun dismissIconColor(): AndesColor?

    abstract fun backgroundColor(): AndesColor

    abstract fun textColor(): AndesColor

    abstract fun primaryActionColorConfig(buttonHierarchy: AndesButtonHierarchy): BackgroundColorConfig
    abstract fun primaryActionTextColor(buttonHierarchy: AndesButtonHierarchy): AndesColor

    abstract fun secondaryActionColorConfig(buttonHierarchy: AndesButtonHierarchy): BackgroundColorConfig
    abstract fun secondaryActionTextColor(buttonHierarchy: AndesButtonHierarchy): AndesColor

    abstract fun linkActionColorConfig(): BackgroundColorConfig
    abstract fun linkActionTextColor(): AndesColor
    abstract fun isLinkUnderlined(): Boolean
}

internal object AndesTooltipLightStyle : AndesTooltipStyleInterface() {
    override fun backgroundColor() = R.color.andes_bg_color_white.toAndesColor()

    override fun textColor() = R.color.andes_gray_800.toAndesColor()

    override fun dismissIconColor(): AndesColor? = null

    override fun primaryActionColorConfig(buttonHierarchy: AndesButtonHierarchy): BackgroundColorConfig {
        return when (buttonHierarchy){
            AndesButtonHierarchy.LOUD -> createBackgroundColorConfigLoud()
            AndesButtonHierarchy.QUIET -> createBackgroundColorConfigQuiet()
            else -> throw IllegalStateException("Transparent button hierarchy is not allowed in Andes Tooltip primary action")
        }
    }
    override fun primaryActionTextColor(buttonHierarchy: AndesButtonHierarchy): AndesColor {
        return when (buttonHierarchy){
            AndesButtonHierarchy.LOUD -> R.color.andes_white.toAndesColor()
            AndesButtonHierarchy.QUIET -> R.color.andes_accent_color_500.toAndesColor()

            else -> throw IllegalStateException("Transparent button hierarchy is not allowed in Andes Tooltip primary action")
        }
    }

    override fun secondaryActionColorConfig(buttonHierarchy: AndesButtonHierarchy): BackgroundColorConfig {
        return when (buttonHierarchy) {
            AndesButtonHierarchy.QUIET -> createBackgroundColorConfigQuiet()
            AndesButtonHierarchy.TRANSPARENT -> createBackgroundColorConfigTransparent()
            else -> throw IllegalStateException("Loud button hierarchy is not allowed in Andes Tooltip secondary action")
        }
    }
    override fun secondaryActionTextColor(buttonHierarchy: AndesButtonHierarchy) = R.color.andes_accent_color_500.toAndesColor()

    override fun linkActionColorConfig(): BackgroundColorConfig {
        return createBackgroundColorConfigTransparent()
    }
    override fun linkActionTextColor() = R.color.andes_accent_color_500.toAndesColor()
    override fun isLinkUnderlined() = false
}

