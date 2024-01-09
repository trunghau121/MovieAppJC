package com.core_app.di
import com.core_app.BaseApplication
import com.core_app.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L
val RetrofitModule = module {
    single { GsonBuilder().create() }
    single { retrofitHttpClient() }
    single { retrofitBuilder() }
}

private fun Scope.retrofitBuilder(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BaseApplication.getInstance().apiUrl)
        .addConverterFactory(GsonConverterFactory.create(get()))
        .client(get())
        .build()
}


private fun retrofitHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        retryOnConnectionFailure(true)
        addInterceptor(Interceptor { chain ->
            chain.proceed(chain.request().newBuilder().apply {
                header("Accept", "application/json")
            }.build())
        })
        addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            }
            else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
    }.build()
}