package com.core_app.network

import android.content.Context
import com.core_app.network.interceptor.HttpRequestInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val CLIENT_TIME_OUT = 60L
private const val CLIENT_CACHE_SIZE = 10 * 1024 * 1024L
private const val CLIENT_CACHE_DIRECTORY = "http"

fun createMoshi(): Moshi {
    return Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
}

fun createCache(context: Context): Cache = Cache(
    directory = File(context.cacheDir, CLIENT_CACHE_DIRECTORY),
    maxSize = CLIENT_CACHE_SIZE
)

fun createHttpLoggingInterceptor(isDev: Boolean = true): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = if (isDev) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}

fun createHttpRequestInterceptor(): HttpRequestInterceptor {
    return HttpRequestInterceptor()
}

fun createOkHttpClient(
    isCache: Boolean = false,
    vararg interceptors: Interceptor,
    context: Context
): OkHttpClient {
    return OkHttpClient.Builder().apply {
        if (isCache) cache(createCache(context))
        interceptors.forEach { addInterceptor(it) }
        connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
        followSslRedirects(true)
        followRedirects(true)
        retryOnConnectionFailure(true)
    }.build()
}


/**
 * Create Retrofit Client with Moshi
 *
 * <reified T> private func let us using reflection.
 * We can use generics and reflection so ->
 *  we don't have to define required NewsApi Interface here
 */
inline fun <reified T> createRetrofitWithMoshi(
    okHttpClient: OkHttpClient,
    moshi: Moshi,
    baseUrl: String
): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    return retrofit.create(T::class.java)
}