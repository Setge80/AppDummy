package edu.javegasergio.appdummy.screens.favoritos

import edu.javegasergio.appdummy.data.model.Libro

sealed class FavoritosUiState {
    data object Cargando : FavoritosUiState()
    data class Exito(val libros: List<Libro>) : FavoritosUiState()
    data object Vacio : FavoritosUiState()
}