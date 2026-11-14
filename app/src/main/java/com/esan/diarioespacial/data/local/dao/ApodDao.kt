package com.esan.diarioespacial.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.esan.diarioespacial.data.local.entity.ApodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    // Las lecturas devuelven Flow y no List: Room vuelve a emitir automáticamente
    // cada vez que la tabla cambia. Gracias a eso la pantalla se actualiza sola
    // cuando llega la respuesta de la API o cuando se marca un favorito, que es
    // justo lo que pide el enunciado ("la interfaz se actualiza a partir de lo
    // almacenado"). Por eso tampoco son suspend: Flow ya es asíncrono.
    @Query("SELECT * FROM apod ORDER BY date DESC")
    fun getAll(): Flow<List<ApodEntity>>

    @Query("SELECT * FROM apod WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavorites(): Flow<List<ApodEntity>>

    // Nullable porque el detalle puede abrirse antes de que la fila exista
    // (o después de que se borre) y no queremos un crash en ese caso.
    @Query("SELECT * FROM apod WHERE date = :date")
    fun getByDate(date: String): Flow<ApodEntity?>

    // UPDATE de una sola columna en vez de reinsertar el objeto completo:
    // no hace falta tener la entidad a mano y no se pisa ningún otro campo.
    @Query("UPDATE apod SET isFavorite = :isFavorite WHERE date = :date")
    suspend fun setFavorite(date: String, isFavorite: Boolean)

    @Query("SELECT date FROM apod WHERE isFavorite = 1")
    suspend fun getFavoriteDates(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ApodEntity>)

    // Borra las filas que se guardaron con el marcador de la NASA ("NASA Science"
    // y el logo) antes de existir el respaldo de science.nasa.gov. Las favoritas
    // se conservan porque el usuario las marcó.
    @Query("DELETE FROM apod WHERE title = 'NASA Science' AND isFavorite = 0")
    suspend fun deleteNasaPlaceholders()

    // Problema que resuelve: REPLACE borra la fila vieja e inserta la nueva, y la
    // nueva viene de la API con isFavorite = false. Si se llamara a insertAll
    // directamente, cada vez que se abre la pantalla se perderían los favoritos.
    // Por eso primero se leen los favoritos y se copian a los datos nuevos.
    // @Transaction asegura que leer + insertar ocurra como una sola operación:
    // si el usuario marca un favorito justo en medio, no se pierde.
    @Transaction
    suspend fun saveFromApi(items: List<ApodEntity>) {
        val favorites = getFavoriteDates().toSet()   // Set: búsqueda O(1) en el map
        insertAll(items.map { it.copy(isFavorite = it.date in favorites) })
    }
}
