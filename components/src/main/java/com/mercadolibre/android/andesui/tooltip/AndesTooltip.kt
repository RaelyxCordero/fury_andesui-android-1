package com.mercadolibre.android.andesui.tooltip

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
import com.mercadolibre.android.andesui.tooltip.extensions.dp2Px
import com.mercadolibre.android.andesui.tooltip.extensions.getActionBarHeight
import com.mercadolibre.android.andesui.tooltip.extensions.getViewPointOnScreen
import com.mercadolibre.android.andesui.tooltip.extensions.isFinishing
import com.mercadolibre.android.andesui.tooltip.extensions.visible
import com.mercadolibre.android.andesui.tooltip.radius.RadiusLayout
import com.mercadolibre.android.andesui.typeface.getFontOrDefault

class AndesTooltip(val context: Context): LifecycleObserver {

    private lateinit var andesTooltipAttrs: AndesTooltipAttrs
    private lateinit var radiusLayout: RadiusLayout
    private lateinit var frameLayoutContainer: FrameLayout
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
    private var marginTop: Int = context.dp2Px(16)
    private var marginLeft: Int = context.dp2Px(16)
    private var marginRight: Int = context.dp2Px(16)
    private var marginBottom: Int = context.dp2Px(16)

    private val ARROW_SIZE = context.dp2Px(10F)
    private val ARROW_WIDTH = context.dp2Px(16)
    private val ARROW_HEIGHT = context.dp2Px(10)
    private val TOOLTIP_ARROW_BORDER = context.dp2Px(10F)
    private val ELEVATION = context.dp2Px(2F)
    private val CORNER_RADIUS = context.dp2Px(4f)
    private val ALPHA: Float = 1F

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

