package com.core_app.utils.blur_glide

import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.FutureTarget
import jp.wasabeef.glide.transformations.BlurTransformation
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Timer

class BlurGlideFetcher(
    private val model: BlurGlideModel,
    val width: Int,
    val height: Int
) : DataFetcher<InputStream> {
    private val request: RequestManager = Glide.with(model.context)
    private var target: FutureTarget<Bitmap>? = null
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        try {
            target = request
                .asBitmap()
                .load(model.url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transform(BlurTransformation(model.radius, model.sampling))
                .submit(width, height)
            callback.onDataReady(toInputStream(target!!.get()))
        } catch (e: Exception) {
            Timber.d(e)
            callback.onLoadFailed(e)
        }

    }

    override fun cleanup() {
        target?.let {
            request.clear(it)
        }
    }

    override fun cancel() {}

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    private fun toInputStream(bitmap: Bitmap): InputStream {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val bitmapData = bos.toByteArray()
        return ByteArrayInputStream(bitmapData)
    }
}