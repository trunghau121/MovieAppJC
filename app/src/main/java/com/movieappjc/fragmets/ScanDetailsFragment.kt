package com.movieappjc.fragmets

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.movieappjc.OCRManager
import com.movieappjc.R
import com.movieappjc.utils.BitmapUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class ScanDetailsFragment : Fragment() {

    private lateinit var flStroke: FrameLayout
    private lateinit var amount: AppCompatEditText
    private lateinit var image: AppCompatImageView
    private lateinit var dataLayout: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan_details, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View) {
        initUI(view)
        getBundle()
        listener()
    }

    private fun getBundle() {
        val arguments = arguments
        if (arguments != null) {
            val imageUri = arguments.getString("imageUri")!!
            val gallery = arguments.getBoolean("gallery")
            if (gallery) {
                Glide.with(requireContext())
                    .load(imageUri)
                    .error(R.drawable.ic_error)
                    .into(image)
            } else {
                Glide.with(requireContext())
                    .load(imageUri)
                    .error(R.drawable.ic_error)
                    .into(image)
            }
            progressBar.visibility = View.VISIBLE
            analyseImageText(BitmapUtil.uriToBitmap(requireContext(), Uri.parse(imageUri)))
        } else {
            Toast.makeText(requireContext(), "Sorry Some Error Occurred in image processing", Toast.LENGTH_LONG).show()
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ScanFragment())
                .commit()
        }
    }

    private fun initUI(view: View) {
        image = view.findViewById(R.id.image)
        flStroke = view.findViewById(R.id.flStroke)
        amount = view.findViewById(R.id.amount)
        dataLayout = view.findViewById(R.id.dataLayout)
        progressBar = view.findViewById(R.id.progress)
        sharedPreferences = requireContext().getSharedPreferences("saved", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("saved", false).apply()
    }

    private fun listener() {}

    private fun analyseImageText(bitmap: Bitmap?, isEEE: Boolean = true) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                OCRManager(requireContext()).extractTextFromImage(bitmap!!)
            }

            Timber.tag("TextRecognition").d(result)

            amount.setText(extractAmount(result) ?: "No Amount")
            dataLayout.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
//        var inputImage: InputImage? = null
//        try {
//            inputImage = InputImage.fromBitmap(bitmap!!, 0)
//        } catch (e: Exception) {
//            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
//        }
//
//        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//        if (inputImage != null) {
//            recognizer.process(inputImage).addOnSuccessListener {
//                if (it != null) {
//                    for (textBlock in it.textBlocks) {
//                        for (line in textBlock.lines) {
//                            if (line.text.contains("00.55") && isEEE) {
//                                detectHandWritten(
//                                    BitmapUtil.convertBitmapToStroke(
//                                        bitmap!!,
//                                        line.boundingBox!!
//                                    )
//                                )
//                            }
//                            Timber.tag("TextRecognition").d("AAAAAA " + line.text)
//                        }
//                    }
//                }
//            }.addOnFailureListener {
//                Toast.makeText(context, "some error occurred", Toast.LENGTH_LONG).show()
//            }.addOnCompleteListener {
//                dataLayout.visibility = View.VISIBLE
//                progressBar.visibility = View.GONE
//            }
//        } else {
//            Toast.makeText(context, "some error occurred", Toast.LENGTH_LONG).show()
//        }
    }

    private fun detectHandWritten(strokeList: List<Ink.Stroke>) {
        val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
        val digitalInkRecognitionModel = DigitalInkRecognitionModel.builder(modelIdentifier!!).build()
        val recognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(digitalInkRecognitionModel).build())

        val strokeView = StrokeView(requireContext(), strokeList)
        strokeView.viewTreeObserver.addOnGlobalLayoutListener {
            analyseImageText(strokeView.drawToBitmap(), false)
        }
        flStroke.addView(strokeView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        val ink = Ink.builder().apply {
            strokeList.forEach { addStroke(it) }
        }.build()
        Timber.tag("TextRecognition").d("------------------------")
        recognizer.recognize(ink)
            .addOnSuccessListener { result ->
                Timber.tag("TextRecognition").d("HandWritten")
                result.candidates.forEach {
                    Timber.tag("TextRecognition").d(it.text)
                }
                println("📝 Chữ viết tay nhận diện: ${result.candidates.firstOrNull()?.text}")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                println("⚠️ Lỗi nhận diện chữ viết tay: $e")
            }
    }

    private fun extractAmount(text: String): String? {
        // Loại bỏ ký tự đặc biệt không cần thiết
        val cleanedText = text.replace(Regex("[^a-zA-Z0-9.,$§\\s]"), " ")

        // Biểu thức chính quy tìm số tiền có dấu $ hoặc §
        val regex = Regex("[\$§]\\s*\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?")

        // Tìm số tiền hợp lệ
        val match = regex.find(cleanedText)?.value?.trim()

        return match?.replace(Regex("[\$§]"), "")?.trim()
    }

}
