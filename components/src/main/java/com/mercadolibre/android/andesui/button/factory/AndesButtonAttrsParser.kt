package com.mercadolibre.android.andesui.button.factory

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.size.AndesButtonSize

/**
 * The data class that contains the public components of the button.
 */
internal data class AndesButtonAttrs(
    val andesButtonHierarchy: AndesButtonHierarchy,
    val andesButtonSize: AndesButtonSize,
    val andesButtonLeftIconPath: String?,
    val andesButtonRightIconPath: String?,
    val andesButtonText: String?,
    val andesButtonEnabled: Boolean = true,
    val andesButtonIsLoading: Boolean = false,
    val andesButtonLeftDrawable: Drawable? = null,
    val andesButtonRightDrawable: Drawable? = null
)

/**
 * This object parse the attribute set and return an instance of AndesButtonAttrs to be used by AndesButton
 */
internal object AndesButtonAttrsParser {

    /**
     * These constants are defined in the attrs XML file.
     * They are just arbitrary values that were given to different params that can be received
     * through custom params.
     */
    private const val ANDES_BUTTON_HIERARCHY_LOUD = "100"
    private const val ANDES_BUTTON_HIERARCHY_QUIET = "101"
    private const val ANDES_BUTTON_HIERARCHY_TRANSPARENT = "102"
    private const val ANDES_BUTTON_SIZE_LARGE = "200"
    private const val ANDES_BUTTON_SIZE_MEDIUM = "201"
    private const val ANDES_BUTTON_SIZE_SMALL = "202"

    /**
     * Reads some properties of the [attr] and returns the [AndesButtonAttrs] with their parsed data.
     *
     * @param context needed for accessing some resources.
     * @param attr list of the attributes received. Main focus will be in the custom params defined in attrs.
     * @return [AndesButtonAttrs] that contains all the data that [AndesButtonConfigurationFactory]
     * needs to create its configuration.
     */
    fun parse(context: Context, attr: AttributeSet?): AndesButtonAttrs {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.AndesButton)

        val hierarchy = when (typedArray.getString(R.styleable.AndesButton_andesButtonHierarchy)) {
            ANDES_BUTTON_HIERARCHY_LOUD -> AndesButtonHierarchy.LOUD
            ANDES_BUTTON_HIERARCHY_QUIET -> AndesButtonHierarchy.QUIET
            ANDES_BUTTON_HIERARCHY_TRANSPARENT -> AndesButtonHierarchy.TRANSPARENT
            else -> AndesButtonHierarchy.LOUD
        }

        val size = when (typedArray.getString(R.styleable.AndesButton_andesButtonSize)) {
            ANDES_BUTTON_SIZE_LARGE -> AndesButtonSize.LARGE
            ANDES_BUTTON_SIZE_MEDIUM -> AndesButtonSize.MEDIUM
            ANDES_BUTTON_SIZE_SMALL -> AndesButtonSize.SMALL
            else -> AndesButtonSize.LARGE
        }

        return AndesButtonAttrs(
                andesButtonHierarchy = hierarchy,
                andesButtonSize = size,
                andesButtonLeftIconPath = typedArray.getString(R.styleable.AndesButton_andesButtonLeftIconPath),
                andesButtonRightIconPath = typedArray.getString(R.styleable.AndesButton_andesButtonRightIconPath),
                andesButtonEnabled = typedArray.getBoolean(R.styleable.AndesButton_andesButtonEnabled, true),
                andesButtonText = typedArray.getString(R.styleable.AndesButton_andesButtonText),
                andesButtonIsLoading = typedArray.getBoolean(R.styleable.AndesButton_andesButtonIsLoading, false)
        ).also { typedArray.recycle() }
    }
}
