package com.movieappjc

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OCRManager(private val context: Context) {
    private var tessBaseAPI: TessBaseAPI? = null
    private val dataPath = "${context.filesDir}/tesseract/"

    init {
        copyTrainedDataIfNeeded()
        initTesseract()
    }

    // 1️⃣ Kiểm tra và sao chép traineddata nếu chưa có
    private fun copyTrainedDataIfNeeded() {
        val tessdataPath = "$dataPath/tessdata/"
        val trainedDataFile = File(tessdataPath, "eng.traineddata")

        if (!trainedDataFile.exists()) {
            try {
                val assetManager = context.assets
                val inputStream = assetManager.open("tessdata/eng.traineddata")

                val outputDir = File(tessdataPath)
                outputDir.mkdirs()  // Tạo thư mục nếu chưa có
                val outputStream = FileOutputStream(trainedDataFile)

                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                Log.d("OCR", "Đã sao chép eng.traineddata vào: $trainedDataFile")
            } catch (e: IOException) {
                Log.e("OCR", "Lỗi khi sao chép traineddata: ${e.message}")
            }
        }
    }

    // 2️⃣ Khởi tạo Tesseract OCR
    private fun initTesseract() {
        tessBaseAPI = TessBaseAPI()
        if (!tessBaseAPI!!.init(dataPath, "eng")) {
            tessBaseAPI!!.recycle()
            throw RuntimeException("Failed to initialize Tesseract")
        }
        tessBaseAPI?.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO)
    }

    // 3️⃣ Nhận diện chữ từ ảnh
    fun extractTextFromImage(bitmap: Bitmap): String {
        tessBaseAPI?.setImage(bitmap)
        val result = tessBaseAPI?.utF8Text.orEmpty()
        tessBaseAPI?.clear()
        return result
    }

    // 4️⃣ Giải phóng tài nguyên
    fun release() {
        tessBaseAPI?.recycle()
        tessBaseAPI = null
    }
}
