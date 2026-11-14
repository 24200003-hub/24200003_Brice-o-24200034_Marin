# Diario Espacial 🚀

Aplicación Android desarrollada en Kotlin y Jetpack Compose que consume la API
abierta de la NASA (APOD y NeoWs) y almacena los datos localmente con Room.

**Curso:** Desarrollo de Aplicaciones Móviles – Ciclo 2026-2
**Evaluación:** Práctica Calificada 2 (en pares)

## Integrantes
| Rol | Nombre                     | Código | Opción |
|---|----------------------------|---|---|
| Alumno 1 | Nino Fermín Briceño Huamán | 24200003 | Imágenes (APOD) |
| Alumno 2 | Gabriela Sofia Marín Soto  | 24200034 | Asteroides (NeoWs) |

## Tecnologías
Kotlin · Jetpack Compose · Navigation Compose · Retrofit + Gson · Room · Coil · StateFlow

## Cómo ejecutar
1. Clonar el repositorio.
2. Generar una API key en https://api.nasa.gov
3. Abrir el archivo `local.properties` (raíz del proyecto) y agregar: NASA_API_KEY=tu_clave
4. Sincronizar Gradle (Sync Now) y ejecutar la app.

> La clave **no** se sube al repositorio: `local.properties` está en `.gitignore`
> y la clave se lee desde el código con `BuildConfig.NASA_API_KEY`.

## Acuerdo de coordinación
- **Base común (Retrofit, API key, dependencias, README):** preparada por
  Fermín Briceño en la rama `feature/base-api` (PR #1), revisada por [compañero].
- **AppDatabase y navegación principal (barra inferior):** [quién]
- Cada integrante trabaja en su propia rama (`feature/imagenes`, `feature/asteroides`)
  y en archivos propios. La base común no se modifica sin avisar al otro.
- Cada PR es revisado y aprobado por el otro integrante antes del merge a `main`.

## Uso del cliente Retrofit
`RetrofitClient` agrega automáticamente el parámetro `api_key` a todas las llamadas.
Cada integrante crea su propia interfaz:

```kotlin
interface ApodApi {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String? = null
    ): List<ApodDto>
}

val api = RetrofitClient.create<ApodApi>()
```