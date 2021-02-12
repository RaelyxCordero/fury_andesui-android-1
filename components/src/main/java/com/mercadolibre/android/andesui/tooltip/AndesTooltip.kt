package com.mercadolibre.android.andesui.tooltip

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.facebook.drawee.view.SimpleDraweeView
import com.mercadolibre.android.andesui.R
import com.mercadolibre.android.andesui.button.AndesButton
import com.mercadolibre.android.andesui.databinding.AndesLayoutTooltipBinding
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipAction
import com.mercadolibre.android.andesui.tooltip.actions.AndesTooltipLinkAction
import com.mercadolibre.android.andesui.tooltip.factory.AndesTooltipAttrs
import com.mercadolibre.android.andesui.tooltip.factory.AndesTooltipConfiguration
import com.mercadolibre.android.andesui.tooltip.factory.AndesTooltipConfigurationFactory
import com.mercadolibre.android.andesui.tooltip.style.AndesTooltipStyle
import com.mercadolibre.android.andesui.tooltip.extensions.displaySize
import com.mercadolibre.android.andesui.tooltip.extensions.isFinishing
import com.mercadolibre.android.andesui.tooltip.extensions.visible
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocation
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocationConfig
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocationInterface
import com.mercadolibre.android.andesui.tooltip.location.getAndesTooltipLocationConfig
import com.mercadolibre.android.andesui.tooltip.radius.RadiusLayout
import com.mercadolibre.android.andesui.typeface.getFontOrDefault

class AndesTooltip(val context: Context): LifecycleObserver, AndesTooltipLocationInterface {

    var title: String?
        get() = andesTooltipAttrs.title
        set(value) {
            andesTooltipAttrs = andesTooltipAttrs.copy(title = value)
            initTooltipTitle(createConfig(andesTooltipAttrs))
        }

    var body: String
        get() = andesTooltipAttrs.body
        set(value) {
            andesTooltipAttrs = andesTooltipAttrs.copy(body = value)
            initTooltipBody(createConfig(andesTooltipAttrs))
        }

    var isDismissible: Boolean
        get() = andesTooltipAttrs.isDismissible
        set(value) {
            andesTooltipAttrs = andesTooltipAttrs.copy(isDismissible = value)
            initDismiss(createConfig(andesTooltipAttrs))
        }

    var style: AndesTooltipStyle
        get() = andesTooltipAttrs.style
        set(value) {
            andesTooltipAttrs = andesTooltipAttrs.copy(style = value)
            setupComponents(createConfig(andesTooltipAttrs), andesTooltipLocationConfigRequired)
        }

    private lateinit var andesTooltipAttrs: AndesTooltipAttrs
    private lateinit var andesTooltipLocationConfigRequired: AndesTooltipLocationConfig

    private lateinit var constraintContainer: ConstraintLayout
    private lateinit var titleComponent: TextView
    private lateinit var bodyComponent: TextView
    private lateinit var dismissComponent: SimpleDraweeView
    private lateinit var primaryActionComponent: AndesButton
    private lateinit var secondaryActionComponent: AndesButton
    private lateinit var linkActionComponent: TextView
    private lateinit var arrowComponent: AppCompatImageView

    private val bodyWindow: PopupWindow
    private var lifecycleOwner: LifecycleOwner? = null
    private var isShowing = false
    private var destroyed: Boolean = false


