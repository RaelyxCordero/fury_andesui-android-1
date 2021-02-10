package com.mercadolibre.android.andesui.tooltip.extensions

import android.app.Activity
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.andesui.tooltip.AndesTooltip
import com.mercadolibre.android.andesui.tooltip.AndesTooltipArrowPosition
import com.mercadolibre.android.andesui.tooltip.AndesTooltipLocation

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

internal fun AndesTooltipLocation.getSpaceConditionByLocation(): ((tooltip: AndesTooltip,target: View) -> Boolean){
  return when(this) {
    AndesTooltipLocation.TOP -> tooltipHasTopSpace
    AndesTooltipLocation.BOTTOM -> tooltipHasBottomSpace
    AndesTooltipLocation.LEFT -> tooltipHasLeftSpace
    AndesTooltipLocation.RIGHT -> tooltipHasRightSpace
  }
}

internal val tooltipHasTopSpace = fun (tooltip: AndesTooltip, target: View): Boolean{
  val actionBarHeight = target.getActionBarHeight() + target.getStatusBarHeight(true)
  val actionBarVisible = target.isActionBarVisible()
  val targetY = target.getViewPointOnScreen().y
  val tooltipHeight = tooltip.getBodyWindowHeight()

  return if (!actionBarVisible){
    targetY - tooltipHeight > 0
  } else {
    targetY - tooltipHeight - actionBarHeight > 0
  }
}

internal val tooltipHasBottomSpace = fun (tooltip: AndesTooltip, target: View): Boolean{
  val targetY = target.getViewPointOnScreen().y
  val targetHeight = target.height
  val tooltipHeight = tooltip.getBodyWindowHeight()
  val bottomWall = tooltip.context.displaySize().y

  return targetY + targetHeight + tooltipHeight < bottomWall
}

internal val tooltipHasLeftSpace = fun (tooltip: AndesTooltip, target: View): Boolean{
  val targetX = target.getViewPointOnScreen().x
  val tooltipWidth = tooltip.getBodyWindowWidth()

  return targetX - tooltipWidth > 0
}

internal val tooltipHasRightSpace = fun (tooltip: AndesTooltip, target: View): Boolean{
  val targetX = target.getViewPointOnScreen().x
  val targetWidth = target.width
  val tooltipWidth = tooltip.getBodyWindowWidth()
  val rightWall = tooltip.context.displaySize().y

  return targetX + targetWidth + tooltipWidth < rightWall
}

internal fun AndesTooltip.getTooltipXOff(target: View): Int {

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

internal fun AndesTooltip.getTooltipYOff(target: View): Int {
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

internal fun AndesTooltip.getArrowPositionX(containerWidth: Int): Float{
  return when(arrowPosition) {
    AndesTooltipArrowPosition.FIRST ->
      TOOLTIP_ARROW_BORDER
    AndesTooltipArrowPosition.MIDDLE ->
      ((containerWidth / 2) - (ARROW_WIDTH/2)).toFloat()
    AndesTooltipArrowPosition.LAST ->
      containerWidth - TOOLTIP_ARROW_BORDER - ARROW_WIDTH
  }
}

internal fun AndesTooltip.getArrowPositionY(containerHeight: Int): Float{
  return when(arrowPosition) {
    AndesTooltipArrowPosition.FIRST ->
      TOOLTIP_ARROW_BORDER
    AndesTooltipArrowPosition.MIDDLE ->
      ((containerHeight / 2) - (ARROW_WIDTH/2)).toFloat()
    AndesTooltipArrowPosition.LAST ->
      containerHeight - TOOLTIP_ARROW_BORDER - ARROW_WIDTH
  }
}
