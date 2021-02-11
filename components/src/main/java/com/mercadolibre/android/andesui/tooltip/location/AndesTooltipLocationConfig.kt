package com.mercadolibre.android.andesui.tooltip.location

import android.view.View
import com.mercadolibre.android.andesui.tooltip.extensions.getArrowPositionX
import com.mercadolibre.android.andesui.tooltip.extensions.getArrowPositionY
import com.mercadolibre.android.andesui.tooltip.extensions.getSpaceConditionByLocation
import com.mercadolibre.android.andesui.tooltip.extensions.getTooltipXOff
import com.mercadolibre.android.andesui.tooltip.extensions.getTooltipYOff

data class AndesTooltipArrowData(val position: AndesTooltipArrowPosition, val point: Int)
data class AndesTooltipArrowPoint(val x: Float, val y: Float)
data class AndesTooltipPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

sealed class AndesTooltipLocationConfig(
        val mLocation: AndesTooltipLocation,
        val otherLocationsAttempts : List<AndesTooltipLocation>
){
    protected lateinit var arrowPosition: AndesTooltipArrowPosition
    abstract fun buildTooltipInRequiredLocation(target: View): Boolean
    abstract fun iterateOtherLocations(target: View): Boolean
    abstract fun getTooltipPadding(): AndesTooltipPadding
    abstract fun getArrowPoint(): AndesTooltipArrowPoint
    abstract fun getArrowRotation(): Float
}

class TopAndesTooltipLocationConfig(private val andesTooltip: AndesTooltipLocationInterface): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.TOP,
        otherLocationsAttempts = listOf(AndesTooltipLocation.BOTTOM, AndesTooltipLocation.LEFT, AndesTooltipLocation.RIGHT)
){

    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        andesTooltip.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val arrowData = getTooltipXOff(target, this)
                arrowPosition = arrowData.position
                val xOff = arrowData.point
                val yOff = -tooltipMeasuredHeight - target.measuredHeight
                showDropDown(target, xOff, yOff, this@TopAndesTooltipLocationConfig)
                return true
            }
            return false
        }
    }

    override fun iterateOtherLocations(target: View): Boolean {
        andesTooltip.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        andesTooltip.apply {
            val paddingSize = paddingWithArrow
            val elevation = elevation
            return AndesTooltipPadding(elevation, elevation, elevation, paddingSize)
        }
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        andesTooltip.run {
            return AndesTooltipArrowPoint(
                    x = getArrowPositionX(frameLayoutContainer.width, this, arrowPosition),
                    y = frameLayoutContainer.y + radiusLayout.height - elevation
            )
        }
    }

    override fun getArrowRotation() = 0F
}

class BottomAndesTooltipLocationConfig(private val andesTooltip: AndesTooltipLocationInterface): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.BOTTOM,
        otherLocationsAttempts = listOf(AndesTooltipLocation.TOP, AndesTooltipLocation.LEFT, AndesTooltipLocation.RIGHT)
){
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        andesTooltip.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val arrowData = getTooltipXOff(target, this)
                arrowPosition = arrowData.position
                val xOff = arrowData.point
                val yOff = 0
                showDropDown(target, xOff, yOff, this@BottomAndesTooltipLocationConfig)
                return true
            }
            return false
        }
    }

    override fun iterateOtherLocations(target: View): Boolean {
        andesTooltip.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        andesTooltip.apply {
            val paddingSize = paddingWithArrow
            val elevation = elevation
            return AndesTooltipPadding(elevation, paddingSize, elevation, elevation)
        }
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        andesTooltip.run {
            return AndesTooltipArrowPoint(
                    x = getArrowPositionX(frameLayoutContainer.width, this, arrowPosition),
                    y = radiusLayout.y - arrowBorder
            )
        }
    }

    override fun getArrowRotation() = 180F
}

class LeftAndesTooltipLocationConfig(private val andesTooltip: AndesTooltipLocationInterface): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.LEFT,
        otherLocationsAttempts = listOf(AndesTooltipLocation.RIGHT, AndesTooltipLocation.TOP, AndesTooltipLocation.BOTTOM)
){
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        andesTooltip.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val arrowData = getTooltipYOff(target, this)
                arrowPosition = arrowData.position
                val yOff = arrowData.point
                val xOff = -(tooltipMeasuredWidth)
                showDropDown(target, xOff, yOff, this@LeftAndesTooltipLocationConfig)
                return true
            }
            return false
        }
    }

    override fun iterateOtherLocations(target: View): Boolean {
        andesTooltip.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        andesTooltip.apply {
            val paddingSize = paddingWithArrow
            val elevation = elevation
            return AndesTooltipPadding(elevation, elevation, paddingSize, elevation)
        }
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        andesTooltip.run {
            return AndesTooltipArrowPoint(
                    x = frameLayoutContainer.x + radiusLayout.width - elevation - arrowImageInnerPadding,
                    y = getArrowPositionY(frameLayoutContainer.height, this, arrowPosition)
            )
        }
    }

    override fun getArrowRotation() = -90F

}

class RightAndesTooltipLocationConfig(private val andesTooltip: AndesTooltipLocationInterface): AndesTooltipLocationConfig(
        mLocation = AndesTooltipLocation.RIGHT,
        otherLocationsAttempts = listOf(AndesTooltipLocation.LEFT, AndesTooltipLocation.TOP, AndesTooltipLocation.BOTTOM)
){
    override fun buildTooltipInRequiredLocation(target: View): Boolean {
        andesTooltip.run {
            if (mLocation.getSpaceConditionByLocation().invoke(this, target)){
                val arrowData = getTooltipYOff(target, this)
                arrowPosition = arrowData.position
                val yOff = arrowData.point
                val xOff = target.measuredWidth
                showDropDown(target, xOff, yOff, this@RightAndesTooltipLocationConfig)
                return true
            }
            return false
        }
    }

    override fun iterateOtherLocations(target: View): Boolean {
        andesTooltip.run {
            otherLocationsAttempts.forEach { location ->
                if (location.getSpaceConditionByLocation().invoke(this, target)){
                    return getAndesTooltipLocationConfig(this, location).buildTooltipInRequiredLocation(target)
                }
            }
            return false
        }
    }

    override fun getTooltipPadding(): AndesTooltipPadding {
        andesTooltip.apply {
            val paddingSize = paddingWithArrow
            val elevation = elevation
            return AndesTooltipPadding(paddingSize, elevation, elevation, elevation)
        }
    }

    override fun getArrowPoint(): AndesTooltipArrowPoint {
        andesTooltip.run {
            return AndesTooltipArrowPoint(
                    x = frameLayoutContainer.x,
                    y = getArrowPositionY(frameLayoutContainer.height, this, arrowPosition)
            )
        }
    }

    override fun getArrowRotation() = 90F
}

fun getAndesTooltipLocationConfig(tooltip: AndesTooltipLocationInterface, location: AndesTooltipLocation): AndesTooltipLocationConfig {
    return when (location) {
        AndesTooltipLocation.TOP -> TopAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.BOTTOM -> BottomAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.LEFT -> LeftAndesTooltipLocationConfig(tooltip)
        AndesTooltipLocation.RIGHT -> RightAndesTooltipLocationConfig(tooltip)
    }
}
