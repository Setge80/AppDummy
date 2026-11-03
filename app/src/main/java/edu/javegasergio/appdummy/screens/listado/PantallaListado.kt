package edu.javegasergio.appdummy.screens.listado

import android.R.attr.navigationIcon
import android.content.Intent
import android.util.Patterns
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import edu.javegasergio.appdummy.R
import edu.javegasergio.appdummy.data.model.Libro
import edu.javegasergio.appdummy.ui.components.ItemLibro
import edu.javegasergio.appdummy.data.model.librosTest
import edu.javegasergio.appdummy.screens.permisos.PantallaGestionPermisos
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// ─── screens/PantallaListado.kt ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListado(
    modifier: Modifier = Modifier,
    viewModel: LibrosViewModel = viewModel(factory = LibrosViewModel.Factory),
    onNavegaADetalle: (Int) -> Unit = {},  // callback de navegación (se conecta al NavHost en T3)
    onNavegaAPermisos: () -> Unit = {},
    onVolver: () -> Unit
) {
    // collectAsStateWithLifecycle: activo en foreground, pausado en background
    //R6 y R7 (DPG2) -> Recoge el estado y sobrevive a la rotación (por el VM** tambien).
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val autorSeleccionado by viewModel.autorSeleccionado.collectAsStateWithLifecycle()
    val autores by viewModel.autores.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var snackbarJob by remember { mutableStateOf<Job?>(null) }

    // LaunchedEffect con Unit: se lanza una única vez al montar el Composable
    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LibrosEvento.MostrarSnackbar -> {
                    // Cancelar snackbar previo si existe
                    snackbarJob?.cancel()
                    // Descarta el Snackbar visible en pantalla
                    snackbarHostState.currentSnackbarData?.dismiss()
                    // Lanzar un nuevo Snackbar
                    snackbarJob = scope.launch {
                        snackbarHostState.showSnackbar(evento.mensaje)
                    }
                }
                is LibrosEvento.NavegarADetalle -> { /* navController.navigate(ruta tipada — ver T3 )*/ }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("AppDummy") },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                    IconButton(onClick = onNavegaAPermisos) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Permisos Cámara")
                    }
                },
                navigationIcon = { // 👈 Añadimos el icono para volver
                        IconButton(onClick = onVolver) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Barra de búsqueda
            OutlinedTextField(
                value = busqueda,
                onValueChange = viewModel::actualizarBusqueda,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar libros...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(visible = busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.actualizarBusqueda("") }) {
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
                        onClick = { viewModel.actualizarAutorSeleccionado(autor) },
                        label = { Text(autor) }
                    )
                }
            }

            // R6(DPG2) -> when exhaustivo sobre la sealed class
            when (val estado = uiState) {
                is LibrosUiState.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is LibrosUiState.Exito -> {
                    val librosFiltrados = estado.libros.filter {
                        val coincideBusqueda = busqueda.isBlank() || it.titulo.contains(busqueda, ignoreCase = true)
                        val coincideAutor = autorSeleccionado == "Todos" || it.autor == autorSeleccionado
                        coincideBusqueda && coincideAutor
                    }
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
                                    onClickItem = { onNavegaADetalle(libro.id) },
                                    onToggleLeido = { viewModel.toggleLeido(libro) },
                                    onToggleFavorito = { viewModel.toggleFavorito(libro) }
                                )
                            }
                        }
                    }
                }

                is LibrosUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = estado.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = viewModel::cargarLibros) { Text("Reintentar") }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemLibro(
    libro: Libro,
    onClickItem: () -> Unit = {},
    //R7 --> Los callbacks conectan la pantalla al VM
    onToggleLeido: (Int) -> Unit = {},
    onToggleFavorito: (Int) -> Unit = {}
) {
    Card(onClick = onClickItem, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text(libro.titulo, style = MaterialTheme.typography.titleSmall)
            if (Patterns.WEB_URL.matcher(libro.cover).matches())
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(libro.cover)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Portada de ${libro.titulo}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            else // Si no es una URL válida, se muestra una imagen por defecto
                AsyncImage(
                    model = R.drawable.nocover,
                    contentDescription = "Portada de ${libro.titulo}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(200.dp)
                )

            Text(
                text = "${libro.autor} • ${libro.year}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    modifier = Modifier.fillMaxSize().weight(0.5f),
                    onClick = { onToggleLeido(libro.id) }) {
                    Icon(
                        imageVector = if (libro.leido) Icons.Default.BookmarkAdded
                        else Icons.Default.BookmarkBorder,
                        contentDescription = if (libro.leido) "Quitar leído" else "Marcar como leído",
                        tint = if (libro.leido) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    modifier = Modifier.fillMaxSize().weight(0.5f),
                    onClick = { onToggleFavorito(libro.id) }) {
                    Icon(
                        imageVector = if (libro.esFavorito) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (libro.esFavorito) "Quitar favorito" else "Añadir favorito",
                        tint = if (libro.esFavorito) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaListadoPreview() {
    PantallaListado(onVolver = {})
}