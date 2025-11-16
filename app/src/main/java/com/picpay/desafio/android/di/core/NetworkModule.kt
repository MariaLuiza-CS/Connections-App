package com.picpay.desafio.android.di.core

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.BuildConfig
import com.picpay.desafio.android.data.remote.PicPayService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit

val networkModule = module {
    single<OkHttpClient> {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    single<PicPayService> {
        val jsonConfig = Json {
            ignoreUnknownKeys = true
        }

        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl(BuildConfig.PICPAY_SERVICE_BASE_URL)
            .client(get())
            .addConverterFactory(jsonConfig.asConverterFactory(contentType))
            .build()
            .create(PicPayService::class.java)
    }
}
