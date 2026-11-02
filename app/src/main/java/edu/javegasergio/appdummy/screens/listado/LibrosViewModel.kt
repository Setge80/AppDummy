package edu.javegasergio.appdummy.screens.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.javegasergio.appdummy.data.repository.LibrosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibrosViewModel(
    private val repository: LibrosRepository
) : ViewModel() {

    //R4 (DPG2) -> Backing properties.
    // Estado principal de la UI
    private val _uiState = MutableStateFlow<LibrosUiState>(LibrosUiState.Cargando)
    val uiState: StateFlow<LibrosUiState> = _uiState.asStateFlow()

    // Estado del campo de búsqueda — independiente del UiState principal
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Lista de autores para los chips de filtro
    private val _autores = MutableStateFlow(listOf("Todos"))
    val autores: StateFlow<List<String>> = _autores.asStateFlow()

    private val _autorSeleccionado = MutableStateFlow("Todos")
    val autorSeleccionado: StateFlow<String> = _autorSeleccionado.asStateFlow()

    init {
        // Se ejecuta al crear el ViewModel — carga los datos automáticamente
        cargarLibros()
        cargarAutores()
    }

    fun cargarLibros() {
        // viewModelScope: corrutina ligada al ViewModel, se cancela al destruirlo
        viewModelScope.launch {
            _uiState.value = LibrosUiState.Cargando
            try {
                val libros = repository.getLibros()
                _uiState.value = LibrosUiState.Exito(libros)
            } catch (e: Exception) {
                _uiState.value = LibrosUiState.Error(
                    e.message ?: "Error desconocido al cargar los libros"
                )
            }
        }
    }

    fun cargarAutores() {
        viewModelScope.launch {
            try {
                val autores = repository.getAutores()
                _autores.value = listOf("Todos") + autores
            } catch (e: Exception) {
                _uiState.value = LibrosUiState.Error(
                    e.message ?: "Error desconocido al cargar los autores"
                )
            }
        }
    }

    fun actualizarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun toggleFavorito(id: Int) {
        // update{} es atómico y thread-safe: lee el estado actual y produce el nuevo
        // en una sola operación, sin condiciones, evitando problemas de concurrencia.
        _uiState.update { estado ->
            if (estado is LibrosUiState.Exito) {
                estado.copy(
                    libros = estado.libros.map { libro ->
                        if (libro.id == id) libro.copy(esFavorito = !libro.esFavorito)
                        else libro
                    }
                )
            } else estado
        }
    }

    fun toggleLeido(id: Int) {
        _uiState.update { estado ->
            if (estado is LibrosUiState.Exito) {
                estado.copy(
                    libros = estado.libros.map { libro ->
                        if (libro.id == id) libro.copy(leido = !libro.leido)
                        else libro
                    }
                )
            } else estado
        }
    }

    fun actualizarAutorSeleccionado(autor: String) {
        _autorSeleccionado.value = autor
    }

    //R5 (DPG2) -> Factory para crear el VM
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LibrosViewModel(LibrosRepository())
            }
        }
    }
}