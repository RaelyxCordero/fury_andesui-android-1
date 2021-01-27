package com.mercadolibre.android.andesui.button.factory

import android.content.Context
import android.graphics.drawable.Drawable
import com.mercadolibre.android.andesui.button.size.AndesButtonSizeInterface

/**
 * Class that retrieves all the necessary data related to margins.
 * Those margins can be calculated thanks to the params received.
 *
 * @constructor
 * Builds a new AndesButtonMargin and immediately calculates the margins so the invoker can consult them.
 *
 * @param size determined size of the button: Needed because the margins are different for each size.
 * @param leftIcon probable icon of the button. Needed because the margins are different if the button has icon or not.
 * @param rightIcon probable icon of the button. Needed because the margins are different if the button has icon or not.
 * @param context needed for accessing dimen resources.
 */
internal class AndesButtonMargin(
    private val size: AndesButtonSizeInterface,
    private val leftIcon: String?,
    private val rightIcon: String?,
    private val context: Context,
    private val leftDrawable: Drawable?,
    private val rightDrawable: Drawable?
) {

    var textLeftMargin: Int = 0
        private set
    var textRightMargin: Int = 0
        private set
    var iconLeftMargin: Int = 0
        private set
    var iconRightMargin: Int = 0
        private set

    init {
        when {
            hasLeftIcon() -> configureLeftIconValues()
            hasRightIcon() -> configureRightIconValues()
            else -> configureDefaultValues()
        }
    }

    private fun hasLeftIcon() = size.canDisplayIcon() && (leftIcon != null || leftDrawable != null)
    private fun hasRightIcon() = size.canDisplayIcon() && (rightIcon != null || rightDrawable != null)

    private fun configureLeftIconValues() {
        textLeftMargin = 0
        textRightMargin = size.textRightMargin(context)
        iconRightMargin = size.leftIconRightMargin(context)
    }

    private fun configureRightIconValues() {
        textLeftMargin = size.textLeftMargin(context)
        textRightMargin = 0
        iconLeftMargin = size.rightIconLeftMargin(context)
    }

    private fun configureDefaultValues() {
        textLeftMargin = size.textLeftMargin(context)
        textRightMargin = size.textRightMargin(context)
    }
}
