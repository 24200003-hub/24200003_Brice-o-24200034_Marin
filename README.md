# Diario Espacial 🚀

Aplicación Android desarrollada en Kotlin y Jetpack Compose que consume la API
abierta de la NASA (APOD y NeoWs) y almacena los datos localmente con Room.

**Curso:** Desarrollo de Aplicaciones Móviles – Ciclo 2026-2
**Evaluación:** Práctica Calificada 2 (en pares)

## Integrantes
| Rol | Nombre | Código | Opción |
|---|---|---|---|
| Alumno 1 | Nino Fermín Briceño Huamán | 24200003 | Imágenes (APOD) |
| Alumno 2 | Gabriela Marín | 24200034 | Asteroides (NeoWs) |

## Tecnologías
Kotlin · Jetpack Compose · Navigation Compose · Retrofit + Gson · OkHttp ·
Room (KSP) · Coil · ViewModel + StateFlow

## Cómo ejecutar
1. Clonar el repositorio.
2. Generar una API key en https://api.nasa.gov (sección *Generate API Key*).
3. Abrir el archivo `local.properties` (raíz del proyecto) y agregar:
   ```
   NASA_API_KEY=tu_clave
   ```
4. Sincronizar Gradle (*Sync Now*) y ejecutar la app.

> La clave **no** se sube al repositorio: `local.properties` está en `.gitignore`
> y la clave se lee desde el código con `BuildConfig.NASA_API_KEY`.
> `RetrofitClient` la agrega automáticamente a cada llamada y Logcat registra
> las peticiones sin mostrarla.

## Acuerdo de coordinación
- **Capa de Room (`AppDatabase`, entidades y DAOs de ambas opciones):**
  Gabriela Marín, en el PR #1 *Capa SQLite con Room (tablas apod y asteroids)*,
  revisado por Fermín Briceño.
- **Base común de red (Retrofit, API key, dependencias, README):**
  Fermín Briceño, en el PR #2 *Base común: Retrofit, API key y dependencias*,
  revisado por Gabriela Marín.
- **Navegación principal (barra inferior):** Fermín Briceño.
- Las dependencias se declaran en el catálogo `gradle/libs.versions.toml`.
- Cada integrante trabaja en su propia rama (`feature/imagenes`, `feature/asteroides`)
  y en archivos propios. La base común no se modifica sin avisar al otro.
- Cada PR es revisado y aprobado por el otro integrante antes del merge a `main`.

## Uso del cliente Retrofit
Cada integrante crea su propia interfaz y la obtiene desde `RetrofitClient`:

```kotlin
interface ApodApi {
    @GET("planetary/apod")
    suspend fun getApodRange(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String? = null
    ): List<ApodDto>
}

val api = RetrofitClient.create<ApodApi>()
```

Los DTO usan campos *nullable* (`String?`, `Double?`) para que la app no se
cierre cuando la API no envía algún dato.