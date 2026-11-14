package com.esan.diarioespacial.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asteroids")
data class AsteroidEntity(
    // Se usa el id de la NASA (no uno autogenerado) para que, si el mismo asteroide
    // vuelve a llegar en otra consulta, se actualice su fila en vez de duplicarse.
    @PrimaryKey
    val id: String,
    val name: String,
    val approachDate: String,
    // Columna extra solo para ordenar. Se guarda el instante exacto en milisegundos
    // porque varios asteroides comparten el mismo día y la fecha en texto no
    // permite saber cuál pasó primero.
    val approachEpochMillis: Long,
    // Se guardan como número (Double) aunque la API los manda como texto: así se
    // convierten una sola vez al guardar y no cada vez que se dibuja la lista.
    // Son nullables para que un dato faltante no tumbe el insert.
    // El redondeo a 2 decimales se hace en la UI, no aquí, para no perder precisión.
    val diameterMinMeters: Double?,
    val diameterMaxMeters: Double?,
    val velocityKmh: Double?,
    val missDistanceKm: Double?,
    val isHazardous: Boolean,
    val isFavorite: Boolean = false
)
