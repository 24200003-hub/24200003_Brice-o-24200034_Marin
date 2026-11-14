package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// "#,##0.##": los # después del punto hacen que se muestren como MÁXIMO dos
// decimales (12.5 se ve "12.5" y no "12.50"), que es lo que pide el enunciado.
// Los símbolos se fijan en español para que siempre use coma de miles y punto
// decimal igual, sin depender del idioma del teléfono.
private val numberFormat = DecimalFormat(
    "#,##0.##",
    DecimalFormatSymbols(Locale.forLanguageTag("es-PE"))
)

// Si la API no envió el valor, se muestra "N/D" en vez de "null" o un crash.
fun formatNumber(value: Double?, unit: String): String =
    value?.let { "${numberFormat.format(it)} $unit" } ?: "N/D"

// Corazón reutilizado en lista, detalle y favoritos para que se vea igual en todos.
// Cambia de icono (relleno / contorno) según el estado, como pide el enunciado.
@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Quitar de favoritos" else "Marcar como favorito",
            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HazardBadge() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "⚠ Potencialmente peligroso",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AsteroidItem(
    asteroid: AsteroidEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    // Los peligrosos llevan además un borde rojo, para que se distingan de un
    // vistazo aunque la etiqueta quede fuera de la vista al hacer scroll rápido.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = if (asteroid.isHazardous) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null,
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asteroid.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Aproximación: ${asteroid.approachDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Diámetro máx.: ${formatNumber(asteroid.diameterMaxMeters, "m")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Velocidad: ${formatNumber(asteroid.velocityKmh, "km/h")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Distancia: ${formatNumber(asteroid.missDistanceKm, "km")}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (asteroid.isHazardous) {
                    Spacer(Modifier.height(6.dp))
                    HazardBadge()
                }
            }
            FavoriteButton(isFavorite = asteroid.isFavorite, onClick = onToggleFavorite)
        }
    }
}

// Lista compartida por la pantalla principal y por "Mis asteroides".
// key = id: Compose identifica cada fila por el id del asteroide, así al filtrar o
// quitar un favorito no confunde una fila con otra ni redibuja toda la lista.
@Composable
fun AsteroidList(
    asteroids: List<AsteroidEntity>,
    onAsteroidClick: (AsteroidEntity) -> Unit,
    onToggleFavorite: (AsteroidEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(asteroids, key = { it.id }) { asteroid ->
            AsteroidItem(
                asteroid = asteroid,
                onClick = { onAsteroidClick(asteroid) },
                onToggleFavorite = { onToggleFavorite(asteroid) }
            )
        }
    }
}

// Mensaje centrado que reemplaza a la lista vacía (requisito del enunciado).
@Composable
fun CenteredMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