    constructor(context: Context,
                style: AndesTooltipStyle = STYLE_DEFAULT,
                title: String? = TITLE_DEFAULT,
                body: String,
                isDismissible: Boolean = IS_DISMISSIBLE_DEFAULT,
                tipOrientation: AndesTooltipTipOrientation = TIP_ORIENTATION_DEFAULT,
                mainAction: AndesTooltipAction,
                secondaryAction: AndesTooltipAction? = SECONDARY_ACTION_DEFAULT

    ): this(context) {
        andesTooltipAttrs = AndesTooltipAttrs(
                style = style,
                title = title,
                body = body,
                isDismissible = isDismissible,
                tipOrientation = tipOrientation,
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
                tipOrientation: AndesTooltipTipOrientation = TIP_ORIENTATION_DEFAULT,
                linkAction: AndesTooltipLinkAction? = LINK_ACTION_DEFAULT

    ): this(context) {
        andesTooltipAttrs = AndesTooltipAttrs(
                style = style,
                title = title,
                body = body,
                isDismissible = isDismissible,
                tipOrientation = tipOrientation,
                linkAction = linkAction
        )
        initComponents(andesTooltipAttrs)
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

        val config = AndesTooltipConfigurationFactory.create(context, attrs)
        setupComponents(config)
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

    private fun setupComponents(config: AndesTooltipConfiguration){
        initializeAndesTooltipBalloon(config)
        initializeBackground(config)
        initializeAndesTooltipWindow()
        initializeAndesTooltipContent(config)
    }

    private fun initializeAndesTooltipBalloon(config: AndesTooltipConfiguration) {
        //TODO("add margins to config or attrs")
        with(container.andesTooltipBalloon) {
            when (andesTooltipAttrs.tipOrientation) {
                AndesTooltipTipOrientation.LEFT -> (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                        0,
                        marginTop,
                        marginRight,
                        marginBottom
                )
                AndesTooltipTipOrientation.TOP -> (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                        marginLeft,
                        0,
                        marginRight,
                        marginBottom
                )
                AndesTooltipTipOrientation.RIGHT -> (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                        marginLeft,
                        marginTop,
                        0,
                        marginBottom
                )
                AndesTooltipTipOrientation.BOTTOM -> (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
                        marginLeft,
                        marginTop,
                        marginRight,
                        0
                )
            }
        }
    }

    private fun getDoubleArrowSize(): Int {
        return (ARROW_SIZE * 2).toInt()
    }

    private fun initializeArrow(target: View) {
        with(arrowComponent) {
            layoutParams = FrameLayout.LayoutParams(ARROW_WIDTH, ARROW_HEIGHT)
            rotation = when (andesTooltipAttrs.tipOrientation) {
                AndesTooltipTipOrientation.BOTTOM -> 180f
                AndesTooltipTipOrientation.TOP -> 0f
                AndesTooltipTipOrientation.LEFT -> -90f
                AndesTooltipTipOrientation.RIGHT -> 90f
            }
            alpha = ALPHA

            radiusLayout.post {
                ViewCompat.setElevation(this, ELEVATION)
                when (andesTooltipAttrs.tipOrientation) {
                    AndesTooltipTipOrientation.BOTTOM -> {
                        x = getArrowPositionX(target)
                        y = radiusLayout.y + radiusLayout.height

                    }
                    AndesTooltipTipOrientation.TOP -> {
                        x = getArrowPositionX(target)
                        y = radiusLayout.y - ARROW_SIZE
                    }
                    AndesTooltipTipOrientation.LEFT -> {
                        x = frameLayoutContainer.x
                        y = getArrowPositionY(target)
                    }
                    AndesTooltipTipOrientation.RIGHT -> {
                        x = frameLayoutContainer.x + radiusLayout.width - ELEVATION
                        y = getArrowPositionY(target)
                    }
                }
            }
        }
    }

    private fun getArrowPositionX(target: View): Float{
        val tooltipHalf = frameLayoutContainer.getViewPointOnScreen().x + (frameLayoutContainer.width / 2)

        return when {
            (tooltipHalf < target.x) ->
                frameLayoutContainer.width - TOOLTIP_ARROW_BORDER - ARROW_WIDTH
            (tooltipHalf > (target.x + target.width) ) ->
                TOOLTIP_ARROW_BORDER
            else -> (frameLayoutContainer.width / 2).toFloat()
        }
    }

    private fun getArrowPositionY(target: View): Float{
        val tooltipHalf = frameLayoutContainer.getViewPointOnScreen().y + (frameLayoutContainer.height / 2)

        return when {
            (tooltipHalf < target.getViewPointOnScreen().y + target.height/2) ->
                frameLayoutContainer.height - TOOLTIP_ARROW_BORDER - ARROW_HEIGHT

            (tooltipHalf > (target.getViewPointOnScreen().y + target.height/2) ) ->
                TOOLTIP_ARROW_BORDER

            else -> (frameLayoutContainer.height / 2).toFloat()
        }
    }

    private fun initializeBackground(config: AndesTooltipConfiguration) {
        with(radiusLayout) {
            alpha = ALPHA
            ViewCompat.setElevation(this, ELEVATION)
            background = GradientDrawable().apply {
                setColor(config.backgroundColor.colorInt(context))
                cornerRadius = CORNER_RADIUS
            }
            radius = CORNER_RADIUS
        }
    }

    private fun initializeAndesTooltipWindow() {
        with(bodyWindow) {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                elevation = ELEVATION
            }
            isClippingEnabled = false
        }
    }

    private fun initializeAndesTooltipListeners() {
        setOnAndesTooltipDismissListener()
        setOnAndesTooltipOutsideTouchListener()
    }

    private fun initializeAndesTooltipContent(config: AndesTooltipConfiguration) {
        val paddingSize = ARROW_SIZE.toInt() + context.dp2Px(3)
        val elevation = ELEVATION.toInt()
        with(frameLayoutContainer) {
            when (andesTooltipAttrs.tipOrientation) {
                AndesTooltipTipOrientation.LEFT -> setPadding(paddingSize, elevation, elevation, elevation)
                AndesTooltipTipOrientation.TOP -> setPadding(elevation, paddingSize, elevation, elevation)
                AndesTooltipTipOrientation.RIGHT -> setPadding(elevation, elevation, paddingSize, elevation)
                AndesTooltipTipOrientation.BOTTOM -> setPadding(elevation, elevation, elevation, paddingSize)
            }
        }
        initTooltipTitle(config)
        initTooltipBody(config)
        initDismiss(config)
        initPrimaryAction(config)
        initSecondaryAction(config)
        initLinkAction(config)
        initializeAndesTooltipListeners()
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
                setOnClickListener { config.primaryAction.onActionClicked(it, this@AndesTooltip) }
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
                setOnClickListener { config.secondaryAction.onActionClicked(it, this@AndesTooltip) }
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
                setOnClickListener { config.linkAction.onActionClicked(it, this@AndesTooltip) }
                visible(true)
            } else {
                visible(false)
            }
        }
    }

    private fun applyAndesTooltipAnimation() {
        bodyWindow.animationStyle = R.style.Andes_FadeWindowAnimation
    }

    @MainThread
    private inline fun show(target: View, crossinline block: () -> Unit) {
        if (!isShowing && !destroyed && !context.isFinishing() && ViewCompat.isAttachedToWindow(target)) {
            this.isShowing = true

            target.post {
                container.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                bodyWindow.width = getMeasuredWidth()
                bodyWindow.height = getMeasuredHeight()
                constraintContainer.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
                initializeArrow(target)
                initializeAndesTooltipContent(AndesTooltipConfigurationFactory.create(context, andesTooltipAttrs))

                applyAndesTooltipAnimation()
                block()
            }
        }
    }
    /**
     * Shows the tooltip on the center of an target view.
     *
     * @param target A target view which popup will be shown to.
     * @param xOff A horizontal offset from the anchor in pixels.
     * @param yOff A vertical offset from the anchor in pixels.
     */
    fun showCentered(target: View, xOff: Int, yOff: Int) {
        show(target) { bodyWindow.showAsDropDown(target, xOff, yOff) }
    }

    /**
    * Shows the tooltip on the center of an target view.
     *
     * @param target A target view which popup will be shown to.
     */
    fun showCentered(target: View){
        show(target){
        bodyWindow.showAsDropDown(
                    target,
                    1 * ((target.measuredWidth / 2) - (getMeasuredWidth() / 2)),
                    -getMeasuredHeight() - (target.measuredHeight / 2)
            )
        }
    }


    /**
     * Shows the tooltip on an target view as the top alignment with x-off and y-off.
     *
     * @param target A target view which popup will be shown to.
     * @param xOff A horizontal offset from the anchor in pixels.
     * @param yOff A vertical offset from the anchor in pixels.
     */
    @JvmOverloads
    fun showAlignTop(target: View, xOff: Int = 0, yOff: Int = 0) {
        show(target) {
            bodyWindow.showAsDropDown(
                    target,
                    1 * ((target.measuredWidth / 2) - (getMeasuredWidth() / 2) + xOff),
                    -getMeasuredHeight() - target.measuredHeight + yOff
            )
        }
    }

    /**
     * Shows the tooltip on an target view as the bottom alignment with x-off and y-off.
     *
     * @param target A target view which popup will be shown to.
     * @param xOff A horizontal offset from the anchor in pixels.
     * @param yOff A vertical offset from the anchor in pixels.
     */
    @JvmOverloads
    fun showAlignBottom(target: View, xOff: Int = 0, yOff: Int = 0) {
        show(target) {
            bodyWindow.showAsDropDown(
                    target,
                    1 * ((target.measuredWidth / 2) - (getMeasuredWidth() / 2) + xOff),
                    yOff
            )
        }
    }

    /**
     * Shows the tooltip on an target view as the right alignment with x-off and y-off.
     *
     * @param target A target view which popup will be shown to.
     * @param xOff A horizontal offset from the anchor in pixels.
     * @param yOff A vertical offset from the anchor in pixels.
     */
    @JvmOverloads
    fun showAlignRight(target: View, xOff: Int = 0, yOff: Int = 0) {
        show(target) {
            bodyWindow.showAsDropDown(
                    target,
                    target.measuredWidth + xOff,
                    -(getMeasuredHeight() / 2) - (target.measuredHeight / 2) + yOff
            )
        }
    }

    /**
     * Shows the tooltip on an target view as the left alignment with x-off and y-off.
     *
     * @param target A target view which popup will be shown to.
     * @param xOff A horizontal offset from the anchor in pixels.
     * @param yOff A vertical offset from the anchor in pixels.
     */
    @JvmOverloads
    fun showAlignLeft(target: View, xOff: Int = 0, yOff: Int = 0) {
        show(target) {
            val actionBarHeight = target.getActionBarHeight()
            bodyWindow.showAsDropDown(
                    target,
                    -(getMeasuredWidth()) + xOff,
                    -(getMeasuredHeight() / 2) - (target.measuredHeight / 2) + yOff
//                    -(getMeasuredWidth()) + xOff,
//                    -(getMeasuredHeight() / 2) - (target.measuredHeight / 2) + yOff
            )
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

    private fun setOnAndesTooltipOutsideTouchListener(callback: (()->Unit)? = null) {
        this.bodyWindow.setTouchInterceptor(
                object : View.OnTouchListener {
                    override fun onTouch(view: View, event: MotionEvent): Boolean {
                        if (event.action == MotionEvent.ACTION_OUTSIDE) {
                                this@AndesTooltip.dismiss()
                            callback?.invoke()
                            return true
                        }
                        return false
                    }
                }
        )
    }

    /** gets measured width size of the AndesTooltip popup. */
    private fun getMeasuredWidth(): Int {
        val displayWidth = context.displaySize().x
        return when {
            container.root.measuredWidth > displayWidth -> displayWidth
            else -> this.container.root.measuredWidth
        }
    }

    /** gets measured height size of the AndesTooltip popup. */
    private fun getMeasuredHeight(): Int {
        return this.container.root.measuredHeight
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        onDestroy()
    }

    /** dismiss automatically when lifecycle owner is destroyed. */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        this.destroyed = true
        this.bodyWindow.dismiss()
    }

    companion object {
        private val STYLE_DEFAULT = AndesTooltipStyle.LIGHT
        private val TIP_ORIENTATION_DEFAULT = AndesTooltipTipOrientation.BOTTOM
        private val TITLE_DEFAULT = null
        private val SECONDARY_ACTION_DEFAULT = null
        private val LINK_ACTION_DEFAULT = null
        private const val IS_DISMISSIBLE_DEFAULT = true
        private const val DISMISS_WHEN_TOUCH_OUTSIDE = false
        private const val ANCHOR_PADDING_RATIO = 2.5f
    }
}