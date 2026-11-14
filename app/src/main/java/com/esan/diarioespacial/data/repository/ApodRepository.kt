package com.esan.diarioespacial.data.repository

import com.esan.diarioespacial.data.local.dao.ApodDao
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.local.toApodEntities
import com.esan.diarioespacial.data.remote.ApodApi
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Único punto de acceso a los datos de APOD. Igual que en Asteroides:
// la API solo escribe en Room (refresh) y la UI solo lee de Room (los Flow).
class ApodRepository(
    private val api: ApodApi,
    private val dao: ApodDao
) {

    fun getImages(): Flow<List<ApodEntity>> = dao.getAll()

    fun getFavorites(): Flow<List<ApodEntity>> = dao.getFavorites()

    fun getImage(date: String): Flow<ApodEntity?> = dao.getByDate(date)

    suspend fun setFavorite(date: String, isFavorite: Boolean) =
        dao.setFavorite(date, isFavorite)

    // Últimos 7 días = desde hace 6 días hasta hoy. Solo se envía la fecha de
    // inicio; la API completa hasta hoy (ver ApodApi).
    suspend fun refresh() {
        val response = api.getApod(startDate = sixDaysAgo())
        dao.saveFromApi(response.toApodEntities())
    }

    // Calendar en vez de LocalDate porque minSdk es 24 (LocalDate pide API 26).
    private fun sixDaysAgo(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }
}
