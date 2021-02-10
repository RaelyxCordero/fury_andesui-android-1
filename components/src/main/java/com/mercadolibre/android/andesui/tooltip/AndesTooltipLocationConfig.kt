package com.mercadolibre.android.andesui.tooltip

import android.view.View
import com.mercadolibre.android.andesui.tooltip.extensions.dp2Px
import com.mercadolibre.android.andesui.tooltip.extensions.getArrowPositionX
import com.mercadolibre.android.andesui.tooltip.extensions.getArrowPositionY
import com.mercadolibre.android.andesui.tooltip.extensions.getSpaceConditionByLocation
import com.mercadolibre.android.andesui.tooltip.extensions.getTooltipXOff
import com.mercadolibre.android.andesui.tooltip.extensions.getTooltipYOff
import java.lang.ref.WeakReference

data class AndesTooltipArrowPoint(val x: Float, val y: Float)
data class AndesTooltipPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

sealed class AndesTooltipLocationConfig(
        val mLocation: AndesTooltipLocation,
        val otherLocationsAttempts : List<AndesTooltipLocation>
){
    var weakTooltip: AndesTooltip?
        get() = tooltip?.get()
        set(value) {
            value?.let { tooltip = WeakReference(value) }
        }

    private var tooltip: WeakReference<AndesTooltip>? = null

    abstract fun buildTooltipInRequiredLocation(target: View): Boolean
    abstract fun iterateOtherLocations(target: View): Boolean
    abstract fun getTooltipPadding(): AndesTooltipPadding
    abstract fun getArrowPoint(): AndesTooltipArrowPoint
    abstract fun getArrowRotation(): Float
}

class TopAndesTooltipLocationConfig(andesTooltip: AndesTooltip): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.TOP,
        otherLocationsAttempts = listOf(AndesTooltipLocation.BOTTOM, AndesTooltipLocation.LEFT, AndesTooltipLocation.RIGHT)
){
    init {
        weakTooltip = andesTooltip
    }
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        weakTooltip?.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val xOff = getTooltipXOff(target)
                val yOff = -getMeasuredHeight() - target.measuredHeight
                showDropDown(target, xOff, yOff, mLocation)
                return true
            }
            return false
        }
        return false
    }

    override fun iterateOtherLocations(target: View): Boolean {
        weakTooltip?.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
        return false
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        weakTooltip?.apply {
            val paddingSize = ARROW_SIZE.toInt() + context.dp2Px(3)
            val elevation = ELEVATION.toInt()
            return AndesTooltipPadding(elevation, elevation, elevation, paddingSize)
        }
        return AndesTooltipPadding(0,0,0,0)
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        weakTooltip?.run {
            return AndesTooltipArrowPoint(
                    x = getArrowPositionX(frameLayoutContainer.width),
                    y = frameLayoutContainer.y + radiusLayout.height - ELEVATION
            )
        }
        return AndesTooltipArrowPoint(0F,0F)
    }

    override fun getArrowRotation(): Float {
        weakTooltip?.run {
            return 0F
        }
        return 0F
    }

}

class BottomAndesTooltipLocationConfig(andesTooltip: AndesTooltip): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.BOTTOM,
        otherLocationsAttempts = listOf(AndesTooltipLocation.TOP, AndesTooltipLocation.LEFT, AndesTooltipLocation.RIGHT)
){
    init {
        weakTooltip = andesTooltip
    }
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        weakTooltip?.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val xOff = getTooltipXOff(target)
                val yOff = 0
                showDropDown(target, xOff, yOff, mLocation)
                return true
            }
            return false
        }
        return false
    }

    override fun iterateOtherLocations(target: View): Boolean {
        weakTooltip?.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
        return false
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        weakTooltip?.apply {
            val paddingSize = ARROW_SIZE.toInt() + context.dp2Px(3)
            val elevation = ELEVATION.toInt()
            return AndesTooltipPadding(elevation, paddingSize, elevation, elevation)
        }
        return AndesTooltipPadding(0,0,0,0)
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        weakTooltip?.run {
            return AndesTooltipArrowPoint(
                    x = getArrowPositionX(frameLayoutContainer.width),
                    y = radiusLayout.y - ARROW_SIZE
            )
        }
        return AndesTooltipArrowPoint(0F,0F)
    }

    override fun getArrowRotation(): Float {
        weakTooltip?.run {
            return 180F
        }
        return 0F
    }

}

class LeftAndesTooltipLocationConfig(andesTooltip: AndesTooltip): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.LEFT,
        otherLocationsAttempts = listOf(AndesTooltipLocation.RIGHT, AndesTooltipLocation.TOP, AndesTooltipLocation.BOTTOM)
){
    init {
        weakTooltip = andesTooltip
    }
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        weakTooltip?.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val xOff = -(getMeasuredWidth())
                val yOff = getTooltipYOff(target)
                showDropDown(target, xOff, yOff, mLocation)
                return true
            }
            return false
        }
        return false
    }

    override fun iterateOtherLocations(target: View): Boolean {
        weakTooltip?.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
        return false
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        weakTooltip?.apply {
            val paddingSize = ARROW_SIZE.toInt() + context.dp2Px(3)
            val elevation = ELEVATION.toInt()
            return AndesTooltipPadding(elevation, elevation, paddingSize, elevation)
        }
        return AndesTooltipPadding(0,0,0,0)
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        weakTooltip?.run {
            return AndesTooltipArrowPoint(
                    x = frameLayoutContainer.x + radiusLayout.width - ELEVATION - context.dp2Px(2F), //image inner padding
                    y = getArrowPositionY(frameLayoutContainer.height)
            )
        }
        return AndesTooltipArrowPoint(0F,0F)
    }

    override fun getArrowRotation(): Float {
        weakTooltip?.run {
            return -90F
        }
        return 0F
    }

}

class RightAndesTooltipLocationConfig(andesTooltip: AndesTooltip): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.RIGHT,
        otherLocationsAttempts = listOf(AndesTooltipLocation.LEFT, AndesTooltipLocation.TOP, AndesTooltipLocation.BOTTOM)
){
    init {
        weakTooltip = andesTooltip
    }
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        weakTooltip?.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val xOff = target.measuredWidth
                val yOff = getTooltipYOff(target)
                showDropDown(target, xOff, yOff, mLocation)
                return true
            }
            return false
        }
        return false
    }

    override fun iterateOtherLocations(target: View): Boolean {
        weakTooltip?.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
        return false
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        weakTooltip?.apply {
            val paddingSize = ARROW_SIZE.toInt() + context.dp2Px(3)
            val elevation = ELEVATION.toInt()
            return AndesTooltipPadding(paddingSize, elevation, elevation, elevation)
        }
        return AndesTooltipPadding(0,0,0,0)
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        weakTooltip?.run {
            return AndesTooltipArrowPoint(
                    x = frameLayoutContainer.x,
                    y = getArrowPositionY(frameLayoutContainer.height)
            )
        }
        return AndesTooltipArrowPoint(0F,0F)
    }

    override fun getArrowRotation(): Float {
        weakTooltip?.run {
            return 90F
        }
        return 0F
    }

}

fun getAndesTooltipLocationConfig(tooltip: AndesTooltip, location: AndesTooltipLocation): AndesTooltipLocationConfig {

    return when (location) {
        AndesTooltipLocation.TOP -> TopAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.BOTTOM -> BottomAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.LEFT -> LeftAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.RIGHT -> RightAndesTooltipLocationConfig(tooltip)
    }
}
