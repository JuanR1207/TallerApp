package com.example.tallerapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores del taller: azul oscuro y naranja.
private val Azul = Color(0xFF1F3A5F)
private val AzulClaro = Color(0xFFA9C7F0)
private val Naranja = Color(0xFFF2A93B)

private val LightColors = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    secondary = Naranja,
    onSecondary = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Azul,
    secondary = Naranja,
    onSecondary = Color.Black
)

@Composable
fun TallerAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}
