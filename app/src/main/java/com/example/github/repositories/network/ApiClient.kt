package com.example.github.repositories.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://api.github.com/"
    private lateinit var retrofit: Retrofit

    private val logging =  HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // OkHttpClient. Be conscious with the order
    var okHttpClient: OkHttpClient = OkHttpClient()
        .newBuilder() //httpLogging interceptor for logging network requests
        .addInterceptor(logging)
        .build()

    val retrofitInstance: Retrofit
        get() {
            if (!::retrofit.isInitialized) {
                retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

}