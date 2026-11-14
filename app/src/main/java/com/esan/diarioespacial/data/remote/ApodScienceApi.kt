package com.esan.diarioespacial.data.remote

import com.esan.diarioespacial.data.remote.dto.ApodScienceDto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Respaldo de APOD. El 29/09/2026 la NASA trasladó APOD a science.nasa.gov y,
// desde entonces, api.nasa.gov/planetary/apod (el endpoint del enunciado)
// devuelve para todas las fechas un marcador: título "NASA Science" y el logo
// de la NASA. ApodRepository consulta primero el endpoint del enunciado y solo
// usa este cuando detecta ese marcador o cuando la llamada falla.
interface ApodScienceApi {

    // Fechas en formato AAMMDD (por ejemplo 261114). Devuelve los días del rango,
    // del más reciente al más antiguo. _fields pide solo los campos que se usan:
    // sin él, cada día trae además la página HTML completa (unas 4 veces más datos).
    @GET("wp-json/wp/v2/apod-basic")
    suspend fun getApodRange(
        @Query("date_from") dateFrom: String,
        @Query("date_to") dateTo: String,
        @Query("per_page") perPage: Int = 25,
        @Query("_fields") fields: String = "date,title,explanation,url,hdurl,media_type,copyright"
    ): List<ApodScienceDto>
}

// Cliente propio para science.nasa.gov: este servidor no usa api_key, así que
// no lleva el interceptor de RetrofitClient y la clave no se envía a otro sitio.
object ScienceNasaClient {

    private const val BASE_URL = "https://science.nasa.gov/"

    val api: ApodScienceApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApodScienceApi::class.java)
    }
}
