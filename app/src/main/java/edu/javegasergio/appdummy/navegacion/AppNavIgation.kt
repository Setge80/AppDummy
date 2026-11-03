package edu.javegasergio.appdummy.navegacion

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edu.javegasergio.appdummy.screens.bienvenida.PantallaBienvenida
import edu.javegasergio.appdummy.screens.listado.PantallaListado
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.javegasergio.appdummy.screens.detalle.PantallaDetalle
import edu.javegasergio.appdummy.screens.favoritos.PantallaFavoritos
import edu.javegasergio.appdummy.screens.permisos.PantallaGestionPermisos

//R14 (DPG2) -> AppNavigation contiene el grafo de navegación con
// todos los posibles nodos (Inicio, Listado...) y gestiona el historial de navegación
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = {
            AppDummyBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Inicio, //R14 (DPG2) -> Inicio es el destino inicial del grafo
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<Inicio> {
                PantallaBienvenida(
                    onEntrar = {
                        navController.navigate(Listado) {
                            popUpTo<Inicio> {
                                inclusive = true    // true: también elimina Inicio
                                                    // Ahora desde listado cerraremos la app (si pulsamos atras) porque hemos borrado Inicio de la pila
                            }
                        }
                    }
                )
            }
            composable<Listado> {
                val context = LocalContext.current
                PantallaListado(    // <- Composable de la pantalla listado.
                    onNavegaADetalle = { id ->
                        navController.navigate(Detalle(id = id))    // <- Ruta de Navegación a la pantalla detalle con el id del libro.
                    },
                    onNavegaAPermisos= {
                        navController.navigate(Permisos)
                    },
                    onVolver = { (context as? Activity)?.finish() }
                )
            }
            composable<Permisos> {
                PantallaGestionPermisos(
                    onVolver = { navController.navigateUp() },
                )
            }
            composable<Favoritos> {
                PantallaFavoritos(
                    onVolver = { navController.navigateUp() },
                    onNavegaADetalle = { id ->
                        navController.navigate(Detalle(id = id))
                    }
                )
            }
            composable<Detalle> { backStackEntry ->
                val ruta: Detalle = backStackEntry.toRoute<Detalle>()
                PantallaDetalle(
                    onVolver = { navController.navigateUp() }
                )
            }
        }
    }
}