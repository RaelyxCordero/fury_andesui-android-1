package com.mercadolibre.android.andesui.tooltip.location

import android.view.View
import android.widget.FrameLayout
import com.mercadolibre.android.andesui.tooltip.radius.RadiusLayout

interface AndesTooltipLocationInterface {

    val bodyWindowHeight: Int
    val bodyWindowWidth: Int
    val displaySizeX: Int
    val displaySizeY: Int
    val tooltipMeasuredWidth: Int
    val tooltipMeasuredHeight: Int
    val arrowWidth: Int
    val arrowHeight: Int
    val arrowBorder: Int
    val arrowImageInnerPadding: Int
    val paddingWithArrow: Int
    val elevation: Int
    val frameLayoutContainer: FrameLayout
    val radiusLayout: RadiusLayout

    fun showDropDown(target: View, xOff: Int, yOff: Int, locationConfig: AndesTooltipLocationConfig)
}