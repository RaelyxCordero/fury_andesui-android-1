package com.mercadolibre.android.andesui.button.factory

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.button.factory.AndesButtonConfigurationFactory.create
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchyInterface
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIcon
import com.mercadolibre.android.andesui.button.size.AndesButtonSize
import com.mercadolibre.android.andesui.button.size.AndesButtonSizeInterface

/**
 * Useful class that holds the data that the [AndesButton] will use to draw the button accordingly.
 *
 * @property background color of the bg for normal, pressed, hover and focused states.
 * @property textColor color of the text.
 * @property textSize size to use for the text of the button.
 * @property margin holder of all the data related to margin.
 * @property height height of the button.
 * @property typeface typeface to use with the text.
 * @property iconConfig holds the data needed to draw the icon accordingly.
 * @property enabled holds the data needed to know if the button is enabled or not.
 * @property lateralPadding holds the lateral padding of the button.
 */
internal data class AndesButtonConfiguration(
    val background: Drawable,
    val text: String? = null,
    val textColor: ColorStateList,
    val textSize: Float,
    val margin: AndesButtonMargin,
    val height: Float,
    val typeface: Typeface,
    val iconConfig: IconConfig? = null,
    val enabled: Boolean = true,
    val lateralPadding: Int,
    val isLoading: Boolean = false
) {
    /**
     * Constant representing the max of lines a button can have
     */
    val maxLines = 1

    /**
     * Utility method that retrieves the left icon from the [IconConfig], if it's not null.
     * Left icon and right icon must be mutually exclusive: Both cannot be non null simultaneously,
     * BUT they CAN be null simultaneously: This means the button has no icon.
     */
    val leftIcon get() = iconConfig?.leftIcon

    /**
     * Utility method that retrieves the right icon from the [IconConfig], if it's not null.
     * Right icon and left icon must be mutually exclusive: Both cannot be non null simultaneously.
     * BUT they CAN be null simultaneously: This means the button has no icon.
     */
    val rightIcon get() = iconConfig?.rightIcon
}

/**
 * Factory that is in charge of creating an [AndesButtonConfiguration].
 * The main method here is [create] which is overloaded:
 * One to be used for XML inflation and the other to be used to programmatic inflation.
 * Both techniques are based on reading each one of the properties received and putting
 * the solved value into the [AndesButtonConfiguration] that will return.
 */
internal object AndesButtonConfigurationFactory {

    /**
     *
     * @param context needed for accessing some resources.
     * @param andesButtonAttrs parsed attributes with AndesButton data from XML
     * @return [AndesButtonConfiguration] that contains all the data that [AndesButton] needs to draw itself properly.
     */
    fun create(context: Context, andesButtonAttrs: AndesButtonAttrs): AndesButtonConfiguration {
        val hierarchy = andesButtonAttrs.andesButtonHierarchy.hierarchy
        val size = andesButtonAttrs.andesButtonSize.size

        return AndesButtonConfiguration(
            background = resolveBackground(hierarchy, size, context),
            text = andesButtonAttrs.andesButtonText,
            textColor = resolveTextColor(hierarchy, context),
            textSize = resolveTextSize(size, context),
            margin = resolveMargin(size, andesButtonAttrs.andesButtonLeftIconPath,
                andesButtonAttrs.andesButtonRightIconPath, andesButtonAttrs.andesButtonLeftDrawable,
                andesButtonAttrs.andesButtonRightDrawable, context),
            height = resolveHeight(size, context),
            typeface = resolveTypeface(hierarchy, context),
            iconConfig = resolveIconConfig(size, hierarchy,
                andesButtonAttrs.andesButtonLeftIconPath,
                andesButtonAttrs.andesButtonRightIconPath, andesButtonAttrs.andesButtonLeftDrawable,
                andesButtonAttrs.andesButtonRightDrawable, context),
            enabled = andesButtonAttrs.andesButtonEnabled,
            lateralPadding = resolveLateralPadding(size, context),
            isLoading = andesButtonAttrs.andesButtonIsLoading
        )
    }

    /**
     * Infers which hierarchy, size, background, etc should be used.
     * Once determined, puts it into the [AndesButtonConfiguration] that it  will return.
     *
     * @param context needed for accessing some resources.
     * @param andesButtonSize one of the elements inside [AndesButtonSize]. Will determine the size of the button.
     * This is the programmatic equivalent of the XML custom attribute 'andesButtonSize'.
     * @param andesButtonHierarchy one of the elements inside [AndesButtonHierarchy]. Will determine the hierarchy of the button.
     * This is the programmatic equivalent of the XML custom attribute 'andesButtonHierarchy'.
     * @param andesButtonIcon has the necessary info about the icon to draw.
     * @return an [AndesButtonConfiguration] that contains all the data that [AndesButton] needs to draw itself properly.
     */

