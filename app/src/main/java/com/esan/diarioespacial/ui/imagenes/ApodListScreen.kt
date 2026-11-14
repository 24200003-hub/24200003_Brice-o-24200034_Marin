package com.esan.diarioespacial.ui.imagenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.ui.components.CenteredMessage
import com.esan.diarioespacial.ui.components.ErrorBanner
import com.esan.diarioespacial.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodListScreen(
    viewModel: ApodViewModel,
    onImageClick: (ApodEntity) -> Unit,
    onFavoritesClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val images by viewModel.images.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Imágenes", fontWeight = FontWeight.Bold)
                        Text(
                            "Imagen astronómica del día · últimos 7 días",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    // Botón en la parte superior que abre "Mis imágenes favoritas".
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Mis imágenes favoritas",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading && images.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }
            uiState.errorMessage?.let { ErrorBanner(it) }

            when {
                uiState.isLoading && images.isEmpty() ->
                    LoadingBox("Consultando a la NASA…")

                images.isEmpty() ->
                    CenteredMessage(
                        title = "No hay imágenes para mostrar",
                        subtitle = "Toca actualizar para volver a consultar.",
                        emoji = "🌌"
                    )

                else ->
                    ApodList(
                        images = images,
                        onImageClick = onImageClick,
                        onToggleFavorite = viewModel::toggleFavorite
                    )
            }
        }
    }
}
