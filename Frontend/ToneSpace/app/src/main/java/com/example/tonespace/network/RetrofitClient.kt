package com.example.tonespace.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.30.249.43/tonespace/"

    // Create a logger that will be active in all builds.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        // Add the logger to the client unconditionally.
        .addInterceptor(logging)
        .build()

    // Create a lenient Gson instance to handle malformed JSON from the server
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // Use the lenient Gson instance for conversion
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthApi::class.java)
    }
}
