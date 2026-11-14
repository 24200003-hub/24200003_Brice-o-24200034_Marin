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
  Fermín Briceño en la rama `feature/base-api` (PR #2), revisada por Gabriela Marín.
- **Base de datos (Room: `AppDatabase`, entidades y DAOs de las dos tablas):** preparada por
  Gabriela Marín en la rama `feature/room` (PR #1), revisada por Fermín Briceño.
- **Navegación principal (barra inferior):** creada en `MainActivity` junto con la opción
  Asteroides. Cada pestaña llama al NavHost propio de su opción; la opción Imágenes
  reemplaza su marcador de posición por su propio NavHost.
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

## Opción Imágenes: respaldo de APOD
El 29/09/2026 la NASA trasladó APOD a science.nasa.gov. Desde entonces el endpoint
del enunciado, `api.nasa.gov/planetary/apod`, devuelve para todas las fechas un
marcador (título "NASA Science" y el logo de la NASA) en lugar de la imagen del día,
y está previsto que se archive el 01/12/2026.

Para que la app siga mostrando las imágenes reales, `ApodRepository`:
1. Consulta primero `api.nasa.gov/planetary/apod` con la API key, como pide el enunciado.
2. Si la respuesta es el marcador o la llamada falla, consulta el respaldo
   `science.nasa.gov/wp-json/wp/v2/apod-basic`, que no usa API key.
3. En los dos casos convierte el DTO a `ApodEntity` y lo guarda en Room; las pantallas
   siguen leyendo solo desde Room.

| Capa | Archivo |
|---|---|
| Servicio y DTO del respaldo | `data/remote/ApodScienceApi.kt`, `data/remote/dto/ApodScienceDto.kt` |
| DTO → Entity y detección del marcador | `data/local/ApodScienceMappers.kt` |
| Elección entre el endpoint del enunciado y el respaldo | `data/repository/ApodRepository.kt` |

## Opción Asteroides (Alumno 2)
Consulta el feed de NeoWs de los últimos 7 días, lo guarda en Room y las pantallas
leen siempre desde Room.

| Capa | Archivo |
|---|---|
| DTO | `data/remote/dto/NeoWsDtos.kt` |
| Servicio Retrofit | `data/remote/NeoWsApi.kt` |
| DTO → Entity | `data/local/AsteroidMappers.kt` |
| Entity y DAO | `data/local/entity/AsteroidEntity.kt`, `data/local/dao/AsteroidDao.kt` |
| Repository | `data/repository/AsteroidRepository.kt` |
| ViewModel (StateFlow) | `ui/asteroides/AsteroidViewModel.kt` |
| Pantallas | `ui/asteroides/` (lista con filtro, detalle, "Mis asteroides") |
| Navegación interna | `ui/asteroides/AsteroidesNavHost.kt` |

