package com.esan.diarioespacial.data.remote

import com.esan.diarioespacial.data.remote.dto.NeoFeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NeoWsApi {

    // No lleva parámetro api_key: el interceptor de RetrofitClient lo agrega
    // a todas las llamadas. El rango máximo que acepta la API es de 7 días.
    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): NeoFeedResponse
}