    @Suppress("LongParameterList")
    @Override
    fun create(
        context: Context,
        andesButtonSize: AndesButtonSize,
        andesButtonHierarchy: AndesButtonHierarchy,
        andesButtonIcon: AndesButtonIcon?,
        andesButtonLeftDrawable: Drawable?,
        andesButtonRightDrawable: Drawable?
    ): AndesButtonConfiguration {
        val size = andesButtonSize.size
        val hierarchy = andesButtonHierarchy.hierarchy

        return AndesButtonConfiguration(
            background = resolveBackground(hierarchy, size, context),
            textColor = resolveTextColor(hierarchy, context),
            textSize = resolveTextSize(size, context),
            margin = resolveMargin(size,
                andesButtonIcon?.leftIcon, andesButtonIcon?.rightIcon, andesButtonLeftDrawable, andesButtonRightDrawable, context),
            height = resolveHeight(size, context),
            typeface = resolveTypeface(hierarchy, context),
            iconConfig = resolveIconConfig(size,
                hierarchy, andesButtonIcon?.leftIcon, andesButtonIcon?.rightIcon, andesButtonLeftDrawable, andesButtonRightDrawable, context),
            lateralPadding = resolveLateralPadding(size, context)
        )
    }

    /**
     * Determines the background to be used from certain parameters that receives.
     *
     * @param hierarchy determined hierarchy of the button: Needed because the color depends on this.
     * @param size determined size of the button: Needed because the size depends on this.
     * @param context needed for accessing some resources.
     */
    private fun resolveBackground(
        hierarchy: AndesButtonHierarchyInterface,
        size: AndesButtonSizeInterface,
        context: Context
    ) = hierarchy.background(context, size.cornerRadius(context))

    /**
     * Determines the text color from certain parameters that receives.
     *
     * @param hierarchy determined hierarchy of the button: Needed because the color depends on this.
     * @param context needed for accessing some resources.
     */
    private fun resolveTextColor(hierarchy: AndesButtonHierarchyInterface, context: Context) = hierarchy.textColor(context)

    /**
     * Determines the text size from certain parameters that receives.
     *
     * @param size determined size of the button: Needed because the text size pends on this.
     * @param context needed for accessing some resources.
     */
    private fun resolveTextSize(size: AndesButtonSizeInterface, context: Context) = size.textSize(context)

    /**
     * Determines the margins of the button.
     * Takes into account key things like the size and the presence of an icon.
     *
     * @param size determined size of the button:
     * Needed because the margins are different for each size.
     * @param leftIconPath probable icon path of the button.
     * Needed because the margins are different if the button has icon or not.
     * @param rightIconPath probable icon path of the button.
     * Needed because the margins are different if the button has icon or not.
     * @param context needed for accessing dimen resources.
     */
    private fun resolveMargin(
        size: AndesButtonSizeInterface,
        leftIconPath: String?,
        rightIconPath: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ) = AndesButtonMargin(size, leftIconPath, rightIconPath, context, leftDrawable, rightDrawable)

    /**
     * Determines the height of the button from certain parameters that receives.
     *
     * @param size determined size of the button: Needed because the height depends on this.
     * @param context needed for accessing some resources.
     */
    private fun resolveHeight(size: AndesButtonSizeInterface, context: Context) =
        size.height(context)

    /**
     * Determines the [Typeface] from certain parameters that receives.
     *
     * @param hierarchy determined hierarchy of the button:
     * Needed because hierarchy is in charge of solving this.
     * @param context needed for accessing font resources.
     */
    private fun resolveTypeface(hierarchy: AndesButtonHierarchyInterface, context: Context) =
        hierarchy.typeface(context)

    /**
     * Determines the [IconConfig] from certain parameters that receives.
     *
     * @param size determined size of the button:
     * Needed because having icon or not depends on this.
     * @param hierarchy determined hierarchy of the button: Needed because
     * hierarchy provides the color of the icon.
     * @param leftIconPath determined icon path of the button.
     * Needed because this is the icon to be resized and tinted
     * to be used properly inside the button.
     * @param rightIconPath determined icon path of the button.
     * Needed because this is the icon to be resized and tinted
     * to be used properly inside the button.
     * @param context needed for accessing some resources.
     */
    private fun resolveIconConfig(
        size: AndesButtonSizeInterface,
        hierarchy: AndesButtonHierarchyInterface,
        leftIconPath: String?,
        rightIconPath: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ) = size.iconConfig(hierarchy, leftIconPath, rightIconPath, leftDrawable, rightDrawable, context)

    /**
     * Determines the padding of the button.
     *
     * @param size determined size of the button: Needed because the paddings
     * are different for each size.
     * @param context needed for accesing dimen resources.
     */
    private fun resolveLateralPadding(size: AndesButtonSizeInterface, context: Context) = size.lateralPadding(context)
}
