package edu.javegasergio.appdummy.navegacion

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

// R15 (DPG2) -> AppDummyBottomBar con NavigationBar generada a partir de ItemsNavegación
// El item activo se obtiene con currentBackStackEntryAsState().
@Composable
fun AppDummyBottomBar(navController: NavController) {
    // currentBackStackEntryAsState: State reactivo del destino actual
    // Cuando el usuario navega, este State cambia y la barra se recompone
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = navBackStackEntry?.destination

    // Solo mostrar el BottomBar en las pantallas principales
    // (no en PantallaDetalle, por ejemplo)
    val itemsNavegacionPrincipal = itemsNavegacionPrincipal // R15 (DPG2) -> Sustituir Null por la lista de ItemsNavegacion
    val mostrarBottomBar = itemsNavegacionPrincipal.any { item ->
        destinoActual?.hierarchy?.any { it.hasRoute(item.ruta::class) } == true
    }

    if (mostrarBottomBar) {
        NavigationBar {
            itemsNavegacionPrincipal.forEach { item ->
                // hierarchy: recorre la cadena de destinos desde el actual hasta la raíz
                // Útil con grafos anidados para resaltar el ítem correcto
                val seleccionado = destinoActual?.hierarchy?.any {
                    it.hasRoute(item.ruta::class)
                } == true

                NavigationBarItem(
                    selected = seleccionado,
                    onClick = {
                        navController.navigate(item.ruta) {
                            // popUpTo: vuelve al inicio antes de navegar
                            // evita acumular destinos en la pila al cambiar de pestaña
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true   // guarda el estado de la pantalla anterior
                            }
                            // launchSingleTop: no apila múltiples copias del mismo destino
                            launchSingleTop = true
                            // restoreState: restaura el estado guardado con saveState
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (seleccionado) item.iconoSeleccionado else item.icono,
                            contentDescription = item.etiqueta
                        )
                    },
                    label = { Text(item.etiqueta) }
                )
            }
        }
    }
}