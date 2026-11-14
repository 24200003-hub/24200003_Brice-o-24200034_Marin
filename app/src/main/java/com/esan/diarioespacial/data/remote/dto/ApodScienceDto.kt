package com.esan.diarioespacial.data.remote.dto

import com.google.gson.annotations.SerializedName

// DTO del endpoint de respaldo de APOD (science.nasa.gov/wp-json/wp/v2/apod-basic).
// Trae los mismos datos que el endpoint del enunciado, con estas diferencias:
// - url es la página del artículo en science.nasa.gov; la imagen viene en hdurl.
// - title, explanation y copyright pueden traer HTML (enlaces, negritas).
// Igual que ApodDto, todos los campos son nullables para que la app no se cierre
// si la NASA no envía alguno.
data class ApodScienceDto(
    @SerializedName("date") val date: String?,          // AAAA-MM-DD
    @SerializedName("title") val title: String?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("hdurl") val hdurl: String?,
    // "image" o "video"; en los videos hdurl es solo una miniatura.
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("copyright") val copyright: String?
)
