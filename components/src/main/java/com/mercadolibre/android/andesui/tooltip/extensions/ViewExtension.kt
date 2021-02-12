package com.mercadolibre.android.andesui.tooltip.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.andesui.tooltip.AndesTooltip
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipArrowData
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipArrowPosition
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocation
import com.mercadolibre.android.andesui.tooltip.location.AndesTooltipLocationInterface

/** gets display size as a point. */
internal fun Context.displaySize(): Point {
  return Point(
          resources.displayMetrics.widthPixels,
          resources.displayMetrics.heightPixels
  )
}
/** returns if an Activity is finishing or not. */
internal fun Context.isFinishing(): Boolean {
  return this is Activity && this.isFinishing
}

/** sets visibility of the view based on the given parameter. */
internal fun View.visible(shouldVisible: Boolean) {
  visibility = if (shouldVisible) {
    View.VISIBLE
  } else {
    View.GONE
  }
}

/** computes and returns the coordinates of this view on the screen. */
internal fun View.getViewPointOnScreen(): Point {
  val location: IntArray = intArrayOf(0, 0)
  getLocationOnScreen(location)
  return Point(location[0], location[1])
}

/** returns the status bar height if the anchor is on the Activity. */
internal fun View.getStatusBarHeight(isStatusBarVisible: Boolean): Int {
  val rectangle = Rect()
  val context = context
  return if (context is Activity && isStatusBarVisible) {
    context.window.decorView.getWindowVisibleDisplayFrame(rectangle)
    rectangle.top
  } else 0
}

internal fun View.getActionBarHeight(): Int {
  if (context is AppCompatActivity){
    (context as AppCompatActivity).supportActionBar?.let {
      if (it.isShowing){ return it.height }
    }
  }
  return 0
}

internal fun View.isActionBarVisible(): Boolean {
  if (context is AppCompatActivity){
    (context as AppCompatActivity).supportActionBar?.let {
      return it.isShowing
    }
  }
  return false
}

internal fun AndesTooltipLocation.getSpaceConditionByLocation(): ((tooltip: AndesTooltipLocationInterface, target: View) -> Boolean){
  return when(this) {
    AndesTooltipLocation.TOP -> tooltipHasTopSpace
    AndesTooltipLocation.BOTTOM -> tooltipHasBottomSpace
    AndesTooltipLocation.LEFT -> tooltipHasLeftSpace
    AndesTooltipLocation.RIGHT -> tooltipHasRightSpace
  }
}

internal val tooltipHasTopSpace = fun (tooltip: AndesTooltipLocationInterface, target: View): Boolean{
  val actionBarHeight = target.getActionBarHeight() + target.getStatusBarHeight(true)
  val actionBarVisible = target.isActionBarVisible()
  val targetY = target.getViewPointOnScreen().y
  val tooltipHeight = tooltip.bodyWindowHeight

  return if (!actionBarVisible){
    targetY - tooltipHeight > 0
  } else {
    targetY - tooltipHeight - actionBarHeight > 0
  }
}

internal val tooltipHasBottomSpace = fun (tooltip: AndesTooltipLocationInterface, target: View): Boolean{
  val targetY = target.getViewPointOnScreen().y
  val targetHeight = target.height
  val tooltipHeight = tooltip.bodyWindowHeight
  val bottomWall = tooltip.displaySizeY

  return targetY + targetHeight + tooltipHeight < bottomWall
}

internal val tooltipHasLeftSpace = fun (tooltip: AndesTooltipLocationInterface, target: View): Boolean{
  val targetX = target.getViewPointOnScreen().x
  val tooltipWidth = tooltip.bodyWindowWidth

  return targetX - tooltipWidth > 0
}

internal val tooltipHasRightSpace = fun (tooltip: AndesTooltipLocationInterface, target: View): Boolean{
  val targetX = target.getViewPointOnScreen().x
  val targetWidth = target.width
  val tooltipWidth = tooltip.bodyWindowWidth
  val rightWall = tooltip.displaySizeX

  return targetX + targetWidth + tooltipWidth < rightWall
}

