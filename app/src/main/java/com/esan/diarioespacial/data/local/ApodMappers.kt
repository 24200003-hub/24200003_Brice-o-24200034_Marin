package com.esan.diarioespacial.data.local

import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.remote.dto.ApodDto

// Conversión explícita DTO (API) -> Entity (Room) de la opción Imágenes.
// Sin fecha no se puede guardar (es la clave primaria), así que ese elemento se
// descarta devolviendo null y usando mapNotNull, en vez de cerrar la app.

fun ApodDto.toEntity(): ApodEntity? {
    val safeDate = date ?: return null
    return ApodEntity(
        date = safeDate,
        title = title ?: "Sin título",
        explanation = explanation ?: "",
        url = url,
        hdUrl = hdurl,
        mediaType = mediaType ?: "image",
        // La NASA a veces envía el autor con saltos de línea al inicio o al final.
        // Si queda vacío se guarda null para que la pantalla no muestre "Autor: ".
        copyright = copyright?.trim()?.ifBlank { null }
    )
}

fun List<ApodDto>.toApodEntities(): List<ApodEntity> = mapNotNull { it.toEntity() }
