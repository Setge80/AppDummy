package edu.javegasergio.appdummy.model

data class LibroUI(
    val id: Int = 0,
    val titulo: String = "",
    val autor: String = "",
    val year: Int? = 1900,
    val isbn: String = "",
    val cover: String = "",
    val esFavorito: Boolean = false,
    val leido: Boolean = false
)
