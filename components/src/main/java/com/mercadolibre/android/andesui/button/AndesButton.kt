package com.mercadolibre.android.andesui.button

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.factory.AndesButtonAttrs
import com.mercadolibre.android.andesui.button.factory.AndesButtonAttrsParser
import com.mercadolibre.android.andesui.button.factory.AndesButtonConfiguration
import com.mercadolibre.android.andesui.button.factory.AndesButtonConfigurationFactory
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonHierarchy
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIcon
import com.mercadolibre.android.andesui.button.hierarchy.AndesButtonIconOrientation
import com.mercadolibre.android.andesui.button.hierarchy.BackgroundColorConfig
import com.mercadolibre.android.andesui.button.hierarchy.getConfiguredBackground
import com.mercadolibre.android.andesui.button.size.AndesButtonSize
import com.mercadolibre.android.andesui.progress.AndesProgressIndicatorIndeterminate
import com.mercadolibre.android.andesui.progress.size.AndesProgressSize
import kotlinx.android.parcel.Parcelize

/**
 * User interface element the user can tap or click to perform an action.
 * Has all the same features as an [AppCompatButton] but reinforces the Andes style.
 *
 * Is compatible to use via code or via XML.
 * If you use it via code then you have several options, like not providing any parameter,
 * providing one of the possibilities [AndesButtonHierarchy] has to offer,
 * providing one of the many [AndesButtonSize] availables, etc.
 * Also you can set an icon via code.
 *
 * If your desire is to use AndesButton via XML, then we got you covered too!
 * Let's take a look:
 *
 *
 * <pre>
 * &lt;com.mercadolibre.android.andesui.button.AndesButton
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     android:layout_marginBottom="16dp"
 *     android:text="@string/large_button_left_icon"
 *     app:andesButtonLeftIconPath="andes_icon_clip"
 *     app:andesButtonSize="large"
 *     app:andesButtonHierarchy="loud" /&gt;</pre>
 *
 *
 * You can also via XML or via code setup an [android.view.View.OnClickListener] as you'd do with any Button.

 * Enabling/disabling this button is also supported.
 *
 *
 * This AndesButton relies heavily in a configuration created by [AndesButtonConfigurationFactory] from
 * its attributes. Some values of this configuration can be updated programmatically, like [text] and [hierarchy],
 * by accessing its related setters
 *
 */
@Suppress("TooManyFunctions")
class AndesButton : ConstraintLayout {

    private lateinit var andesButtonAttrs: AndesButtonAttrs
    internal lateinit var textComponent: TextView
    internal lateinit var loadingView: AndesProgressIndicatorIndeterminate

    lateinit var leftIconComponent: SimpleDraweeView
    lateinit var rightIconComponent: SimpleDraweeView

    private var customIcon = false

    /**
     * Getter and setter for [text].
     */
    var text: String?
        get() = andesButtonAttrs.andesButtonText
        set(value) {
            andesButtonAttrs = andesButtonAttrs.copy(andesButtonText = value)
            textComponent.text = andesButtonAttrs.andesButtonText
        }

    /**
     * Getter and setter for [hierarchy].
     */
    var hierarchy: AndesButtonHierarchy
        get() = andesButtonAttrs.andesButtonHierarchy
        set(value) {
            andesButtonAttrs = andesButtonAttrs.copy(andesButtonHierarchy = value)
            createConfig().also {
                updateDynamicComponents(it)
                updateComponentsAlignment(it)
            }
        }

    /**
     * Getter and setter for [size].
     */
    var size: AndesButtonSize
        get() = andesButtonAttrs.andesButtonSize
        set(value) {
            andesButtonAttrs = andesButtonAttrs.copy(andesButtonSize = value)
            createConfig().also {
                setupHeight(it)
                updateDynamicComponents(it)
                updateComponentsAlignment(it)
            }
        }

    /**
     * Getter and setter for [isLoading].
     */
    var isLoading: Boolean
        get() = loadingView.visibility == View.VISIBLE
        set(value) {
            andesButtonAttrs = andesButtonAttrs.copy(andesButtonIsLoading = value)
            createConfig().also {
                updateComponentsAlignment(it)
                updateDynamicComponents(it)
            }
        }

    init {
        isSaveEnabled = true
    }

