package com.esan.diarioespacial.ui.imagenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.ui.components.CenteredMessage
import com.esan.diarioespacial.ui.components.FavoriteButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApodDetailScreen(
    date: String,
    viewModel: ApodViewModel,
    onBack: () -> Unit
) {
    // remember(date): evita crear un Flow nuevo en cada recomposición.
    // Se lee de Room para que el corazón se actualice al instante.
    val imageFlow = remember(date) { viewModel.getImage(date) }
    val image by imageFlow.collectAsState(initial = null)
    val uriHandler = LocalUriHandler.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    image?.let { img ->
                        FavoriteButton(isFavorite = img.isFavorite, onClick = { viewModel.toggleFavorite(img) })
                    }
                }
            )
        }
    ) { padding ->
        val img = image
        if (img == null) {
            CenteredMessage("No se encontró la imagen.", Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (img.isVideo) {
                VideoPlaceholder(Modifier.fillMaxWidth().aspectRatio(16f / 9f))
            } else {
                // Imagen en grande: hdurl si existe y, si no, url (bestImageUrl).
                // FillWidth respeta la proporción original de la foto (no la recorta
                // como en la lista); heightIn evita un hueco enorme mientras carga.
                ApodImage(
                    url = img.bestImageUrl,
                    contentDescription = img.title,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(img.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    "📅 ${img.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // El autor solo se muestra si la API lo envió (en imágenes de
                // dominio público no viene).
                img.copyright?.let {
                    Text(
                        "📷 Autor: $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                // Para videos se ofrece abrirlo en el navegador o en YouTube,
                // ya que dentro de la app no se puede reproducir con Coil.
                if (img.isVideo && img.url != null) {
                    Button(onClick = { uriHandler.openUri(img.url) }) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text("Ver video")
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    img.explanation.ifBlank { "Sin explicación disponible." },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
