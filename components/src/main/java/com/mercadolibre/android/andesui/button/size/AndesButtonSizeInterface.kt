package com.mercadolibre.android.andesui.button.size

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.button.factory.IconConfig
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchyInterface
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIconOrientation
import com.mercadolibre.android.andesui.icons.IconProvider
import com.mercadolibre.android.andesui.utils.buildColoredAndesBitmapDrawable
import java.io.FileNotFoundException

/**
 * Defines all size related properties that an [AndesButton] needs to be drawn properly.
 * Those properties change depending on the size of the button.
 *
 */
@Suppress("TooManyFunctions")
internal interface AndesButtonSizeInterface {
    /**
     * Returns a [Float] representing the text size to be used.
     *
     * @param context needed for accessing some resources.
     * @return a [Float] representing the text size to be used.
     */
    fun textSize(context: Context): Float

    /**
     * Returns a [Float] representing the height size to be used.
     *
     * @param context needed for accessing some resources.
     * @return a [Float] representing the height size to be used.
     */
    fun height(context: Context): Float

    /**
     * Returns an [Int] representing the left margin to be used.
     *
     * @param context needed for accessing some resources.
     * @return an [Int] representing the left margin to be used.
     */
    fun textLeftMargin(context: Context): Int

    /**
     * Returns an [Int] representing the right margin to be used.
     *
     * @param context needed for accessing some resources.
     * @return an [Int] representing the right margin to be used.
     */
    fun textRightMargin(context: Context): Int

    /**
     * Returns the left margin that the icon should have in its left side.
     * 0 by default.
     *
     * @param context needed for accessing some dimen resources.
     */
    fun leftIconLeftMargin(context: Context) = 0

    /**
     * Returns the left margin that the icon should have in its right side.
     * 0 by default.
     *
     * @param context needed for accessing some dimen resources.
     */
    fun leftIconRightMargin(context: Context) = 0

    /**
     * Returns the right margin that the icon should have in its left side.
     * 0 by default.
     *
     * @param context needed for accessing some dimen resources.
     */
    fun rightIconLeftMargin(context: Context) = 0

    /**
     * Returns the right margin that the icon should have in its right side.
     * 0 by default.
     *
     * @param context needed for accessing some dimen resources.
     */
    fun rightIconRightMargin(context: Context) = 0

    fun lateralPadding(context: Context): Int

    /**
     * Returns a [Float] representing the corner radius to be used.
     *
     * @param context needed for accessing some resources.
     * @return a [Float] representing the corner radius to be used.
     */
    fun cornerRadius(context: Context): Float

    /**
     * Returns an [IconConfig] that holds the icons to be used in the button.
     *
     *
     * @param hierarchy determined hierarchy of the button:
     * Needed because hierarchy provides the color of the icon.
     * @param leftIcon determined icon of the button.
     * Needed because this is the icon to be resized and tinted
     * to be used properly inside the button.
     * @param rightIcon determined icon of the button.
     * Needed because this is the icon to be resized and tinted
     * to be used properly inside the button.
     * @param context needed for accessing some resources.
     * @return an [IconConfig] that holds the icons to be used in the button.
     */
    @Suppress("LongParameterList")
    fun iconConfig(
        hierarchy: AndesButtonHierarchyInterface,
        leftIcon: String?,
        rightIcon: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ): IconConfig?

    /**
     * Checks if the current size can display an icon.
     * Returns false by default.
     *
     * @return true if can display an icon, false otherwise.
     */
    fun canDisplayIcon(): Boolean = false
}

/**
 * Implementation of [AndesButtonSizeInterface] that returns the required data but personalized for the Large Size,
 * according to Andes specifications.
 *
 */
@Suppress("TooManyFunctions")
internal class AndesLargeButtonSize : AndesButtonSizeInterface {

    override fun textSize(context: Context) =
        context.resources.getDimension(R.dimen.andes_text_size_button_large)

    override fun height(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_height_large)

    override fun textLeftMargin(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_margin_large).toInt()

    override fun textRightMargin(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_margin_large).toInt()

    override fun leftIconRightMargin(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_left_icon_right_margin).toInt()

    override fun rightIconLeftMargin(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_right_icon_left_margin).toInt()

    override fun lateralPadding(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_lateral_padding_large).toInt()

    override fun cornerRadius(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_border_radius_large)

