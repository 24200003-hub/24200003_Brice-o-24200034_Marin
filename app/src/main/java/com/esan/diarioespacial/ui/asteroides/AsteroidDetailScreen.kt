package com.esan.diarioespacial.ui.asteroides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
                        FavoriteButton(isFavorite = a.isFavorite) { viewModel.toggleFavorite(a) }
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(a.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (a.isHazardous) HazardBadge()
            HorizontalDivider()
            DetailRow("ID", a.id)
            DetailRow("Fecha de aproximación", a.approachDate)
            DetailRow("Diámetro mínimo estimado", formatNumber(a.diameterMinMeters, "m"))
            DetailRow("Diámetro máximo estimado", formatNumber(a.diameterMaxMeters, "m"))
            DetailRow("Velocidad relativa", formatNumber(a.velocityKmh, "km/h"))
            DetailRow("Distancia de paso", formatNumber(a.missDistanceKm, "km"))
            DetailRow("Potencialmente peligroso", if (a.isHazardous) "Sí" else "No")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
