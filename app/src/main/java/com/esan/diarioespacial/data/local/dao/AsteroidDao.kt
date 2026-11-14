package com.esan.diarioespacial.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AsteroidDao {

    // El filtro "Solo potencialmente peligrosos" se resuelve en SQL con un solo
    // query en lugar de dos métodos. Room guarda los Boolean como 0/1, así que:
    //   onlyHazardous = false -> (1 OR ...) es siempre verdadero -> trae todos
    //   onlyHazardous = true  -> (0 OR isHazardous = 1)          -> solo peligrosos
    // Se ordena por approachEpochMillis y no por approachDate porque la fecha en
    // texto empata entre asteroides del mismo día.
    @Query(
        """
        SELECT * FROM asteroids
        WHERE (:onlyHazardous = 0 OR isHazardous = 1)
        ORDER BY approachEpochMillis DESC
        """
    )
    fun getAll(onlyHazardous: Boolean = false): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroids WHERE isFavorite = 1 ORDER BY approachEpochMillis DESC")
    fun getFavorites(): Flow<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroids WHERE id = :id")
    fun getById(id: String): Flow<AsteroidEntity?>

    @Query("UPDATE asteroids SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT id FROM asteroids WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AsteroidEntity>)

    // Igual que en ApodDao: REPLACE reescribe la fila con isFavorite = false,
    // así que se recuperan los favoritos antes de insertar. El Repository debe
    // llamar a este método y no a insertAll.
    @Transaction
    suspend fun saveFromApi(items: List<AsteroidEntity>) {
        val favorites = getFavoriteIds().toSet()
        insertAll(items.map { it.copy(isFavorite = it.id in favorites) })
    }
}
