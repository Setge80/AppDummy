package edu.javegasergio.appdummy.screens.permisos

// Estado del permiso — representado como sealed class
sealed class EstadoPermiso {
    data object Concedido : EstadoPermiso()
    data object Pendiente : EstadoPermiso()
    data object Denegado : EstadoPermiso()
    data object DenegadoPermanentemente : EstadoPermiso()
}