    override fun iconConfig(
        hierarchy: AndesButtonHierarchyInterface,
        leftIcon: String?,
        rightIcon: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ): IconConfig? {

        return when {
            leftIcon != null -> { // Ignoring if rightIcon is also non null: Left icon has higher precedence than right
                handlePathIcon(context, leftIcon, hierarchy, AndesButtonIconOrientation.LEFT)
            }
            rightIcon != null -> {
                handlePathIcon(context, rightIcon, hierarchy, AndesButtonIconOrientation.RIGHT)
            }
            leftDrawable != null -> {
                handleIcon(context, leftDrawable, hierarchy, AndesButtonIconOrientation.LEFT)
            }
            rightDrawable != null -> {
                handleIcon(context, rightDrawable, hierarchy, AndesButtonIconOrientation.RIGHT)
            }
            else -> {
                null // No icon has been specified
            }
        }
    }

    private fun handleIcon(context: Context, icon: Drawable,
        hierarchy: AndesButtonHierarchyInterface, orientation: AndesButtonIconOrientation): IconConfig? {
        return try {
            val bitmapDrawable = buildColoredAndesBitmapDrawable(
                icon as BitmapDrawable,
                context,
                context.resources.getDimensionPixelSize(R.dimen.andes_button_icon_width),
                context.resources.getDimensionPixelSize(R.dimen.andes_button_icon_height),
                hierarchy.iconColor(context)
            )
            when (orientation) {
                AndesButtonIconOrientation.LEFT -> IconConfig(leftIcon = bitmapDrawable, rightIcon = null)
                AndesButtonIconOrientation.RIGHT -> IconConfig(leftIcon = null, rightIcon = bitmapDrawable)

            }
        } catch (e: FileNotFoundException) {
            IconConfig(leftIcon = null, rightIcon = null)
        }
    }

    private fun handlePathIcon(context: Context, leftIcon: String,
        hierarchy: AndesButtonHierarchyInterface, orientation: AndesButtonIconOrientation): IconConfig? {
        val icon = IconProvider(context)
            .loadIcon(leftIcon)
        icon?.let {
            return when (orientation) {
                AndesButtonIconOrientation.LEFT -> handleIcon(context, icon, hierarchy, AndesButtonIconOrientation.LEFT)
                AndesButtonIconOrientation.RIGHT -> handleIcon(context, icon, hierarchy, AndesButtonIconOrientation.RIGHT)
            }
        }
        return null
    }

    override fun canDisplayIcon() = true
}

/**
 * Implementation of [AndesButtonSizeInterface] that returns the required data
 * but personalized for the Medium Size,
 * according to Andes specifications.
 *
 */
@Suppress("TooManyFunctions")
internal class AndesMediumButtonSize : AndesButtonSizeInterface {
    override fun textSize(context: Context) =
        context.resources.getDimension(R.dimen.andes_text_size_button_medium)

    override fun height(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_height_medium)

    override fun textLeftMargin(context: Context) = 0
    override fun textRightMargin(context: Context) = 0
    override fun lateralPadding(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_lateral_padding_medium).toInt()

    override fun cornerRadius(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_border_radius_medium)

    override fun iconConfig(
        hierarchy: AndesButtonHierarchyInterface,
        leftIcon: String?,
        rightIcon: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ): Nothing? = null
}

/**
 * Implementation of [AndesButtonSizeInterface] that returns the required data but personalized for the Small Size,
 * according to Andes specifications.
 *
 */
@Suppress("TooManyFunctions")
internal class AndesSmallButtonSize : AndesButtonSizeInterface {
    override fun textSize(context: Context) =
        context.resources.getDimension(R.dimen.andes_text_size_button_small)

    override fun height(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_height_small)

    override fun textLeftMargin(context: Context) = 0
    override fun textRightMargin(context: Context) = 0
    override fun lateralPadding(context: Context) = context.resources.getDimension(R.dimen.andes_button_lateral_padding_small).toInt()
    override fun cornerRadius(context: Context) =
        context.resources.getDimension(R.dimen.andes_button_border_radius_small)

    override fun iconConfig(
        hierarchy: AndesButtonHierarchyInterface,
        leftIcon: String?,
        rightIcon: String?,
        leftDrawable: Drawable?,
        rightDrawable: Drawable?,
        context: Context
    ): Nothing? = null
}
