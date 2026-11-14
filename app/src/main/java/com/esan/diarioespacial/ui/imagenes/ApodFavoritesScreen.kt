package com.esan.diarioespacial.ui.imagenes

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
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.ui.components.CenteredMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodFavoritesScreen(
    viewModel: ApodViewModel,
    onImageClick: (ApodEntity) -> Unit,
    onBack: () -> Unit
) {
    // Consulta a Room filtrada por isFavorite: al quitar el corazón la tarjeta
    // desaparece sola de esta pantalla.
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Mis imágenes favoritas") },
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
                title = "Todavía no tienes imágenes favoritas",
                subtitle = "Márcalas con el corazón en la lista o en el detalle.",
                emoji = "♡",
                modifier = Modifier.padding(padding)
            )
        } else {
            ApodList(
                images = favorites,
                onImageClick = onImageClick,
                onToggleFavorite = viewModel::toggleFavorite,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