    /**
     * Simplest constructor for creating an AndesButton programmatically.
     * Builds an AndesButton with Large Size and Hierarchy Loud by default.
     */
    constructor(context: Context) : super(context) {
        initAttrs(SIZE_DEFAULT, HIERARCHY_DEFAULT, ICON_DEFAULT, TEXT_DEFAULT)
    }

    /**
     * Constructor for creating an AndesButton programmatically with the specified [buttonSize],
     * and optionally [buttonIcon] and [buttonText].
     */
    constructor(
        context: Context,
        buttonSize: AndesButtonSize = SIZE_DEFAULT,
        buttonHierarchy: AndesButtonHierarchy = HIERARCHY_DEFAULT,
        buttonIcon: AndesButtonIcon? = ICON_DEFAULT,
        buttonText: String? = TEXT_DEFAULT
    ) :
        super(context) {
        initAttrs(buttonSize, buttonHierarchy, buttonIcon, buttonText)
    }

    /**
     * Constructor for creating an AndesButton via XML.
     * The [attrs] are the attributes specified in the parameters of XML.
     *
     * Hope you are using the parameters specified in attrs.xml
     * file: andesButtonHierarchy, andesButtonSize, andesButtonLeftIconCustom, etc.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
    }

    /**
     * Constructor for creating an AndesButton via XML.
     * The [attrs] are the attributes specified in the parameters of XML.
     * The [defStyleAttr] is not considered because we take care of all Andes styling for you.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs)

    /**
     * Sets the proper configuration for this button based on the [attrs] received via XML.
     *
     * @param attrs attributes from the XML.
     */
    private fun initAttrs(attrs: AttributeSet?) {
        andesButtonAttrs = AndesButtonAttrsParser.parse(context, attrs)
        setupComponents(createConfig())
    }

    /**
     * Sets the proper configuration for this button based on the [buttonSize] and [buttonHierarchy] received.
     * This method will be called when this button is created programmatically.
     *
     * @param buttonSize one of the sizes available in [AndesButtonSize]
     * @param buttonHierarchy one of the hierarchies available in [AndesButtonHierarchy]
     * @param buttonIcon contains the data needed to draw an icon on the button.
     */
    private fun initAttrs(
        buttonSize: AndesButtonSize,
        buttonHierarchy: AndesButtonHierarchy,
        buttonIcon: AndesButtonIcon?,
        text: String?
    ) {
        andesButtonAttrs = AndesButtonAttrs(buttonHierarchy,
            buttonSize,
            buttonIcon?.leftIcon,
            buttonIcon?.rightIcon,
            text)
        setupComponents(createConfig())
    }

    /**
     * Responsible for setting up all properties of each component that is part of this button.
     * Is like a choreographer ;)
     *
     */
    private fun setupComponents(config: AndesButtonConfiguration) {
        initComponents()

        setupViewId()
        setupViewAsClickable()
        setupEnabledView(config)
        setupHeight(config)

        updateDynamicComponents(config)
        setupIsLoadingView(config)

        addView(loadingView)
        addView(textComponent)
        addView(leftIconComponent)
        addView(rightIconComponent)

        updateComponentsAlignment(config)
    }

