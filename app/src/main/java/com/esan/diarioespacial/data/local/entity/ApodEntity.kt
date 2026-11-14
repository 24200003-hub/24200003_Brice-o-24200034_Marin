package com.esan.diarioespacial.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod")
data class ApodEntity(
    // APOD publica exactamente una entrada por día, así que la fecha ya es única
    // y no hace falta un id autogenerado. Además, al estar en formato AAAA-MM-DD,
    // ordenarla como texto da el mismo resultado que ordenarla como fecha.
    @PrimaryKey
    val date: String,
    val title: String,
    val explanation: String,
    // url es nullable: en algunos videos la API no la manda, y Room no acepta
    // null en una columna NOT NULL (la app se caería al insertar).
    val url: String?,
    val hdUrl: String?,       // solo viene en imágenes, nunca en videos
    val mediaType: String,
    val copyright: String?,   // ausente cuando la imagen es de dominio público
    // El favorito vive en la misma fila y no en otra tabla: así una sola consulta
    // trae los datos y el estado del botón, y la lista se repinta sola al cambiarlo.
    val isFavorite: Boolean = false
) {
    // Propiedades calculadas: Room no las guarda como columnas porque no tienen
    // campo de respaldo; solo existen para que la UI no repita esta lógica.
    val isVideo: Boolean get() = mediaType == "video"
    val bestImageUrl: String? get() = hdUrl ?: url
}
