package com.esan.diarioespacial.data.repository

import com.esan.diarioespacial.data.local.dao.AsteroidDao
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.data.local.toAsteroidEntities
import com.esan.diarioespacial.data.remote.NeoWsApi
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Único punto de acceso a los datos de asteroides. El ViewModel no sabe si los
// datos vienen de la API o de Room: eso queda encapsulado aquí.
//
// Flujo: la API solo escribe en Room (refresh) y la UI solo lee de Room (los Flow).
// Así la pantalla se actualiza sola cuando llegan datos nuevos y sigue mostrando
// lo último guardado aunque no haya internet.
class AsteroidRepository(
    private val api: NeoWsApi,
    private val dao: AsteroidDao
) {

    fun getAsteroids(onlyHazardous: Boolean): Flow<List<AsteroidEntity>> =
        dao.getAll(onlyHazardous)

    fun getFavorites(): Flow<List<AsteroidEntity>> = dao.getFavorites()

    fun getAsteroid(id: String): Flow<AsteroidEntity?> = dao.getById(id)

    suspend fun setFavorite(id: String, isFavorite: Boolean) =
        dao.setFavorite(id, isFavorite)

    // No devuelve nada: el resultado llega a la UI a través de los Flow de Room.
    // Si la llamada falla, la excepción sube al ViewModel para mostrar el mensaje.
    suspend fun refresh() {
        val (start, end) = lastSevenDays()
        val response = api.getFeed(start, end)
        dao.saveFromApi(response.toAsteroidEntities())
    }

    // "Últimos 7 días" = desde hace 6 días hasta hoy (7 días contando hoy),
    // que además es el rango máximo que permite el feed.
    // Calendar en vez de LocalDate porque minSdk es 24 (LocalDate pide API 26).
    private fun lastSevenDays(): Pair<String, String> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        val end = format.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val start = format.format(calendar.time)
        return start to end
    }
}
