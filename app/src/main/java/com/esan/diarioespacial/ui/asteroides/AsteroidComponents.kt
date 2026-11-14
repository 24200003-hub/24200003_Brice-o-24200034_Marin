package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.data.local.entity.AsteroidEntity
import com.esan.diarioespacial.ui.components.FavoriteButton
import com.esan.diarioespacial.ui.components.InfoRow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// "#,##0.##": los # después del punto hacen que se muestren como MÁXIMO dos
// decimales (12.5 se ve "12.5" y no "12.50"), que es lo que pide el enunciado.
// Se fijan los símbolos en Locale.US para que el formato no dependa del idioma
// del teléfono (coma para miles, punto para decimales).
private val numberFormat = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))

// Si la API no envió el valor se muestra "N/D" en vez de "null" o un crash.
fun formatNumber(value: Double?, unit: String): String =
    value?.let { "${numberFormat.format(it)} $unit" } ?: "N/D"

@Composable
fun HazardBadge(isHazardous: Boolean) {
    Surface(
        color = if (isHazardous) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (isHazardous) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = if (isHazardous) "⚠ Potencialmente peligroso" else "✓ Sin riesgo",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = if (asteroid.isHazardous) BorderStroke(1.5.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "☄ ${asteroid.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Aproximación: ${asteroid.approachDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FavoriteButton(isFavorite = asteroid.isFavorite, onClick = onToggleFavorite)
            }
            Column(
                modifier = Modifier.padding(end = 12.dp, top = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (asteroid.isHazardous) HazardBadge(isHazardous = true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                InfoRow("Diámetro máximo", formatNumber(asteroid.diameterMaxMeters, "m"))
                InfoRow("Velocidad", formatNumber(asteroid.velocityKmh, "km/h"))
                InfoRow("Distancia de paso", formatNumber(asteroid.missDistanceKm, "km"))
            }
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
