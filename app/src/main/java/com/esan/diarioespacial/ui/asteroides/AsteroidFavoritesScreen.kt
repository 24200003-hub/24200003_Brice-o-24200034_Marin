package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.ui.components.CenteredMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidFavoritesScreen(
    viewModel: AsteroidViewModel,
    onAsteroidClick: (AsteroidEntity) -> Unit,
    onBack: () -> Unit
) {
    // Viene de una consulta a Room filtrada por isFavorite, así que al quitar un
    // corazón la fila desaparece sola de esta lista sin código extra.
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Mis asteroides") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            CenteredMessage(
                title = "Todavía no tienes asteroides favoritos",
                subtitle = "Márcalos con el corazón en la lista o en el detalle.",
                emoji = "♡",
                modifier = Modifier.padding(padding)
            )
        } else {
            AsteroidList(
                asteroids = favorites,
                onAsteroidClick = onAsteroidClick,
                onToggleFavorite = viewModel::toggleFavorite,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
