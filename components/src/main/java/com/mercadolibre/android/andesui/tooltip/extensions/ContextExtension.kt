package com.mercadolibre.android.andesui.tooltip.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import androidx.activity.ComponentActivity
import androidx.annotation.DimenRes

/** gets display size as a point. */
internal fun Context.displaySize(): Point {
  return Point(
    resources.displayMetrics.widthPixels,
    resources.displayMetrics.heightPixels
  )
}

/** dp size to px size. */
internal fun Context.dp2Px(dp: Int): Int {
  val scale = resources.displayMetrics.density
  return (dp * scale).toInt()
}

/** dp size to px size. */
internal fun Context.dp2Px(dp: Float): Float {
  val scale = resources.displayMetrics.density
  return (dp * scale)
}

/** gets a dimension size from dimension resource. */
internal fun Context.dimen(@DimenRes dimenRes: Int): Float {
  return resources.getDimension(dimenRes)
}

/** returns if an Activity is finishing or not. */
internal fun Context.isFinishing(): Boolean {
  return this is Activity && this.isFinishing
}

/** returns an activity from a context. */
internal fun Context.getActivity(): ComponentActivity? {
  var context = this
  while (context is ContextWrapper) {
    if (context is ComponentActivity) {
      return context
    }
    context = context.baseContext
  }
  return null
}
