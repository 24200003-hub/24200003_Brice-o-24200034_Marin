package com.esan.diarioespacial.data.remote

import com.esan.diarioespacial.data.remote.dto.ApodDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {

    // Con start_date la API devuelve un ARREGLO (una entrada por día) en lugar de
    // un solo objeto. end_date es opcional: si no se envía, la NASA usa "hoy"
    // según su propio horario. Se deja en null a propósito, porque si el teléfono
    // ya está en el día siguiente (otra zona horaria) y se manda esa fecha, la
    // API responde error 400 por pedir una fecha futura.
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String? = null
    ): List<ApodDto>
}
