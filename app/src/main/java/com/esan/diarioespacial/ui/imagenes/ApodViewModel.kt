package com.esan.diarioespacial.ui.imagenes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.esan.diarioespacial.data.local.AppDatabase
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.data.remote.ApodApi
import com.esan.diarioespacial.data.remote.RetrofitClient
import com.esan.diarioespacial.data.remote.ScienceNasaClient
import com.esan.diarioespacial.data.repository.ApodRepository
import com.esan.diarioespacial.ui.components.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ApodUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

// AndroidViewModel porque necesita el Context de la aplicación para abrir Room.
// Una sola instancia la comparten lista, detalle y favoritos (ver ImagenesNavHost).
class ApodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApodRepository(
        api = RetrofitClient.create<ApodApi>(),
        backupApi = ScienceNasaClient.api,
        dao = AppDatabase.getInstance(application).apodDao()
    )

    private val _uiState = MutableStateFlow(ApodUiState())
    val uiState: StateFlow<ApodUiState> = _uiState.asStateFlow()

    // stateIn convierte el Flow de Room en StateFlow. WhileSubscribed(5000)
    // mantiene la consulta viva 5 s sin observadores (p. ej. al girar el teléfono).
    val images: StateFlow<List<ApodEntity>> = repository.getImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favorites: StateFlow<List<ApodEntity>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.refresh()
            } catch (e: CancellationException) {
                throw e // cancelación normal de la corrutina: no es un error
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.toUserMessage()) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleFavorite(image: ApodEntity) {
        viewModelScope.launch {
            repository.setFavorite(image.date, !image.isFavorite)
        }
    }

    fun getImage(date: String): Flow<ApodEntity?> = repository.getImage(date)
}
