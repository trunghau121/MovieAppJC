package com.movieappjc.data.remote.di

import android.content.Context
import com.core_app.base.app.NetworkConfig
import com.core_app.network.createHttpLoggingInterceptor
import com.core_app.network.createHttpRequestInterceptor
import com.core_app.network.createMoshi
import com.core_app.network.createOkHttpClient
import com.core_app.network.createRetrofitWithMoshi
import com.core_app.network.interceptor.HttpRequestInterceptor
import com.movieappjc.data.remote.MovieServices
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

private const val BASE_URL = "base_url"

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Provides
    @Singleton
    @Named(value = BASE_URL)
    fun provideBaseUrl(networkConfig: NetworkConfig): String {
        return networkConfig.baseUrl()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return createMoshi()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(networkConfig: NetworkConfig): HttpLoggingInterceptor {
        return createHttpLoggingInterceptor(isDev = networkConfig.isDev())
    }

    @Provides
    @Singleton
    fun provideHttpRequestInterceptor(): HttpRequestInterceptor {
        return createHttpRequestInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        httpRequestInterceptor: HttpRequestInterceptor
    ): OkHttpClient {
        return createOkHttpClient(
            isCache = true,
            interceptors = arrayOf(
                httpLoggingInterceptor,
                httpRequestInterceptor
            ),
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideMovieServices(
        @Named(value = BASE_URL) baseUrl: String,
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ) = createRetrofitWithMoshi<MovieServices>(
        okHttpClient = okHttpClient,
        moshi = moshi,
        baseUrl = baseUrl
    )
}