package com.mercadolibre.android.andesui.tooltip.radius

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.Px
import com.mercadolibre.android.andesui.tooltip.extensions.dp2Px

class RadiusLayout @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attr, defStyle) {

  private val path = Path()

  @Px private var _radius: Float = 0f
  var radius: Float
    @Px get() = _radius
    set(dpValue) {
      _radius = context.dp2Px(dpValue)
      invalidate()
    }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    path.apply {
      addRoundRect(
        RectF(0f, 0f, w.toFloat(), h.toFloat()),
        radius, radius,
        Path.Direction.CW
      )
    }
  }

  override fun dispatchDraw(canvas: Canvas) {
    canvas.clipPath(path)
    super.dispatchDraw(canvas)
  }
}
