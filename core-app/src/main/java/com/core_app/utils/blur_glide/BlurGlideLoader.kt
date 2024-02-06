package com.core_app.utils.blur_glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

class BlurGlideLoader : ModelLoader<BlurGlideModel, InputStream> {
    override fun buildLoadData(
        model: BlurGlideModel,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(ObjectKey(model), BlurGlideFetcher(model, width, height))
    }

    override fun handles(model: BlurGlideModel): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<BlurGlideModel, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<BlurGlideModel, InputStream> {
            return BlurGlideLoader()
        }

        override fun teardown() {}
    }
}