    private val container: AndesLayoutTooltipBinding =
            AndesLayoutTooltipBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        bodyWindow = PopupWindow(
                container.root,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        )
        setLifecycleOwner()
        adjustFitsSystemWindows(container.root)
    }

    @JvmOverloads
    constructor(context: Context,
                style: AndesTooltipStyle = STYLE_DEFAULT,
                title: String? = TITLE_DEFAULT,
                body: String,
                isDismissible: Boolean = IS_DISMISSIBLE_DEFAULT,
                tooltipLocation: AndesTooltipLocation = TIP_ORIENTATION_DEFAULT,
                mainAction: AndesTooltipAction,
                secondaryAction: AndesTooltipAction? = SECONDARY_ACTION_DEFAULT

    ): this(context) {
        andesTooltipAttrs = AndesTooltipAttrs(
                style = style,
                title = title,
                body = body,
                isDismissible = isDismissible,
                tooltipLocation = tooltipLocation,
                mainAction = mainAction,
                secondaryAction = secondaryAction
        )
        initComponents(andesTooltipAttrs)
    }

    @JvmOverloads
    constructor(context: Context,
                style: AndesTooltipStyle = STYLE_DEFAULT,
                title: String? = TITLE_DEFAULT,
                body: String,
                isDismissible: Boolean = IS_DISMISSIBLE_DEFAULT,
                tooltipLocation: AndesTooltipLocation = TIP_ORIENTATION_DEFAULT,
                linkAction: AndesTooltipLinkAction? = LINK_ACTION_DEFAULT

    ): this(context) {
        andesTooltipAttrs = AndesTooltipAttrs(
                style = style,
                title = title,
                body = body,
                isDismissible = isDismissible,
                tooltipLocation = tooltipLocation,
                linkAction = linkAction
        )
        initComponents(andesTooltipAttrs)
    }

    @MainThread
    fun show(target: View) {
        if (!isShowing && !destroyed && !context.isFinishing() && ViewCompat.isAttachedToWindow(target)) {
            target.post {
                container.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                bodyWindow.width = tooltipMeasuredWidth
                bodyWindow.height = tooltipMeasuredHeight
                constraintContainer.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )

                if (!andesTooltipLocationConfigRequired.buildTooltipInRequiredLocation(target)){
                    andesTooltipLocationConfigRequired.iterateOtherLocations(target)
                }

            }
        }
    }

    fun dismiss() {
        if (this.isShowing) {
            this.isShowing = false
            this.bodyWindow.dismiss()
        }
    }

    fun setOnAndesTooltipDismissListener(callback: (()->Unit)? = null) {
        this.bodyWindow.setOnDismissListener {
            this@AndesTooltip.dismiss()
            callback?.invoke()
        }
    }


    private fun initComponents(attrs: AndesTooltipAttrs){
        radiusLayout = container.andesTooltipRadioLayout
        frameLayoutContainer = container.andesTooltipContent
        constraintContainer = container.andesTooltipContainer
        titleComponent = container.andesTooltipTitle
        bodyComponent = container.andesTooltipBody
        dismissComponent = container.andesTooltipDismiss
        primaryActionComponent = container.andesTooltipPrimaryAction
        secondaryActionComponent = container.andesTooltipSecondaryAction
        linkActionComponent = container.andesTooltipLinkAction
        arrowComponent = container.andesTooltipArrow

        setupComponents(createConfig(attrs), andesTooltipLocationConfigRequired)
    }

    private fun createConfig(attrs: AndesTooltipAttrs): AndesTooltipConfiguration {
        val config = AndesTooltipConfigurationFactory.create(context, attrs)
        andesTooltipLocationConfigRequired = getAndesTooltipLocationConfig(this, attrs.tooltipLocation)
        return config
    }

    private fun setLifecycleOwner(){
        if (lifecycleOwner == null && context is LifecycleOwner) {
            lifecycleOwner = context
            context.lifecycle.addObserver(this@AndesTooltip)
        } else {
            lifecycleOwner?.lifecycle?.addObserver(this@AndesTooltip)
        }
    }

    private fun adjustFitsSystemWindows(parent: ViewGroup) {
        parent.fitsSystemWindows = false
        (0 until parent.childCount).map { parent.getChildAt(it) }.forEach { child ->
            child.fitsSystemWindows = false
            if (child is ViewGroup) {
                adjustFitsSystemWindows(child)
            }
        }
    }

    private fun setupComponents(config: AndesTooltipConfiguration, locationConfig: AndesTooltipLocationConfig){
        initializeBackground(config)
        initializeAndesTooltipWindow()
        initializeAndesTooltipContent(config, locationConfig)
    }

    private fun initializeArrow(locationConfig: AndesTooltipLocationConfig) {
        with(arrowComponent) {
            layoutParams = FrameLayout.LayoutParams(arrowWidth, arrowHeight)
            rotation = locationConfig.getArrowRotation()
            alpha = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_alpha).toFloat()
            radiusLayout.post {
                ViewCompat.setElevation(this, this@AndesTooltip.elevation.toFloat())
                val arrowPoint = locationConfig.getArrowPoint()
                x = arrowPoint.x
                y = arrowPoint.y
            }
        }
    }

    private fun initializeBackground(config: AndesTooltipConfiguration) {
        with(radiusLayout) {
            alpha = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_alpha).toFloat()
            ViewCompat.setElevation(this, this@AndesTooltip.elevation.toFloat())
            background = GradientDrawable().apply {
                setColor(config.backgroundColor.colorInt(context))
                cornerRadius = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_corner_radius).toFloat()
            }
        }
    }

    private fun initializeAndesTooltipWindow() {
        with(bodyWindow) {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = this@AndesTooltip.elevation.toFloat()
            }
            isClippingEnabled = false
            setTouchInterceptor(
                    object : View.OnTouchListener {
                        @SuppressLint("ClickableViewAccessibility")
                        override fun onTouch(view: View, event: MotionEvent): Boolean {
                            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                                this@AndesTooltip.dismiss()
                                return true
                            }
                            return false
                        }
                    }
            )
        }
    }

    private fun initializeAndesTooltipContent(config: AndesTooltipConfiguration, locationConfig: AndesTooltipLocationConfig) {
        with(frameLayoutContainer) {
            val paddingByConfig = locationConfig.getTooltipPadding()
            setPadding(paddingByConfig.left, paddingByConfig.top, paddingByConfig.right, paddingByConfig.bottom)
        }
        initTooltipTitle(config)
        initTooltipBody(config)
        initDismiss(config)
        initPrimaryAction(config)
        initSecondaryAction(config)
        initLinkAction(config)
    }

    private fun initTooltipTitle(config: AndesTooltipConfiguration){
        with(titleComponent){
            if (!config.titleText.isNullOrEmpty()){
                text = config.titleText
                typeface = config.titleTypeface
                config.titleTextSize?.let { setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
                setTextColor(config.textColor.colorInt(context))
                visible(true)
            } else {
                visible(false)
            }

        }
    }

    private fun initTooltipBody(config: AndesTooltipConfiguration){
        with(bodyComponent){
            text = config.bodyText
            typeface = config.bodyTypeface
            setTextColor(config.textColor.colorInt(context))
            config.bodyTextSize?.let { setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
        }
    }

    private fun initDismiss(config: AndesTooltipConfiguration){
        with(dismissComponent){
            if (config.isDismissible){
                setImageDrawable(config.dismissibleIcon)
                setOnClickListener { dismiss() }
                visible(true)
            } else {
                visible(false)
            }
        }
    }

    private fun initPrimaryAction(config: AndesTooltipConfiguration) {
        with(primaryActionComponent){
            if (config.primaryAction != null){
                text = config.primaryAction.label
                hierarchy = config.primaryAction.hierarchy
                config.primaryActionBackgroundColor?.let { primaryActionComponent.changeBackgroundColor(it) }
                config.primaryActionTextColor?.let { primaryActionComponent.changeTextColor(it.colorInt(context)) }
                setOnClickListener {
                    dismiss()
                    config.primaryAction.onActionClicked(it, this@AndesTooltip)
                }
                visible(true)
            } else {
                visible(false)
            }
        }
    }

    private fun initSecondaryAction(config: AndesTooltipConfiguration) {
        with(secondaryActionComponent){
            if (config.secondaryAction != null){
                text = config.secondaryAction.label
                hierarchy = config.secondaryAction.hierarchy
                config.secondaryActionBackgroundColor?.let { changeBackgroundColor(it) }
                config.secondaryActionTextColor?.let { changeTextColor(it.colorInt(context)) }
                setOnClickListener {
                    dismiss()
                    config.secondaryAction.onActionClicked(it, this@AndesTooltip)
                }
                visible(true)
            } else {
                visible(false)
            }
        }
    }

    private fun initLinkAction(config: AndesTooltipConfiguration) {
        with(linkActionComponent){
            if (config.linkAction != null) {
                text = config.linkAction.label
                typeface = context.getFontOrDefault(R.font.andes_font_regular)
                config.linkActionTextColor?.let { setTextColor(it.colorInt(context)) }
                config.linkActionIsUnderlined?.let { paintFlags = Paint.UNDERLINE_TEXT_FLAG }
                setOnClickListener {
                    dismiss()
                    config.linkAction.onActionClicked(it, this@AndesTooltip)
                }
                visible(true)
            } else {
                visible(false)
            }
        }
    }

    private fun applyAndesTooltipAnimation() {
        bodyWindow.animationStyle = R.style.Andes_FadeWindowAnimation
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        onDestroy()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        this.destroyed = true
        this.bodyWindow.dismiss()
    }


    override lateinit var radiusLayout: RadiusLayout
    override lateinit var frameLayoutContainer: FrameLayout

    override val bodyWindowHeight: Int
        get() = bodyWindow.height

    override val bodyWindowWidth: Int
        get() = bodyWindow.width

    override val displaySizeX: Int
        get() = context.displaySize().x

    override val displaySizeY: Int
        get() = context.displaySize().y

    override val tooltipMeasuredWidth: Int
        get() = container.root.measuredWidth

    override val tooltipMeasuredHeight: Int
        get() = container.root.measuredHeight

    override val paddingWithArrow: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_padding_with_arrow)

    override val arrowImageInnerPadding: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_arrow_inner_margin)

    override val arrowWidth: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_arrow_width)

    override val arrowHeight: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_arrow_height)

    override val arrowBorder: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_arrow_border)

    override val elevation: Int
        get() = context.resources.getDimensionPixelOffset(R.dimen.andes_tooltip_elevation)

    override fun showDropDown(target: View, xOff: Int, yOff: Int, locationConfig: AndesTooltipLocationConfig){
        this.isShowing = true
        var attrs = andesTooltipAttrs

        bodyWindow.showAsDropDown(target, xOff, yOff)

        if (locationConfig.mLocation != andesTooltipLocationConfigRequired.mLocation){
            attrs = andesTooltipAttrs.copy(tooltipLocation = locationConfig.mLocation)
        }

        val config = AndesTooltipConfigurationFactory.create(context, attrs)
        initializeArrow(locationConfig)
        setupComponents(config, locationConfig)
        applyAndesTooltipAnimation()
    }

    companion object {
        private val STYLE_DEFAULT = AndesTooltipStyle.LIGHT
        private val TIP_ORIENTATION_DEFAULT = AndesTooltipLocation.TOP
        private val TITLE_DEFAULT = null
        private val SECONDARY_ACTION_DEFAULT = null
        private val LINK_ACTION_DEFAULT = null
        private const val IS_DISMISSIBLE_DEFAULT = true
    }
}