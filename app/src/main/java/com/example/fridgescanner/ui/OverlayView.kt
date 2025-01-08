package com.example.fridgescanner.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint().apply {
        //color = context.getColor(R.color.design_default_color_primary)
        color = android.graphics.Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    var qrCodeBounds: android.graphics.Rect? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        qrCodeBounds?.let {
            canvas.drawRect(it, paint)
        }
    }
}