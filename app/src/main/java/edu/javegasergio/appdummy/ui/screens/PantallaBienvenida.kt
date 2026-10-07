package edu.javegasergio.appdummy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class) //El compilador exige esta línea
@Composable
fun PantallaBienvenida(onEntrar: () -> Unit) {
    //R3 -> El nombre se conserva al rotar el dispositivo
    var nombreUsuario by rememberSaveable { mutableStateOf("") }
    //R2-> Validación nombre y habilitar botón
    val botonHabilitado = nombreUsuario.trim().length >= 3
    //1a decisión: Cuando se gira el dispositivo es imposible interaccionar con el botón
    //Con un scroll ahora es posible interaccionar.
    val scrollState= rememberScrollState()

    //R4 -> Scaffold + topAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AppDummy") }
            )
        }
    ) { paddingValues ->
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)  // paddingValues debe aplicarse al contenido principal SIEMPRE
            .padding(32.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //R1-> Icono y título, subtítulo y OutlinedTextField para nombreUsuario
        Icon(
            imageVector = Icons.Default.Book,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "AppDummy",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tu catálogo de libros",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = { Text("¿Cómo te llamas?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            //R2-> Validación nombre y habilitar botón
            supportingText = {
                Text("Mínimo 3 caracteres (${nombreUsuario.length}/3)")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        //R2-> Validación nombre y habilitar botón
        Button(
            onClick = onEntrar,
            enabled = botonHabilitado,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (botonHabilitado) "Entrar como ${nombreUsuario.trim()}" else "Entrar",
                style = MaterialTheme.typography.labelLarge
            )
        }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PantallaBienvenidaPreview() {
    MaterialTheme {
        PantallaBienvenida(onEntrar = { })
    }
}