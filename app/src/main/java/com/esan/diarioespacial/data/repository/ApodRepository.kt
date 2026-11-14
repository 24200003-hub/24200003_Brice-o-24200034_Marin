package com.esan.diarioespacial.data.repository

import android.util.Log
import com.esan.diarioespacial.data.local.dao.ApodDao
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.local.isNasaPlaceholder
import com.esan.diarioespacial.data.local.toApodEntities
import com.esan.diarioespacial.data.local.toApodEntitiesFromScience
import com.esan.diarioespacial.data.remote.ApodApi
import com.esan.diarioespacial.data.remote.ApodScienceApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Único punto de acceso a los datos de APOD. Igual que en Asteroides:
// la API solo escribe en Room (refresh) y la UI solo lee de Room (los Flow).
class ApodRepository(
    private val api: ApodApi,
    private val backupApi: ApodScienceApi,
    private val dao: ApodDao
) {

    fun getImages(): Flow<List<ApodEntity>> = dao.getAll()

    fun getFavorites(): Flow<List<ApodEntity>> = dao.getFavorites()

    fun getImage(date: String): Flow<ApodEntity?> = dao.getByDate(date)

    suspend fun setFavorite(date: String, isFavorite: Boolean) =
        dao.setFavorite(date, isFavorite)

    // Últimos 7 días = desde hace 6 días hasta hoy.
    // 1) Se consulta el endpoint del enunciado (api.nasa.gov, con api_key).
    // 2) Si responde con el marcador de la NASA o falla, se usa el respaldo de
    //    science.nasa.gov (ver ApodScienceApi).
    // En los dos casos se guarda una ApodEntity, así que la UI sigue leyendo solo de Room.
    suspend fun refresh() {
        val official = fetchOfficial()
        if (official != null) {
            dao.saveFromApi(official)
            return
        }
        val backup = backupApi.getApodRange(
            dateFrom = date(daysAgo = 6, pattern = "yyMMdd"),
            dateTo = date(daysAgo = 0, pattern = "yyMMdd")
        ).toApodEntitiesFromScience()
        dao.deleteNasaPlaceholders()
        dao.saveFromApi(backup)
    }

    // Endpoint del enunciado. Devuelve null cuando hay que usar el respaldo.
    // Solo se envía la fecha de inicio; la API completa hasta hoy (ver ApodApi).
    private suspend fun fetchOfficial(): List<ApodEntity>? {
        val response = try {
            api.getApod(startDate = date(daysAgo = 6, pattern = "yyyy-MM-dd"))
        } catch (e: CancellationException) {
            throw e // cancelación normal de la corrutina: no es un error
        } catch (e: Exception) {
            // Por ejemplo, cuando la NASA archive este endpoint.
            Log.w(TAG, "Falló api.nasa.gov/planetary/apod; se usa el respaldo", e)
            return null
        }
        // El marcador repite "NASA Science" y la misma imagen (el logo) en todas las fechas.
        val isPlaceholder = response.isEmpty() ||
            response.any { it.isNasaPlaceholder() } ||
            (response.size > 1 && response.map { it.url }.distinct().size == 1)
        if (isPlaceholder) {
            Log.w(TAG, "api.nasa.gov devolvió el marcador de la NASA; se usa el respaldo")
            return null
        }
        return response.toApodEntities()
    }

    // Calendar en vez de LocalDate porque minSdk es 24 (LocalDate pide API 26).
    // pattern: "yyyy-MM-dd" para api.nasa.gov y "yyMMdd" para science.nasa.gov.
    private fun date(daysAgo: Int, pattern: String): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return SimpleDateFormat(pattern, Locale.US).format(calendar.time)
    }

    private companion object {
        const val TAG = "ApodRepository"
    }
}
