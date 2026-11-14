package com.esan.diarioespacial.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.esan.diarioespacial.data.local.dao.ApodDao
import com.esan.diarioespacial.data.local.dao.AsteroidDao
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.local.entity.AsteroidEntity

// Una sola base de datos con dos tablas (una por opción), como pide el enunciado.
// exportSchema = false: no se genera el JSON del esquema porque no se manejan
// migraciones versionadas en este proyecto (evita también un warning de compilación).
@Database(
    entities = [ApodEntity::class, AsteroidEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Son abstractas porque Room genera la implementación en tiempo de
    // compilación (con KSP); nosotros solo declaramos qué DAOs existen.
    abstract fun apodDao(): ApodDao
    abstract fun asteroidDao(): AsteroidDao

    companion object {
        // Singleton: abrir la base de datos es costoso y tener dos instancias
        // abiertas al mismo archivo puede dar datos inconsistentes.
        // @Volatile hace que, si un hilo crea la instancia, los demás la vean
        // de inmediato y no usen una copia vieja en caché.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            // Doble verificación: el primer "?:" evita entrar al synchronized
            // (que es lento) cuando ya existe; el segundo evita que dos hilos que
            // entraron a la vez creen dos instancias.
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    // applicationContext y no el de una Activity: el singleton vive
                    // toda la app y guardar una Activity causaría una fuga de memoria.
                    context.applicationContext,
                    AppDatabase::class.java,
                    "diario_espacial.db"
                )
                    // Si se cambia una Entity y se sube "version", Room borra y
                    // recrea las tablas en vez de cerrarse por falta de migración.
                    // Aceptable aquí porque los datos se pueden volver a descargar
                    // (solo se perderían los favoritos).
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
