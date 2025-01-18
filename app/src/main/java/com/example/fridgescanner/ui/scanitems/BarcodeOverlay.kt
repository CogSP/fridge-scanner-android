package com.example.fridgescanner.ui.scanitems

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class BarcodeOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var barcodeRects: List<Rect> = emptyList()

    fun updateBarcodes(newBarcodeRects: List<Rect>) {
        barcodeRects = newBarcodeRects
        invalidate() // Trigger a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (rect in barcodeRects) {
            canvas.drawRect(rect, paint)
        }
    }
}
