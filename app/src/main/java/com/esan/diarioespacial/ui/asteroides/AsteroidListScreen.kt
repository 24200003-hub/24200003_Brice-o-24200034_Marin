package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.ui.components.CenteredMessage
import com.esan.diarioespacial.ui.components.ErrorBanner
import com.esan.diarioespacial.ui.components.LoadingBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidListScreen(
    viewModel: AsteroidViewModel,
    onAsteroidClick: (AsteroidEntity) -> Unit,
    onFavoritesClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val asteroids by viewModel.asteroids.collectAsState()

    // contentWindowInsets = 0: la barra inferior de MainActivity ya reserva el
    // espacio de abajo; sin esto el espacio se sumaría dos veces.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Asteroides", fontWeight = FontWeight.Bold)
                        Text(
                            "Objetos cercanos a la Tierra · últimos 7 días",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onFavoritesClick) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Mis asteroides",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Solo potencialmente peligrosos", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${asteroids.size} asteroides en la lista",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.onlyHazardous,
                        onCheckedChange = viewModel::setOnlyHazardous
                    )
                }
            }

            // Si ya hay datos guardados mientras se actualiza, se muestra una barra
            // fina y se mantiene la lista; el indicador grande solo aparece cuando
            // todavía no hay nada que mostrar.
            if (uiState.isLoading && asteroids.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            }
            uiState.errorMessage?.let { ErrorBanner(it) }

            when {
                uiState.isLoading && asteroids.isEmpty() ->
                    LoadingBox("Consultando a la NASA…")

                asteroids.isEmpty() ->
                    if (uiState.onlyHazardous) {
                        CenteredMessage(
                            title = "No hay asteroides potencialmente peligrosos",
                            subtitle = "En los últimos 7 días ninguno fue clasificado como peligroso.",
                            emoji = "🛡"
                        )
                    } else {
                        CenteredMessage(
                            title = "No hay asteroides para mostrar",
                            subtitle = "Toca actualizar para volver a consultar.",
                            emoji = "☄"
                        )
                    }

                else ->
                    AsteroidList(
                        asteroids = asteroids,
                        onAsteroidClick = onAsteroidClick,
                        onToggleFavorite = viewModel::toggleFavorite
                    )
            }
        }
    }
}
