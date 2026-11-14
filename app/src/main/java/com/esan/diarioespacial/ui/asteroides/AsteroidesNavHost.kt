package com.esan.diarioespacial.ui.asteroides

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Rutas de la navegación interna de la opción Asteroides.
private object AsteroidRoutes {
    const val LIST = "asteroides/lista"
    const val DETAIL = "asteroides/detalle/{id}"
    const val FAVORITES = "asteroides/favoritos"
    fun detail(id: String) = "asteroides/detalle/$id"
}

// Punto de entrada de la opción Asteroides. La barra inferior de MainActivity
// solo llama a este Composable; todo lo de adentro es de esta opción.
//
// Tiene su propio NavController: así volver atrás dentro de Asteroides
// (detalle -> lista) no afecta a la pestaña Imágenes, y cada opción recuerda
// en qué pantalla se quedó.
@Composable
fun AsteroidesNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // El ViewModel se crea aquí, fuera del NavHost, y se pasa a las tres pantallas:
    // comparten la misma instancia, así el filtro se mantiene al volver del
    // detalle y la API no se consulta de nuevo en cada pantalla.
    val viewModel: AsteroidViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AsteroidRoutes.LIST,
        modifier = modifier
    ) {
        composable(AsteroidRoutes.LIST) {
            AsteroidListScreen(
                viewModel = viewModel,
                onAsteroidClick = { navController.navigate(AsteroidRoutes.detail(it.id)) },
                onFavoritesClick = { navController.navigate(AsteroidRoutes.FAVORITES) }
            )
        }
        // Se pasa solo el id por la ruta (no el objeto entero): las rutas son
        // texto, y con el id la pantalla de detalle lee el resto desde Room.
        composable(
            route = AsteroidRoutes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            AsteroidDetailScreen(
                asteroidId = backStackEntry.arguments?.getString("id").orEmpty(),
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(AsteroidRoutes.FAVORITES) {
            AsteroidFavoritesScreen(
                viewModel = viewModel,
                onAsteroidClick = { navController.navigate(AsteroidRoutes.detail(it.id)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
