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
    private val _eventos = MutableSharedFlow<LibrosEvento>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val eventos: SharedFlow<LibrosEvento> = _eventos.asSharedFlow()


    init {
        cargarFavoritos()
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            val librosFavs = repository.getLibrosFavoritos()
            _uiState.value = if (librosFavs.isNotEmpty()) FavoritosUiState.Exito(librosFavs)
            else FavoritosUiState.Vacio
        }
    }

    fun toggleFavorito(libro: Libro) {
        viewModelScope.launch {
            repository.toggleFavorito(libro)
            cargarFavoritos()
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
