package com.esan.diarioespacial.ui.asteroides

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esan.diarioespacial.data.local.AppDatabase
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.data.remote.NeoWsApi
import com.esan.diarioespacial.data.remote.RetrofitClient
import com.esan.diarioespacial.data.repository.AsteroidRepository
import com.esan.diarioespacial.ui.components.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la pantalla que no viene de Room (la lista sí viene de Room).
data class AsteroidUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val onlyHazardous: Boolean = false
)

// AndroidViewModel (y no ViewModel) porque necesita el Context de la aplicación
// para abrir la base de datos. Como el sistema sabe crear AndroidViewModel por sí
// solo, no hace falta escribir una Factory.
// Una sola instancia se comparte entre lista, detalle y favoritos (ver
// AsteroidesNavHost), así el filtro y la carga no se repiten al navegar.
@OptIn(ExperimentalCoroutinesApi::class)
class AsteroidViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AsteroidRepository(
        api = RetrofitClient.create<NeoWsApi>(),
        dao = AppDatabase.getInstance(application).asteroidDao()
    )

    // Mutable solo aquí dentro; la UI recibe la versión de solo lectura para que
    // no pueda cambiar el estado sin pasar por las funciones del ViewModel.
    private val _uiState = MutableStateFlow(AsteroidUiState())
    val uiState: StateFlow<AsteroidUiState> = _uiState.asStateFlow()

    // Cada vez que cambia el interruptor, flatMapLatest cancela la consulta
    // anterior a Room y se suscribe a la nueva (con o sin filtro).
    // stateIn convierte el Flow de Room en StateFlow, como pide el enunciado.
    // WhileSubscribed(5000): si la pantalla deja de observar (p. ej. al girar el
    // teléfono) espera 5 s antes de cortar la consulta, así no se reinicia en cada giro.
    val asteroids: StateFlow<List<AsteroidEntity>> = _uiState
        .flatMapLatest { state -> repository.getAsteroids(state.onlyHazardous) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favorites: StateFlow<List<AsteroidEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // init: la consulta a la API se hace una sola vez al crear el ViewModel y no
    // cada vez que la pantalla se recompone o vuelve del detalle.
    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.refresh()
            } catch (e: CancellationException) {
                // Se relanza: es la señal normal de que la corrutina se canceló
                // (p. ej. al cerrar la pantalla); tragarla rompería la cancelación.
                throw e
            } catch (e: Exception) {
                // Sin internet, key inválida o respuesta inesperada: no se cierra la
                // app, se muestra un mensaje y se siguen viendo los datos guardados.
                _uiState.update { it.copy(errorMessage = e.toUserMessage()) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun setOnlyHazardous(value: Boolean) {
        _uiState.update { it.copy(onlyHazardous = value) }
    }

    fun toggleFavorite(asteroid: AsteroidEntity) {
        viewModelScope.launch {
            repository.setFavorite(asteroid.id, !asteroid.isFavorite)
        }
    }

    fun getAsteroid(id: String): Flow<AsteroidEntity?> = repository.getAsteroid(id)
}
