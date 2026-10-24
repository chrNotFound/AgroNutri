package cl.duoc.agronutri.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdeOscuro, onPrimary = Blanco,
    primaryContainer = Color(0xFFC8E6C9), onPrimaryContainer = Negro,
    secondary = Color(0xFF004D40), onSecondary = Blanco,
    secondaryContainer = Color(0xFFB2DFDB), onSecondaryContainer = Negro,
    background = Blanco, onBackground = Negro,
    surface = Blanco, onSurface = Negro,
    surfaceVariant = Color(0xFFE6EDE6), onSurfaceVariant = Color(0xFF1A1A1A),
    surfaceContainerHigh = Color(0xFFEDF2ED), surfaceContainerHighest = Color(0xFFE6EDE6),
    outline = Color(0xFF424242),
    error = Color(0xFFB00020), onError = Blanco,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002)
)

private val EsquemaOscuro = darkColorScheme(
    primary = VerdeClaro, onPrimary = Negro,
    primaryContainer = Color(0xFF1F4D22), onPrimaryContainer = Blanco,
    secondary = Color(0xFF80CBC4), onSecondary = Negro,
    secondaryContainer = Color(0xFF004D40), onSecondaryContainer = Blanco,
    background = Negro, onBackground = Blanco,
    surface = Negro, onSurface = Blanco,
    surfaceVariant = Color(0xFF1F2A1F), onSurfaceVariant = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFF182218), surfaceContainerHighest = Color(0xFF1F2A1F),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFFF8A80), onError = Negro,
    errorContainer = Color(0xFF7A0010), onErrorContainer = Blanco
)

@Composable
fun AgroNutriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}
