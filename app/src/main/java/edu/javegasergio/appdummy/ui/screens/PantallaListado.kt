package edu.javegasergio.appdummy.ui.screens

import android.util.Patterns
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import edu.javegasergio.appdummy.R
import edu.javegasergio.appdummy.model.LibroUI
import edu.javegasergio.appdummy.ui.components.ItemLibro

// ─── screens/PantallaListado.kt ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado() {
    // Estado local de la pantalla (en B2 pasará al ViewModel)
    var busqueda by remember { mutableStateOf("") }
    var autorSeleccionado by remember { mutableStateOf("Todos") }
    var libros by remember {
        mutableStateOf(
            listOf(
                LibroUI(
                    id = 1,
                    titulo = "Proyecto Hail Mary",
                    autor = "Andy Weir",
                    year = 2021,
                    isbn = "9788418037016",
                    cover = "https://covers.openlibrary.org/b/isbn/9788418037016-L.jpg",
                    esFavorito = true,
                    leido = false
                ),
                LibroUI(
                    id = 2,
                    titulo = "Juego de tronos",
                    autor = "George R.R. Martin",
                    year = 1996,
                    isbn = "9780307951182",
                    cover = "https://covers.openlibrary.org/b/isbn/9780307951182-L.jpg",
                    esFavorito = true,
                    leido = true
                ),
                LibroUI(
                    id = 3,
                    titulo = "Festín de cuervos",
                    autor = "George R.R. Martin",
                    year = 2005,
                    isbn = "9780307951212",
                    esFavorito = false,
                    leido = false
                ),
                LibroUI(
                    id = 4,
                    titulo = "Cementerio de Animales",
                    autor = "Stephen King",
                    year = 1983,
                    isbn = "9788401499845",
                    esFavorito = false,
                    leido = true
                ),
                LibroUI(
                    id = 5,
                    titulo = "El juego de Ender",
                    autor = "Orson Scott Card",
                    year = 1985,
                    isbn = "9788498720068",
                    esFavorito = false,
                    leido = true
                )
            )
        )
    }

    val autores = listOf("Todos") + libros.map { it.autor }.distinct().sorted()

    // Filtrado reactivo
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
            // Barra de búsqueda
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar libros...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(visible = busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true
            )

            // Chips de autores
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
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sin resultados para \"$busqueda\"",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(librosFiltrados, key = { it.id }) { libro ->
                        ItemLibro(
                            libro = libro,
                            onToggleLeido = { id ->
                                libros = libros.map {
                                    if (it.id == id) it.copy(leido = !it.leido) else it
                                }
                            },
                            onToggleFavorito = { id ->
                                libros = libros.map {
                                    if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaListadoPreview() {
    MaterialTheme {
        PantallaListado()
    }
}