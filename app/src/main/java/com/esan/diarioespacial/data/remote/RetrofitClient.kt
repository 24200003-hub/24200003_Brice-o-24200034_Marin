package com.esan.diarioespacial.data.remote

import com.esan.diarioespacial.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://api.nasa.gov/"

    // Agrega api_key a TODAS las llamadas automáticamente
    private val apiKeyInterceptor = Interceptor { chain ->
        val original = chain.request()
        val url = original.url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.NASA_API_KEY)
            .build()
        chain.proceed(original.newBuilder().url(url).build())
    }

    // Muestra las peticiones en Logcat solo en modo debug
    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
    }

    // El orden importa: los interceptores se ejecutan en el orden en que se agregan.
    // El logging va primero para que registre la URL ANTES de que se le agregue
    // api_key; así la clave no aparece en Logcat.
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(apiKeyInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Uso: val api = RetrofitClient.create<ApodApi>()
    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}