internal fun getTooltipXOff(target: View, tooltip: AndesTooltipLocationInterface): AndesTooltipArrowData {

  val targetX = target.getViewPointOnScreen().x
  val targetWidth = target.measuredWidth
  val targetHalfXPoint = targetX + (targetWidth / 2)

  val tooltipWidth = tooltip.tooltipMeasuredWidth
  val tooltipHalf = tooltipWidth / 2

  val leftSpaceNeededForCenterArrow = targetHalfXPoint - tooltipHalf
  val rightSpaceNeededForCenterArrow = targetHalfXPoint + tooltipHalf

  val rightSpaceNeededForLeftArrow = targetHalfXPoint - tooltip.arrowWidth/2 - tooltip.arrowBorder + tooltipWidth
  val availableSpaceForLeftArrow = tooltip.displaySizeX - targetHalfXPoint

  return when {
    //can arrow center?
    (leftSpaceNeededForCenterArrow > 0 && rightSpaceNeededForCenterArrow < tooltip.displaySizeX) -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.MIDDLE,
              point = ((targetWidth / 2) - (tooltip.tooltipMeasuredWidth / 2))
      )
    }

    //can arrow left?
    (rightSpaceNeededForLeftArrow < availableSpaceForLeftArrow) -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.FIRST,
              point = (targetWidth/2 - tooltip.arrowWidth/2 - tooltip.arrowBorder)
      )
    }

    //arrow right
    else -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.LAST,
              point = (-tooltip.tooltipMeasuredWidth + targetWidth/2 + tooltip.arrowWidth/2 + tooltip.arrowBorder)
      )
    }
  }
}

internal fun getTooltipYOff(target: View, tooltip: AndesTooltipLocationInterface): AndesTooltipArrowData {
  val actionBarHeight = target.getActionBarHeight() + target.getStatusBarHeight(true)
  val actionBarVisible = target.isActionBarVisible()

  val targetY = target.getViewPointOnScreen().y
  val targetHeight = target.measuredHeight
  val targetHalfYPoint = targetY + (targetHeight / 2)

  val tooltipHeight = tooltip.tooltipMeasuredHeight
  val tooltipHalf = tooltipHeight / 2

  val topSpaceNeededForCenterArrow = targetHalfYPoint - tooltipHalf
  val bottomSpaceNeededForCenterArrow = targetHalfYPoint + tooltipHalf
  val topWall = if (actionBarVisible){ actionBarHeight } else { 0 }

  val bottomSpaceNeededForTopArrow = targetHalfYPoint - tooltip.arrowHeight/2 - tooltip.arrowBorder + tooltipHeight
  val availableSpaceForTopArrow = tooltip.displaySizeY - targetHalfYPoint

  return when {
    //can arrow center?
    (topSpaceNeededForCenterArrow > topWall && bottomSpaceNeededForCenterArrow < tooltip.displaySizeY) -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.MIDDLE,
              point = -((tooltipHeight / 2) + (targetHeight / 2))
      )
    }

    //can arrow top?
    (bottomSpaceNeededForTopArrow < availableSpaceForTopArrow) -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.FIRST,
              point = -(targetHeight/2 + tooltip.arrowWidth/2 + tooltip.arrowBorder)
      )
    }

    //arrow bottom
    else -> {
      AndesTooltipArrowData(
              position = AndesTooltipArrowPosition.LAST,
              point = -tooltipHeight + tooltip.arrowWidth/2 - tooltip.arrowBorder + targetHeight/2
      )
    }
  }
}

internal fun getArrowPositionX(containerWidth: Int, tooltip: AndesTooltipLocationInterface, arrowPosition: AndesTooltipArrowPosition): Float{
  return when(arrowPosition) {
    AndesTooltipArrowPosition.FIRST ->
      tooltip.arrowBorder.toFloat()
    AndesTooltipArrowPosition.MIDDLE ->
      ((containerWidth / 2) - (tooltip.arrowWidth/2)).toFloat()
    AndesTooltipArrowPosition.LAST ->
      (containerWidth - tooltip.arrowBorder - tooltip.arrowWidth).toFloat()
  }
}

internal fun getArrowPositionY(containerHeight: Int, tooltip: AndesTooltipLocationInterface, arrowPosition: AndesTooltipArrowPosition): Float{
  return when(arrowPosition) {
    AndesTooltipArrowPosition.FIRST ->
      tooltip.arrowBorder.toFloat()
    AndesTooltipArrowPosition.MIDDLE ->
      ((containerHeight / 2) - (tooltip.arrowWidth/2)).toFloat()
    AndesTooltipArrowPosition.LAST ->
      (containerHeight - tooltip.arrowBorder - tooltip.arrowWidth).toFloat()
  }
}