    /**
     * Responsible for update all properties related to components that can change dynamically
     *
     */
    private fun updateDynamicComponents(config: AndesButtonConfiguration) {
        setupTextComponent(config)
        setupLeftIconComponent(config)
        setupRightIconComponent(config)
        setupLoadingComponent(config)

        background = config.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stateListAnimator = null
        }
    }

    /**
     * Configures the constraints for this button.
     *
     */
    private fun setupConstraints(config: AndesButtonConfiguration) {
        val set = ConstraintSet()
        set.clone(this)
        set.createHorizontalChain(
            ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
            intArrayOf(leftIconComponent.id, textComponent.id, rightIconComponent.id),
            null,
            ConstraintSet.CHAIN_PACKED
        )

        set.centerVertically(leftIconComponent.id, ConstraintSet.PARENT_ID)

        set.centerVertically(textComponent.id, ConstraintSet.PARENT_ID)
        set.setMargin(textComponent.id, ConstraintSet.START, config.margin.iconRightMargin)
        set.setGoneMargin(textComponent.id, ConstraintSet.START, config.margin.textLeftMargin)
        set.setMargin(textComponent.id, ConstraintSet.END, config.margin.iconLeftMargin)
        set.setGoneMargin(textComponent.id, ConstraintSet.END, config.margin.textRightMargin)

        set.centerVertically(rightIconComponent.id, ConstraintSet.PARENT_ID)

        set.centerVertically(loadingView.id, ConstraintSet.PARENT_ID)
        set.centerHorizontally(loadingView.id, ConstraintSet.PARENT_ID)

        set.applyTo(this)
    }

    /**
     * Creates all the views that are part of this button.
     * After a view is created then a view id is added to it.
     *
     */
    private fun initComponents() {
        textComponent = TextView(context)
        textComponent.id = View.generateViewId()
        leftIconComponent = SimpleDraweeView(context)
        leftIconComponent.id = View.generateViewId()
        rightIconComponent = SimpleDraweeView(context)
        rightIconComponent.id = View.generateViewId()
        loadingView = AndesProgressIndicatorIndeterminate(context)
        loadingView.id = View.generateViewId()
    }

    /**
     * Sets a view id to this button.
     *
     */
    private fun setupViewId() {
        if (id == NO_ID) { // If this view has no id
            id = View.generateViewId()
        }
    }

    /**
     * Makes sure that this button can receive touch events.
     *
     */
    private fun setupViewAsClickable() {
        isClickable = true
        isFocusable = true
    }

    /**
     * Sets this button enabled or disabled based on the current config.
     *
     */
    private fun setupEnabledView(config: AndesButtonConfiguration) {
        isEnabled = config.enabled
    }

    /**
     * Sets this button show loading or not based on the current config.
     *
     */
    private fun setupIsLoadingView(config: AndesButtonConfiguration) {
        isLoading = config.isLoading
    }

    /**
     * Sets the height of this button.
     *
     */
    private fun setupHeight(config: AndesButtonConfiguration) {
        minHeight = config.height.toInt()
        maxHeight = config.height.toInt()
    }

    /**
     * Gets data from the config and sets to the text component of this button.
     *
     */
    private fun setupTextComponent(config: AndesButtonConfiguration) {
        textComponent.text = config.text
        textComponent.maxLines = config.maxLines
        textComponent.isAllCaps = false
        textComponent.setTextSize(TypedValue.COMPLEX_UNIT_PX, config.textSize)
        textComponent.setTextColor(config.textColor)
        textComponent.typeface = config.typeface
        textComponent.ellipsize = TextUtils.TruncateAt.END
    }

    /**
     * Gets data from the config and sets to the left icon component of this button.
     * If this button has no left icon then hides it.
     *
     */
    private fun setupLeftIconComponent(config: AndesButtonConfiguration) {
        if (!customIcon)
            leftIconComponent.setImageDrawable(config.leftIcon)

        if (config.leftIcon == null && !customIcon) {
            leftIconComponent.visibility = View.GONE
        }
    }

    /**
     * Gets data from the config and sets to the right icon component of this button.
     * If this button has no right icon then hides it.
     *
     */
    private fun setupRightIconComponent(config: AndesButtonConfiguration) {
        if (!customIcon)
            rightIconComponent.setImageDrawable(config.rightIcon)

        if (config.rightIcon == null && !customIcon) {
            rightIconComponent.visibility = View.GONE
        }
    }

    /**
     * Sets the paddings of the button.
     */
    private fun setupPaddings(config: AndesButtonConfiguration) {
        setPadding(config.lateralPadding, paddingTop, config.lateralPadding, paddingBottom)
    }

    /**
     * Gets data from the config and sets to the loading component of this button.
     *
     */
    private fun setupLoadingComponent(config: AndesButtonConfiguration) {
        if (config.isLoading) {

            loadingView.size = AndesProgressSize.fromString(size.name)
            loadingView.tint = config.textColor.getColorForState(drawableState, 0)

            textComponent.visibility = View.INVISIBLE
            loadingView.visibility = View.VISIBLE
            leftIconComponent.visibility = View.INVISIBLE
            rightIconComponent.visibility = View.INVISIBLE

            loadingView.start()
        } else {
            textComponent.visibility = View.VISIBLE
            loadingView.visibility = View.GONE
            leftIconComponent.visibility = View.VISIBLE
            rightIconComponent.visibility = View.VISIBLE
        }
    }

    /**
     * Responsible to update components positions and constraints based on the current configuration
     */
    private fun updateComponentsAlignment(config: AndesButtonConfiguration) {
        setupConstraints(config)
        setupPaddings(config)
    }

    private fun createConfig() = AndesButtonConfigurationFactory.create(context, andesButtonAttrs)

    /**
     * Set the enabled type of this button and its children views.
     *
     * @param enabled true if this view is enabled, false otherwise.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        textComponent.isEnabled = enabled
        leftIconComponent.isEnabled = enabled
        rightIconComponent.isEnabled = enabled
    }

    internal fun changeTextColor(color: Int) {
        textComponent.setTextColor(color)
    }

    internal fun changeBackgroundColor(backgroundColorConfig: BackgroundColorConfig) {
        background = getConfiguredBackground(context,
            context.resources.getDimension(R.dimen.andes_button_border_radius_medium),
            backgroundColorConfig)
    }

    /**
     * Save the current button status
     */
    override fun onSaveInstanceState(): Parcelable? {
        val superState: Parcelable = super.onSaveInstanceState() ?: Bundle()
        return SavedState(isLoading, superState)
    }

    /**
     * Restore button status
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            isLoading = state.isLoading
            super.onRestoreInstanceState(state.superState)
            return
        }
        super.onRestoreInstanceState(state)
    }

    /**
     * Load custom button icon using another strategy
     */
    fun loadCustomButtonIcon(
        pipelineDraweeControllerBuilder: PipelineDraweeControllerBuilder,
        leftIconPosition: Boolean = true
    ) {
        customIcon = true
        var icon: SimpleDraweeView = leftIconComponent
        andesButtonAttrs = andesButtonAttrs.copy(andesButtonLeftIconPath = CUSTOM_ICON_DEFAULT)

        if (!leftIconPosition) {
            icon = rightIconComponent
            andesButtonAttrs = andesButtonAttrs.copy(andesButtonLeftIconPath = null,
                andesButtonRightIconPath = CUSTOM_ICON_DEFAULT)
        }

        val listener: ControllerListener<ImageInfo> = object : BaseControllerListener<ImageInfo>() {
            override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {
                updateIconMargin(imageInfo, icon)
            }

            override fun onFinalImageSet(id: String?, imageInfo: ImageInfo?, animatable: Animatable?) {
                updateIconMargin(imageInfo, icon)
            }
        }

        val controller = pipelineDraweeControllerBuilder
            .setControllerListener(listener)
            .build()

        icon.controller = controller
        icon.visibility = View.VISIBLE

        createConfig().also {
            updateDynamicComponents(it)
            updateComponentsAlignment(it)
        }
    }

    /**
     * update margen for custom icon
     */
    private fun updateIconMargin(@Nullable imageInfo: ImageInfo?, simpleDraweeView: SimpleDraweeView) {

        if (imageInfo != null) {
            val iconWidth = context.resources.getDimensionPixelSize(R.dimen.andes_button_icon_width)
            val iconHeight = context.resources.getDimensionPixelSize(R.dimen.andes_button_icon_height)
            simpleDraweeView.layoutParams.width = iconWidth
            simpleDraweeView.layoutParams.height = iconHeight
            simpleDraweeView.aspectRatio = iconWidth.toFloat() / iconHeight
        }

        createConfig().also {
            updateComponentsAlignment(it)
        }
    }

    /**
     * sets an icon to right or left, depending on the orientation
     * @param drawable the drawable that is going to be shown
     * @param orientation AndesButtonIconOrientation LEFT|RIGHT
     */
    fun setIconDrawable(drawable: Drawable, orientation: AndesButtonIconOrientation) {
        andesButtonAttrs = when (orientation) {
            AndesButtonIconOrientation.LEFT -> andesButtonAttrs.copy(andesButtonLeftDrawable = drawable)
            AndesButtonIconOrientation.RIGHT -> andesButtonAttrs.copy(andesButtonRightDrawable = drawable)
        }

        createConfig().also {
            updateDynamicComponents(it)
            updateComponentsAlignment(it)
        }
    }

    /**
     * Default values for AndesButton basic properties
     */
    companion object {
        private const val TEXT_DEFAULT = "Button text"
        private val HIERARCHY_DEFAULT = AndesButtonHierarchy.LOUD
        private val SIZE_DEFAULT = AndesButtonSize.LARGE
        private val ICON_DEFAULT = null
        private const val CUSTOM_ICON_DEFAULT = "andesui_icon"
    }

    @Parcelize
    data class SavedState(
        var isLoading: Boolean,
        var superState: Parcelable
    ) : Parcelable
}
