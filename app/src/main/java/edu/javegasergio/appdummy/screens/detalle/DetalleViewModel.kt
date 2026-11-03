package edu.javegasergio.appdummy.screens.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import edu.javegasergio.appdummy.data.repository.LibrosRepository
import edu.javegasergio.appdummy.navegacion.Detalle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//R17(DPG2) -> VM de detalle con sus propios estados (en DetalleUiState), que ademas obtiene el id
// del libro a traves de un SavedStateHandle (Asi la pantalla no depende de la id)
class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LibrosRepository
) : ViewModel() {
    private val ruta = savedStateHandle.toRoute<Detalle>()
    private val libroId: Int = ruta.id
    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            val libro = repository.getLibroPorId(libroId)
            _uiState.value = if (libro != null) DetalleUiState.Exito(libro)
            else DetalleUiState.NoEncontrado
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // createSavedStateHandle() lee automáticamente los argumentos de la ruta (En este caso, necesitamos el ID del libro)
                // Y ahora no hay que pasarle el ID a PantallaDetalle, tod0 se resuelve aqui
                val savedStateHandle = createSavedStateHandle()

                DetalleViewModel(
                    savedStateHandle = savedStateHandle,
                    repository = LibrosRepository()
                )
            }
        }
    }
}

