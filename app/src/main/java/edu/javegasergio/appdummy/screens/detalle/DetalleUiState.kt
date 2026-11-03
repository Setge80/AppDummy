package edu.javegasergio.appdummy.screens.detalle

import edu.javegasergio.appdummy.data.model.Libro

sealed class DetalleUiState {
    data object Cargando : DetalleUiState()
    data class Exito(val libro: Libro) : DetalleUiState()
    data object NoEncontrado : DetalleUiState()
}