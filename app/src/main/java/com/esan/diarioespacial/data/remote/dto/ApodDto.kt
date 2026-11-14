package com.esan.diarioespacial.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO del endpoint APOD: un elemento del arreglo que devuelve la API cuando se
// consulta un rango de fechas. Todos los campos son nullables porque Gson no
// respeta la nulabilidad de Kotlin: hdurl y copyright muchas veces no vienen,
// y en algunos videos tampoco viene url.
data class ApodDto(
    @SerializedName("date") val date: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("hdurl") val hdurl: String?,
    // "image" o "video"; con "video" la url apunta a YouTube/Vimeo, no a una imagen.
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("copyright") val copyright: String?
)
