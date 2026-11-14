package com.esan.diarioespacial.ui.imagenes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private object ImagenesRoutes {
    const val LIST = "imagenes/lista"
    const val DETAIL = "imagenes/detalle/{date}"
    const val FAVORITES = "imagenes/favoritos"
    fun detail(date: String) = "imagenes/detalle/$date"
}

// Punto de entrada de la opción Imágenes. Igual que Asteroides, tiene su propio
// NavController para que volver atrás aquí no afecte a la otra pestaña.
@Composable
fun ImagenesNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // Una sola instancia del ViewModel para las tres pantallas: la API se
    // consulta una vez y los favoritos se ven igual en todas.
    val viewModel: ApodViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = ImagenesRoutes.LIST,
        modifier = modifier
    ) {
        composable(ImagenesRoutes.LIST) {
            ApodListScreen(
                viewModel = viewModel,
                onImageClick = { navController.navigate(ImagenesRoutes.detail(it.date)) },
                onFavoritesClick = { navController.navigate(ImagenesRoutes.FAVORITES) }
            )
        }
        // Se pasa solo la fecha (clave primaria); el detalle lee el resto de Room.
        composable(
            route = ImagenesRoutes.DETAIL,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            ApodDetailScreen(
                date = backStackEntry.arguments?.getString("date").orEmpty(),
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(ImagenesRoutes.FAVORITES) {
            ApodFavoritesScreen(
                viewModel = viewModel,
                onImageClick = { navController.navigate(ImagenesRoutes.detail(it.date)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
