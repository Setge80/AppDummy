package edu.javegasergio.appdummy.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.javegasergio.appdummy.ui.components.ItemLibro
import edu.javegasergio.appdummy.model.librosTest

// ─── screens/PantallaListado.kt ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado() {
    // Estado local de la pantalla (en B2 pasará al ViewModel)
    var busqueda by remember { mutableStateOf("") }
    var autorSeleccionado by remember { mutableStateOf("Todos") }
    var libros by remember { mutableStateOf(librosTest) }
    //R16=> Necesario para compartir
    val context= LocalContext.current

    val autores = listOf("Todos") + libros.map { it.autor }.distinct().sorted()

    //R9=> Filtrado reactivo
    val librosFiltrados = libros.filter { libro ->
        val coincideBusqueda = busqueda.isBlank() ||
                libro.titulo.contains(busqueda, ignoreCase = true)
        val coincideGenero = autorSeleccionado == "Todos" ||
                libro.autor == autorSeleccionado
        coincideBusqueda && coincideGenero
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppDummy") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            //R8=> Barra de búsqueda
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar libros...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = { //R8=> Boton de borrado
                    AnimatedVisibility(visible = busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true
            )

            //R9=> Chips de autores
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(autores) { autor ->
                    FilterChip(
                        selected = autor == autorSeleccionado,
                        onClick = { autorSeleccionado = autor },
                        label = { Text(autor) }
                    )
                }
            }

            // Resultado del filtrado
            if (librosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { //R10=>Mensaje si no hay resultados
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            //R11=>Colores de MaterialTheme, nada fijado a mano
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sin resultados para \"$busqueda\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else { //R6=> LVG de 2 columnas
                LazyVerticalGrid(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(librosFiltrados, key = { it.id }) { libro ->
                        ItemLibro(
                            libro = libro,
                            onToggleLeido = { id -> //R7=> Botones favorito y leido que se aprovechan
                                // de generar cambios para forzar recomposicion de Compose
                                libros = libros.map {
                                    if (it.id == id) it.copy(leido = !it.leido) else it
                                }
                            },
                            onToggleFavorito = { id ->
                                libros = libros.map {
                                    if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it
                                }
                            }, //R16=>Botón Compartir titulo y autor
                            onToggleCompartir={
                                val intent= Intent(Intent.ACTION_SEND).apply{
                                    type="text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Título: ${libro.titulo}," +
                                            " autor : "+ libro.autor)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Compartir con"))
                                })
                            }
                    }
                }
            }
        }
    }

//R17=>Preview Listado
@Preview(showBackground = true)
@Composable
fun PantallaListadoPreview() {
    MaterialTheme {
        PantallaListado()
    }
}