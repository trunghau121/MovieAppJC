package com.movieappjc.fragmets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.google.mlkit.vision.digitalink.Ink

class StrokeView(context: Context, private val strokeList: List<Ink.Stroke>) : View(context) {
    private val paint = Paint().apply {
        color = Color.BLACK // Đổi sang màu đỏ để dễ nhìn hơn
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        strokeList.forEach { stroke ->
            val path = Path()
            stroke.points.forEachIndexed { index, point ->
                if (index == 0) {
                    path.moveTo(point.x, point.y)
                } else {
                    path.lineTo(point.x, point.y)
                }
            }
            canvas.drawPath(path, paint)
        }
    }
}