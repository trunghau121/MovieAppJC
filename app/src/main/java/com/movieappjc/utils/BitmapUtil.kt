package com.movieappjc.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.digitalink.Ink
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.max

object BitmapUtil {

    private const val MAX_IMAGE_DIMENSION = 1280
    private const val SCALE_FACTOR = 0.5

    fun optimizeImageForOCR(source: Bitmap): Bitmap {
        var width = source.width
        var height = source.height

        if (max(width.toDouble(), height.toDouble()) > MAX_IMAGE_DIMENSION) {
            val ratio = (MAX_IMAGE_DIMENSION.toFloat() / max(
                width.toDouble(),
                height.toDouble()
            )).toFloat()
            width = Math.round(width * ratio)
            height = Math.round(height * ratio)
        }

        val sourceMat = Mat()
        Utils.bitmapToMat(source, sourceMat)
        val resized = Mat()
        Imgproc.resize(sourceMat, resized, Size(width * SCALE_FACTOR, height * SCALE_FACTOR))

        val processed = Mat()
        Imgproc.GaussianBlur(resized, processed, Size(3.0, 3.0), 0.0)
        Imgproc.threshold(
            processed,
            processed,
            0.0,
            255.0,
            Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
        )

        val result =
            Bitmap.createBitmap(processed.cols(), processed.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(processed, result)

        sourceMat.release()
        resized.release()
        processed.release()

        return result
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        // Đảm bảo `rect` nằm trong kích thước hợp lệ của ảnh
        val left = rect.left.coerceAtLeast(0)
        val top = rect.top.coerceAtLeast(0)
        val width = rect.width().coerceAtMost(bitmap.width - left)
        val height = rect.height().coerceAtMost(bitmap.height - top)

        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }


//    fun convertBitmapToStroke(bitmap: Bitmap, rect: Rect): List<Ink.Stroke> {
//        val croppedBitmap = cropBitmap(bitmap, rect) // Cắt ảnh trước khi xử lý
//        val strokeList = mutableListOf<Ink.Stroke>()
//        var strokeBuilder: Ink.Stroke.Builder? = null
//
//        for (y in 0 until croppedBitmap.height) {
//            for (x in 0 until croppedBitmap.width) {
//                val pixel = croppedBitmap.getPixel(x, y)
//                val alpha = (pixel shr 24) and 0xff
//                val red = (pixel shr 16) and 0xff
//                val green = (pixel shr 8) and 0xff
//                val blue = pixel and 0xff
//
//                val isInkPixel = (red < 128 && green < 128 && blue < 128) // Phát hiện nét vẽ (màu tối)
//
//                if (isInkPixel) {
//                    if (strokeBuilder == null) {
//                        strokeBuilder = Ink.Stroke.builder()
//                    }
//                    strokeBuilder.addPoint(Ink.Point.create(x.toFloat(), y.toFloat(), System.currentTimeMillis()))
//                } else {
//                    // Nếu gặp khoảng trống, đóng stroke trước đó
//                    if (strokeBuilder != null) {
//                        strokeList.add(strokeBuilder.build())
//                        strokeBuilder = null
//                    }
//                }
//            }
//        }
//
//        // Đảm bảo stroke cuối cùng không bị bỏ sót
//        if (strokeBuilder != null) {
//            strokeList.add(strokeBuilder.build())
//        }
//
//        return strokeList
//    }

    fun convertBitmapToStroke(bitmap: Bitmap, rect: Rect): List<Ink.Stroke> {
        val croppedBitmap = cropBitmap(bitmap, rect)
        val strokeList = mutableListOf<Ink.Stroke>()
        val visited = Array(croppedBitmap.width) { BooleanArray(croppedBitmap.height) }

        val directions = listOf(
            Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1), // Lên, xuống, trái, phải
            Pair(1, 1), Pair(-1, -1), Pair(1, -1), Pair(-1, 1)  // Các hướng chéo
        )

        fun isInkPixel(x: Int, y: Int): Boolean {
            val pixel = croppedBitmap.getPixel(x, y)
            val red = (pixel shr 16) and 0xff
            val green = (pixel shr 8) and 0xff
            val blue = pixel and 0xff
            return red < 128 && green < 128 && blue < 128 // Chữ có màu tối
        }

        fun bfsStroke(startX: Int, startY: Int): Ink.Stroke {
            val strokeBuilder = Ink.Stroke.builder()
            val queue = ArrayDeque<Pair<Int, Int>>()
            queue.add(Pair(startX, startY))

            while (queue.isNotEmpty()) {
                val (x, y) = queue.removeFirst()
                if (visited[x][y]) continue
                visited[x][y] = true
                strokeBuilder.addPoint(Ink.Point.create(x.toFloat(), y.toFloat(), System.currentTimeMillis()))

                // Tìm các điểm lân cận cùng thuộc nét vẽ
                for ((dx, dy) in directions) {
                    val newX = x + dx
                    val newY = y + dy
                    if (newX in 0 until croppedBitmap.width &&
                        newY in 0 until croppedBitmap.height &&
                        !visited[newX][newY] &&
                        isInkPixel(newX, newY)
                    ) {
                        queue.add(Pair(newX, newY))
                    }
                }
            }
            return strokeBuilder.build()
        }

        for (x in 0 until croppedBitmap.width) {
            for (y in 0 until croppedBitmap.height) {
                if (!visited[x][y] && isInkPixel(x, y)) {
                    strokeList.add(bfsStroke(x, y))
                }
            }
        }

        return strokeList
    }


}