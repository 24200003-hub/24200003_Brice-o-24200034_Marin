package com.esan.diarioespacial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.esan.diarioespacial.ui.asteroides.AsteroidesNavHost
import com.esan.diarioespacial.ui.asteroides.CenteredMessage
import com.esan.diarioespacial.ui.theme.DiarioEspacialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiarioEspacialTheme {
                MainScreen()
            }
        }
    }
}

// Pestañas de la barra inferior. Cada una apunta al NavHost propio de su opción.
private enum class Tab(val route: String, val label: String, val icon: Int) {
    IMAGENES("imagenes", "Imágenes", android.R.drawable.ic_menu_gallery),
    ASTEROIDES("asteroides", "Asteroides", android.R.drawable.ic_menu_compass)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                // Patrón recomendado para barras inferiores:
                                // popUpTo(inicio) evita que se apilen pestañas al
                                // alternar (atrás saldría de la app y no recorrería
                                // todas las pestañas visitadas); saveState/restoreState
                                // hacen que cada pestaña vuelva a la pantalla donde
                                // se quedó; launchSingleTop evita duplicarla al
                                // tocar la pestaña que ya está abierta.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Solo se aplica el padding de abajo (el de la barra). El de arriba lo maneja
        // la TopAppBar de cada pantalla; aplicarlo aquí también lo duplicaría.
        NavHost(
            navController = navController,
            startDestination = Tab.IMAGENES.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Tab.IMAGENES.route) {
                // TODO(Alumno 1): reemplazar por la navegación de la opción Imágenes.
                CenteredMessage("Opción Imágenes (APOD): en desarrollo por el Alumno 1.")
            }
            composable(Tab.ASTEROIDES.route) {
                AsteroidesNavHost()
            }
        }
    }
}
