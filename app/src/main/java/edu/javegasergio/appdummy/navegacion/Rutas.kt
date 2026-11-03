package edu.javegasergio.appdummy.navegacion

import kotlinx.serialization.Serializable

//R11 (DPG2) -> Definir como serializable los distintos destinos de la app.

// Sin argumentos → data object
@Serializable data object Inicio
@Serializable data object Listado
@Serializable data class Detalle(val id: Int)
@Serializable data object Favoritos