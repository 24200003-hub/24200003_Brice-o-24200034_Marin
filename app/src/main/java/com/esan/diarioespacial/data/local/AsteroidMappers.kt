package com.esan.diarioespacial.data.local

import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.data.remote.dto.NeoDto
import com.esan.diarioespacial.data.remote.dto.NeoFeedResponse
import java.text.SimpleDateFormat
import java.util.Locale

// Conversión explícita DTO (API) -> Entity (Room) de la opción Asteroides.
// Por qué separar DTO y Entity: el DTO refleja el JSON tal cual (campos nulos,
// números en texto, datos anidados); la Entity es lo que guardamos, limpio y plano.
// Si la API cambia, solo se toca este archivo.
//
// Criterio para datos incompletos: sin id el asteroide no se puede guardar (es la
// clave primaria), así que se descarta devolviendo null y usando mapNotNull.
// Si falta un dato secundario se usa un valor por defecto o null.

// El feed no trae una lista sino un objeto agrupado por fecha:
//   { "2026-11-14": [ast1, ast2], "2026-11-13": [ast3] }
// La pantalla necesita una sola lista, así que se aplana con flatMap. La clave
// (feedDate) se pasa como respaldo por si close_approach_data viene vacío.
fun NeoFeedResponse.toAsteroidEntities(): List<AsteroidEntity> =
    nearEarthObjects.orEmpty().flatMap { (feedDate, neos) ->
        neos.orEmpty().mapNotNull { it.toEntity(feedDate) }
    }

fun NeoDto.toEntity(feedDate: String): AsteroidEntity? {
    val safeId = id ?: return null
    // close_approach_data es una lista; en el feed la aproximación del día
    // consultado viene primero. firstOrNull evita un crash si llega vacía.
    val approach = closeApproachData?.firstOrNull()
    val date = approach?.closeApproachDate ?: feedDate
    val meters = estimatedDiameter?.meters

    return AsteroidEntity(
        id = safeId,
        name = name ?: "Desconocido",
        approachDate = date,
        approachEpochMillis = approach?.epochDateCloseApproach ?: dateToMillis(date),
        diameterMinMeters = meters?.estimatedDiameterMin,
        diameterMaxMeters = meters?.estimatedDiameterMax,
        // toDoubleOrNull y no toDouble: si el texto llega vacío o con otro
        // formato devuelve null en vez de lanzar NumberFormatException.
        velocityKmh = approach?.relativeVelocity?.kilometersPerHour?.toDoubleOrNull(),
        missDistanceKm = approach?.missDistance?.kilometers?.toDoubleOrNull(),
        isHazardous = isPotentiallyHazardousAsteroid ?: false
    )
}

// Respaldo cuando no viene el epoch. SimpleDateFormat y no LocalDate porque
// LocalDate requiere API 26 y el proyecto tiene minSdk 24.
// Locale.US para que el parseo no dependa del idioma del teléfono.
private fun dateToMillis(date: String): Long =
    runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)?.time
    }.getOrNull() ?: 0L
