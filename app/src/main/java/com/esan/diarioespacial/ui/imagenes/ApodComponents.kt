package com.esan.diarioespacial.ui.imagenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.esan.diarioespacial.data.local.entity.ApodEntity
import com.esan.diarioespacial.ui.components.FavoriteButton

// Imagen de APOD cargada desde internet con Coil.
// SubcomposeAsyncImage (y no AsyncImage) porque permite mostrar Composables
// propios mientras carga (un indicador) y si falla (un aviso), en vez de dejar
// un hueco vacío.
@Composable
fun ApodImage(
    url: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        },
        error = {
            Box(
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                    Text("No se pudo cargar la imagen", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    )
}

// Cuando media_type es "video" la url es de YouTube/Vimeo y Coil no puede
// dibujarla; por eso no se intenta cargar y se muestra este recuadro (requisito
// del enunciado: título, fecha y el texto "Contenido en video").
@Composable
fun VideoPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.surfaceVariant)
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                "Contenido en video",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ApodItem(
    image: ApodEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        // 16:9 fijo: todas las tarjetas tienen la misma altura aunque las fotos
        // tengan proporciones distintas, y la lista no "salta" mientras cargan.
        val imageModifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
        if (image.isVideo) {
            VideoPlaceholder(imageModifier)
        } else {
            // En la lista se usa url (resolución normal) y no hdurl: las HD pesan
            // varios MB y harían lenta la lista; la HD se reserva para el detalle.
            ApodImage(url = image.url, contentDescription = image.title, modifier = imageModifier)
        }
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = image.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = image.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FavoriteButton(isFavorite = image.isFavorite, onClick = onToggleFavorite)
        }
    }
}

// key = date: cada fila se identifica por su fecha (clave primaria), así al
// marcar un favorito Compose solo redibuja esa tarjeta.
@Composable
fun ApodList(
    images: List<ApodEntity>,
    onImageClick: (ApodEntity) -> Unit,
    onToggleFavorite: (ApodEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(images, key = { it.date }) { image ->
            ApodItem(
                image = image,
                onClick = { onImageClick(image) },
                onToggleFavorite = { onToggleFavorite(image) }
            )
        }
    }
}
