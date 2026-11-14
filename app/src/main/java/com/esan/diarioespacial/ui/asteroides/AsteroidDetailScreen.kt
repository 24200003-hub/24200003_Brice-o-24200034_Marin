package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.esan.diarioespacial.ui.components.CenteredMessage
import com.esan.diarioespacial.ui.components.FavoriteButton
import com.esan.diarioespacial.ui.components.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsteroidDetailScreen(
    asteroidId: String,
    viewModel: AsteroidViewModel,
    onBack: () -> Unit
) {
    // remember(asteroidId): sin esto se crearía un Flow nuevo en cada
    // recomposición y la pantalla se volvería a suscribir a Room una y otra vez.
    // Se lee de Room (no se pasa el objeto desde la lista) para que el corazón
    // se actualice al instante cuando se marca o desmarca aquí.
    val asteroidFlow = remember(asteroidId) { viewModel.getAsteroid(asteroidId) }
    val asteroid by asteroidFlow.collectAsState(initial = null)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Detalle del asteroide") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    asteroid?.let { a ->
                        FavoriteButton(isFavorite = a.isFavorite, onClick = { viewModel.toggleFavorite(a) })
                    }
                }
            )
        }
    ) { padding ->
        val a = asteroid
        if (a == null) {
            CenteredMessage("No se encontró el asteroide.", Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (a.isHazardous) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("☄", style = MaterialTheme.typography.displaySmall)
                    Text(a.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("ID NASA: ${a.id}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (a.isHazardous) "⚠ Potencialmente peligroso" else "✓ No es potencialmente peligroso",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            DetailSection("Aproximación") {
                InfoRow("Fecha de aproximación", a.approachDate)
                InfoRow("Velocidad relativa", formatNumber(a.velocityKmh, "km/h"))
                InfoRow("Distancia de paso", formatNumber(a.missDistanceKm, "km"))
            }

            DetailSection("Tamaño estimado") {
                InfoRow("Diámetro mínimo", formatNumber(a.diameterMinMeters, "m"))
                InfoRow("Diámetro máximo", formatNumber(a.diameterMaxMeters, "m"))
            }

            DetailSection("Clasificación") {
                InfoRow("Potencialmente peligroso", if (a.isHazardous) "Sí" else "No")
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}
