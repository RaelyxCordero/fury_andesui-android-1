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
import com.mercadolibre.android.andesui.tooltip.extensions.getStatusBarHeight
import com.mercadolibre.android.andesui.tooltip.extensions.getViewPointOnScreen
import com.mercadolibre.android.andesui.tooltip.extensions.isActionBarVisible
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
    private lateinit var arrowPosition: AndesTooltipArrowPosition
    private val bodyWindow: PopupWindow
    private var lifecycleOwner: LifecycleOwner? = null
    private var isShowing = false
    private var destroyed: Boolean = false
    private var marginTop: Int = context.dp2Px(16)
    private var marginLeft: Int = context.dp2Px(16)
    private var marginRight: Int = context.dp2Px(16)
    private var marginBottom: Int = context.dp2Px(16)

    private val ARROW_SIZE = context.dp2Px(10F)
    private val ARROW_WIDTH = context.dp2Px(20)
    private val ARROW_HEIGHT = context.dp2Px(14)
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
        initializeBackground(config)
        initializeAndesTooltipWindow()
        initializeAndesTooltipContent(config)
    }

    private fun initializeArrow() {
        with(arrowComponent) {
            layoutParams = FrameLayout.LayoutParams(ARROW_WIDTH, ARROW_HEIGHT)
            rotation = when (andesTooltipAttrs.tooltipLocation) {
                AndesTooltipLocation.BOTTOM -> 180f
                AndesTooltipLocation.TOP -> 0f
                AndesTooltipLocation.RIGHT -> 90f
                AndesTooltipLocation.LEFT -> - 90f
            }
            alpha = ALPHA

            radiusLayout.post {
                ViewCompat.setElevation(this, ELEVATION)
                when (andesTooltipAttrs.tooltipLocation) {
                    AndesTooltipLocation.TOP -> {
                        x = getArrowPositionX()
                        y = frameLayoutContainer.y + radiusLayout.height - ELEVATION
                    }
                    AndesTooltipLocation.BOTTOM -> {
                        x = getArrowPositionX()
                        y = radiusLayout.y - ARROW_SIZE
                    }
                    AndesTooltipLocation.RIGHT -> {
                        x = frameLayoutContainer.x
                        y = getArrowPositionY()
                    }
                    AndesTooltipLocation.LEFT -> {
                        x = frameLayoutContainer.x + radiusLayout.width - ELEVATION - context.dp2Px(2F) //image inner padding
                        y = getArrowPositionY()
                    }
                }
            }
        }
    }

    private fun getArrowPositionX(): Float{
        return when(arrowPosition) {
            AndesTooltipArrowPosition.FIRST ->
                TOOLTIP_ARROW_BORDER
            AndesTooltipArrowPosition.MIDDLE ->
                ((frameLayoutContainer.width / 2) - (ARROW_WIDTH/2)).toFloat()
            AndesTooltipArrowPosition.LAST ->
                frameLayoutContainer.width - TOOLTIP_ARROW_BORDER - ARROW_WIDTH
        }
    }

    private fun getArrowPositionY(): Float{
       return when(arrowPosition) {
            AndesTooltipArrowPosition.FIRST ->
                TOOLTIP_ARROW_BORDER
            AndesTooltipArrowPosition.MIDDLE ->
                ((frameLayoutContainer.height / 2) - (ARROW_WIDTH/2)).toFloat()
            AndesTooltipArrowPosition.LAST ->
                frameLayoutContainer.height - TOOLTIP_ARROW_BORDER - ARROW_WIDTH
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
            when (andesTooltipAttrs.tooltipLocation) {
                AndesTooltipLocation.LEFT -> setPadding(elevation, elevation, paddingSize, elevation)
                AndesTooltipLocation.TOP -> setPadding(elevation, elevation, elevation, paddingSize)
                AndesTooltipLocation.RIGHT -> setPadding(paddingSize, elevation, elevation, elevation)
                AndesTooltipLocation.BOTTOM -> setPadding(elevation, paddingSize, elevation, elevation)
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
    private inline fun initializeBeforeShow(target: View, crossinline block: () -> Unit) {
        if (!isShowing && !destroyed && !context.isFinishing() && ViewCompat.isAttachedToWindow(target)) {

            target.post {
                container.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                bodyWindow.width = getMeasuredWidth()
                bodyWindow.height = getMeasuredHeight()
                constraintContainer.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )

                initializeAndesTooltipContent(AndesTooltipConfigurationFactory.create(context, andesTooltipAttrs))

                applyAndesTooltipAnimation()
                block()
            }
        }
    }

    fun show(target: View) {
        initializeBeforeShow(target) {
            when (andesTooltipAttrs.tooltipLocation) {
                AndesTooltipLocation.TOP -> {
                    if (tooltipHasTopSpace(target)){
                        val xOff = getTooltipXOff(target)
                        val yOff = -getMeasuredHeight() - target.measuredHeight
                        showDropDown(target, xOff, yOff)
                    }
                }
                AndesTooltipLocation.BOTTOM -> {
                    if (tooltipHasBottomSpace(target)){
                        val xOff = getTooltipXOff(target)
                        val yOff = 0
                        showDropDown(target, xOff, yOff)
                    }
                }
                AndesTooltipLocation.LEFT -> {
                    if (tooltipHasLeftSpace(target)){
                        val xOff = -(getMeasuredWidth())
                        val yOff = getTooltipYOff(target)
                        showDropDown(target, xOff, yOff)
                    }
                }
                AndesTooltipLocation.RIGHT -> {
                    if (tooltipHasRightSpace(target)){
                        val xOff = target.measuredWidth
                        val yOff = getTooltipYOff(target)
                        showDropDown(target, xOff, yOff)
                    }
                }
            }
        }
    }

    private fun showDropDown(target: View, xOff: Int, yOff: Int){
        this.isShowing = true
        bodyWindow.showAsDropDown(target, xOff, yOff)
        initializeArrow()
    }

    private fun tooltipHasTopSpace(target: View): Boolean{
        val actionBarHeight = target.getActionBarHeight() + target.getStatusBarHeight(true)
        val actionBarVisible = target.isActionBarVisible()
        val targetY = target.getViewPointOnScreen().y
        val tooltipHeight = bodyWindow.height

        return if (!actionBarVisible){
            targetY - tooltipHeight > 0
        } else {
            targetY - tooltipHeight - actionBarHeight > 0
        }
    }

    private fun tooltipHasBottomSpace(target: View): Boolean{
        val targetY = target.getViewPointOnScreen().y
        val targetHeight = target.height
        val tooltipHeight = bodyWindow.height
        val bottomWall = context.displaySize().y

        return targetY + targetHeight + tooltipHeight < bottomWall
    }

    private fun tooltipHasLeftSpace(target: View): Boolean{
        val targetX = target.getViewPointOnScreen().x
        val tooltipWidth = bodyWindow.width

        return targetX - tooltipWidth > 0
    }

    private fun tooltipHasRightSpace(target: View): Boolean{
        val targetX = target.getViewPointOnScreen().x
        val targetWidth = target.width
        val tooltipWidth = bodyWindow.width
        val rightWall = context.displaySize().y

        return targetX + targetWidth + tooltipWidth < rightWall
    }

    private fun getTooltipXOff(target: View): Int {

        val targetX = target.getViewPointOnScreen().x
        val targetWidth = target.measuredWidth
        val targetHalfXPoint = targetX + (targetWidth / 2)

        val tooltipWidth = getMeasuredWidth()
        val tooltipHalf = tooltipWidth / 2

        val leftSpaceNeededForCenterArrow = targetHalfXPoint - tooltipHalf
        val rightSpaceNeededForCenterArrow = targetHalfXPoint + tooltipHalf

        val rightSpaceNeededForLeftArrow = targetHalfXPoint - ARROW_WIDTH/2 - TOOLTIP_ARROW_BORDER + tooltipWidth
        val availableSpaceForLeftArrow = context.displaySize().x - targetHalfXPoint

        return when {
            //can arrow center?
            (leftSpaceNeededForCenterArrow > 0 && rightSpaceNeededForCenterArrow < context.displaySize().x) -> {
                arrowPosition = AndesTooltipArrowPosition.MIDDLE
                ((targetWidth / 2) - (getMeasuredWidth() / 2))
            }

            //can arrow left?
            (rightSpaceNeededForLeftArrow < availableSpaceForLeftArrow) -> {
                arrowPosition = AndesTooltipArrowPosition.FIRST
                (targetWidth/2 - ARROW_WIDTH/2 - TOOLTIP_ARROW_BORDER).toInt()
            }

            //arrow right
            else -> {
                arrowPosition = AndesTooltipArrowPosition.LAST
                (-getMeasuredWidth() + targetWidth/2 + ARROW_WIDTH/2 + TOOLTIP_ARROW_BORDER).toInt()
            }
        }
    }

    private fun getTooltipYOff(target: View): Int {
        val actionBarHeight = target.getActionBarHeight() + target.getStatusBarHeight(true)
        val actionBarVisible = target.isActionBarVisible()

        val targetY = target.getViewPointOnScreen().y
        val targetHeight = target.measuredHeight
        val targetHalfYPoint = targetY + (targetHeight / 2)

        val tooltipHeight = getMeasuredHeight()
        val tooltipHalf = tooltipHeight / 2

        val topSpaceNeededForCenterArrow = targetHalfYPoint - tooltipHalf
        val bottomSpaceNeededForCenterArrow = targetHalfYPoint + tooltipHalf
        val topWall = if (actionBarVisible){ actionBarHeight } else { 0 }

        val bottomSpaceNeededForTopArrow = targetHalfYPoint - ARROW_HEIGHT/2 - TOOLTIP_ARROW_BORDER + tooltipHeight
        val availableSpaceForTopArrow = context.displaySize().y - targetHalfYPoint

        return when {
            //can arrow center?
            (topSpaceNeededForCenterArrow > topWall && bottomSpaceNeededForCenterArrow < context.displaySize().y) -> {
                arrowPosition = AndesTooltipArrowPosition.MIDDLE
                -(tooltipHeight / 2) - (targetHeight / 2)
            }

            //can arrow top?
            (bottomSpaceNeededForTopArrow < availableSpaceForTopArrow) -> {
                arrowPosition = AndesTooltipArrowPosition.FIRST
                -(targetHeight/2 + ARROW_WIDTH/2 + TOOLTIP_ARROW_BORDER).toInt()
            }

            //arrow bottom
            else -> {
                arrowPosition = AndesTooltipArrowPosition.LAST
                (targetHeight/2 + ARROW_WIDTH/2 + TOOLTIP_ARROW_BORDER).toInt()
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

    fun setOnAndesTooltipOutsideTouchListener(callback: (()->Unit)? = null) {
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

    /** gets measured width size of the AndesTooltip popup. TODO AJUSTAR CON ANCHO MAXIMO ACORDADO*/
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
        private val TIP_ORIENTATION_DEFAULT = AndesTooltipLocation.TOP
        private val TITLE_DEFAULT = null
        private val SECONDARY_ACTION_DEFAULT = null
        private val LINK_ACTION_DEFAULT = null
        private const val IS_DISMISSIBLE_DEFAULT = true
        private const val DISMISS_WHEN_TOUCH_OUTSIDE = false
        private const val ANCHOR_PADDING_RATIO = 2.5f
    }
}