package edu.javegasergio.appdummy.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

//R5=> Modelo LibroUI
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

val librosTest =
        listOf(
            LibroUI(
                id = 1,
                titulo = "Proyecto Hail Mary",
                autor = "Andy Weir",
                year = 2021,
                isbn = "9788418037016",
                cover = "https://covers.openlibrary.org/b/isbn/9788418037016-L.jpg",
                esFavorito = true,
                leido = false
            ),
            LibroUI(
                id = 2,
                titulo = "Juego de tronos",
                autor = "George R.R. Martin",
                year = 1996,
                isbn = "9780307951182",
                cover = "https://covers.openlibrary.org/b/isbn/9780307951182-L.jpg",
                esFavorito = true,
                leido = true
            ),
            LibroUI(
                id = 3,
                titulo = "Festín de cuervos",
                autor = "George R.R. Martin",
                year = 2005,
                isbn = "9780307951212",
                esFavorito = false,
                leido = false
            ),
            LibroUI(
                id = 4,
                titulo = "Cementerio de Animales",
                autor = "Stephen King",
                year = 1983,
                isbn = "9788401499845",
                esFavorito = false,
                leido = true
            ),
            LibroUI(
                id = 5,
                titulo = "El juego de Ender",
                autor = "Orson Scott Card",
                year = 1985,
                isbn = "9788498720068",
                esFavorito = false,
                leido = true
            )
        )