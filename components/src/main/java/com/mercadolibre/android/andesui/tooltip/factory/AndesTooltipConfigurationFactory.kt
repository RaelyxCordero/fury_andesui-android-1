package com.mercadolibre.android.andesui.tooltip.factory

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.hierarchy.BackgroundColorConfig
import com.mercadolibre.android.andesui.color.AndesColor
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipAction
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipLinkAction
import com.mercadolibre.android.andesui.tooltip.style.AndesTooltipStyle
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocation

internal data class AndesTooltipConfiguration(
        val backgroundColor: AndesColor,
        val textColor: AndesColor,
        val titleText: String? = null,
        val titleTypeface: Typeface?,
        val titleTextSize: Float?,
        val bodyText: String,
        val bodyTypeface: Typeface?,
        val bodyTextSize: Float?,
        val isDismissible: Boolean,
        val dismissibleIcon: Drawable?,
        val primaryAction: AndesTooltipAction?,
        val primaryActionBackgroundColor: BackgroundColorConfig?,
        val primaryActionTextColor: AndesColor?,
        val secondaryAction: AndesTooltipAction?,
        val secondaryActionBackgroundColor: BackgroundColorConfig?,
        val secondaryActionTextColor: AndesColor?,
        val linkAction: AndesTooltipLinkAction?,
        val linkActionBackgroundColor: BackgroundColorConfig?,
        val linkActionTextColor: AndesColor?,
        val linkActionIsUnderlined: Boolean?,
        val tooltipLocation: AndesTooltipLocation,
        val isDynamicWidth: Boolean
)

@Suppress("TooManyFunctions")
internal object AndesTooltipConfigurationFactory {

    fun create(context: Context, andesMessageAttrs: AndesTooltipAttrs): AndesTooltipConfiguration {
        return with(andesMessageAttrs) {
            AndesTooltipConfiguration(
                backgroundColor = resolveBackgroundColor(style),
                    textColor = resolveTextColor(style),
                    titleText = title,
                    titleTypeface = resolveTitleTypeface(style, context),
                    titleTextSize = resolveTitleSize(context),
                    bodyText = body,
                    bodyTypeface = resolveBodyTypeface(style, context),
                    bodyTextSize = resolveBodySize(context),
                    isDismissible = isDismissible,
                    dismissibleIcon = resolveDismissibleIcon(style, context),
                    primaryAction = mainAction,
                    primaryActionBackgroundColor = mainAction?.hierarchy?.let { resolvePrimaryActionBackgroundColor(style, it) },
                    primaryActionTextColor = mainAction?.hierarchy?.let { resolvePrimaryActionTextColor(style, it) },
                    secondaryAction = secondaryAction,
                    secondaryActionBackgroundColor = secondaryAction?.hierarchy?.let { resolveSecondaryActionBackgroundColor(style, it) },
                    secondaryActionTextColor = secondaryAction?.hierarchy?.let { resolveSecondaryActionTextColor(style, it) },
                    linkAction = linkAction,
                    linkActionBackgroundColor = resolveLinkActionBackgroundColor(style),
                    linkActionTextColor = resolveLinkActionTextColor(style),
                    linkActionIsUnderlined = resolveBodyLinkIsUnderlined(style),
                    tooltipLocation = tooltipLocation,
                    isDynamicWidth = mainAction == null

            )
        }
    }

    private fun resolveBackgroundColor(style: AndesTooltipStyle) = style.type.backgroundColor()
    private fun resolveTextColor(style: AndesTooltipStyle) = style.type.textColor()
    private fun resolveTitleSize(context: Context) = context.resources.getDimension(R.dimen.andes_message_title)
    private fun resolveBodySize(context: Context) = context.resources.getDimension(R.dimen.andes_message_body)
    private fun resolveTitleTypeface(style: AndesTooltipStyle, context: Context) = style.type.titleTypeface(context)
    private fun resolveBodyTypeface(style: AndesTooltipStyle, context: Context) = style.type.bodyTypeface(context)
    private fun resolveDismissibleIcon(style: AndesTooltipStyle, context: Context) = style.type.dismissibleIcon(context)

    private fun resolvePrimaryActionBackgroundColor(style: AndesTooltipStyle, buttonHierarchy: AndesButtonHierarchy) =
            style.type.primaryActionColorConfig(buttonHierarchy)

    private fun resolvePrimaryActionTextColor(style: AndesTooltipStyle, buttonHierarchy: AndesButtonHierarchy) =
            style.type.primaryActionTextColor(buttonHierarchy)

    private fun resolveSecondaryActionBackgroundColor(style: AndesTooltipStyle, buttonHierarchy: AndesButtonHierarchy) =
            style.type.secondaryActionColorConfig(buttonHierarchy)

    private fun resolveSecondaryActionTextColor(style: AndesTooltipStyle, buttonHierarchy: AndesButtonHierarchy) =
            style.type.secondaryActionTextColor(buttonHierarchy)

    private fun resolveLinkActionBackgroundColor(style: AndesTooltipStyle) =
            style.type.linkActionColorConfig()

    private fun resolveLinkActionTextColor(style: AndesTooltipStyle) =
            style.type.linkActionTextColor()

    private fun resolveBodyLinkIsUnderlined(style: AndesTooltipStyle) =
            style.type.isLinkUnderlined()
}
