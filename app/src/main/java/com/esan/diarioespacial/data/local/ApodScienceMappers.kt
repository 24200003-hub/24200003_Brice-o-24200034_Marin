package com.esan.diarioespacial.data.local

import androidx.core.text.HtmlCompat
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.remote.dto.ApodDto
import com.esan.diarioespacial.data.remote.dto.ApodScienceDto

// Conversión DTO -> Entity del respaldo de APOD (science.nasa.gov).
// Llena la misma ApodEntity que el endpoint del enunciado, así que Room y las
// pantallas no cambian.

// Título que devuelve api.nasa.gov/planetary/apod en lugar del APOD real.
const val NASA_PLACEHOLDER_TITLE = "NASA Science"

// true si un elemento del endpoint del enunciado es el marcador de la NASA.
fun ApodDto.isNasaPlaceholder(): Boolean =
    title?.trim().equals(NASA_PLACEHOLDER_TITLE, ignoreCase = true)

fun ApodScienceDto.toEntity(): ApodEntity? {
    val safeDate = date ?: return null
    val isVideo = mediaType == "video"
    // hdurl apunta a un servicio que redimensiona la imagen con ?w=...: se piden
    // 800 px de ancho para la lista y 1600 px para el detalle (fit=max no la agranda).
    val imageBase = hdurl?.substringBefore('?')?.takeIf { it.isNotBlank() }
    return ApodEntity(
        date = safeDate,
        title = title?.htmlToText()?.ifBlank { null } ?: "Sin título",
        explanation = explanation?.htmlToText()
            ?.removePrefix("Explanation:")
            // Al final viene un adelanto del día siguiente ("Tomorrow's picture: ...").
            ?.substringBefore("Tomorrow's picture:")
            ?.substringBefore("Tomorrow’s picture:")
            ?.trim()
            .orEmpty(),
        // En los videos url es la página del artículo: el botón "Ver video" la abre.
        url = if (isVideo) url else imageBase?.let { "$it?w=800&fit=max" },
        hdUrl = if (isVideo) null else imageBase?.let { "$it?w=1600&fit=max" },
        mediaType = mediaType ?: "image",
        copyright = copyright?.htmlToText()?.ifBlank { null }
    )
}

fun List<ApodScienceDto>.toApodEntitiesFromScience(): List<ApodEntity> =
    mapNotNull { it.toEntity() }

// Quita etiquetas y entidades HTML (<a>, <strong>, &#039;) y deja texto plano.
private fun String.htmlToText(): String =
    HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .replace(Regex("\\s+"), " ")
        .trim()
