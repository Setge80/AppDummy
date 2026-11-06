package edu.javegasergio.appdummy.screens.favoritos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import edu.javegasergio.appdummy.data.model.Libro
import edu.javegasergio.appdummy.data.repository.LibrosRepository
import edu.javegasergio.appdummy.navegacion.Detalle
import edu.javegasergio.appdummy.screens.detalle.DetalleUiState
import edu.javegasergio.appdummy.screens.detalle.DetalleViewModel
import edu.javegasergio.appdummy.screens.listado.LibrosEvento
import edu.javegasergio.appdummy.screens.listado.LibrosUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritosViewModel(
    private val repository: LibrosRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoritosUiState>(FavoritosUiState.Cargando)
    val uiState: StateFlow<FavoritosUiState> = _uiState.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Lista de autores para los chips de filtro
    private val _autores = MutableStateFlow(listOf("Todos"))
    val autores: StateFlow<List<String>> = _autores.asStateFlow()

    private val _autorSeleccionado = MutableStateFlow("Todos")
    val autorSeleccionado: StateFlow<String> = _autorSeleccionado.asStateFlow()

    private val _eventos = MutableSharedFlow<LibrosEvento>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventos: SharedFlow<LibrosEvento> = _eventos.asSharedFlow()


    init {
        cargarFavoritos()
        cargarAutores()
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            val librosFavs = repository.getLibrosFavoritos()
            _uiState.value = if (librosFavs.isNotEmpty()) FavoritosUiState.Exito(librosFavs)
            else FavoritosUiState.Vacio
        }
    }

    fun cargarAutores() {
        viewModelScope.launch {
            try {
                val autores = repository.getAutores()
                _autores.value = listOf("Todos") + autores
            } catch (e: Exception) {
                _uiState.value = FavoritosUiState.Vacio
            }
        }
    }

    fun actualizarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun actualizarAutorSeleccionado(autor: String) {
        _autorSeleccionado.value = autor
    }

    fun toggleFavorito(libro: Libro) {
        viewModelScope.launch {
            repository.toggleFavorito(libro)
            cargarFavoritos()
        }
        viewModelScope.launch {
            _eventos.emit(
                LibrosEvento.MostrarSnackbar(
                    if (libro.esFavorito) "\"${libro.titulo}\" eliminado de favoritos"
                    else "\"${libro.titulo}\" añadido a favoritos"
                )
            )
        }
    }

    fun toggleLeido(libro: Libro) {
        _uiState.update { estado ->
            if (estado is FavoritosUiState.Exito) {
                estado.copy(libros = estado.libros.map {
                    if (it.id == libro.id) it.copy(leido = !it.leido) else it
                })
            } else estado
        }
        viewModelScope.launch {
            _eventos.emit(
                LibrosEvento.MostrarSnackbar(
                    if (libro.leido) "\"${libro.titulo}\" marcado como no leído"
                    else "\"${libro.titulo}\" marcado como leído"
                )
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavoritosViewModel(
                    repository = LibrosRepository()
                )
            }
        }
    }
}
