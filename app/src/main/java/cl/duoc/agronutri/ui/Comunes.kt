package cl.duoc.agronutri.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.TipoMuestra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraAtras(
    titulo: String,
    onAtras: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            IconButton(onClick = onAtras) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = acciones
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipsSelector(
    titulo: String,
    opciones: List<String>,
    seleccion: String,
    etiqueta: (String) -> String = { it },
    onSeleccion: (String) -> Unit
) {
    Text(titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (opcion in opciones) {
            FilterChip(
                selected = opcion == seleccion,
                onClick = { onSeleccion(opcion) },
                label = { Text(etiqueta(opcion)) }
            )
        }
    }
}

@Composable
private fun EtiquetaBase(texto: String, fondo: Color, letra: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(8.dp)) {
        Text(
            texto,
            color = letra,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EtiquetaVerde(texto: String) = EtiquetaBase(texto, Color(0xFFC8E6C9), Color(0xFF000000))

@Composable
fun EtiquetaAmbar(texto: String) = EtiquetaBase(texto, Color(0xFFFFE0B2), Color(0xFF3E2000))

@Composable
fun EtiquetaRoja(texto: String) = EtiquetaBase(texto, Color(0xFFFFDAD6), Color(0xFF410002))

@Composable
fun EtiquetaGris(texto: String) = EtiquetaBase(texto, Color(0xFFE0E0E0), Color(0xFF000000))

@Composable
fun EtiquetaNivel(nivel: String) {
    when (nivel) {
        "BAJO" -> EtiquetaAmbar("Nivel bajo")
        "ALTO" -> EtiquetaRoja("Nivel alto")
        "ADECUADO" -> EtiquetaVerde("Nivel adecuado")
        else -> EtiquetaGris("Sin dato")
    }
}

fun Double.fmt(): String = "%.1f".format(this)

fun Long.fecha(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(this))

fun nombreTipo(tipo: String): String {
    return when (tipo) {
        TipoMuestra.SUELO -> "Suelo"
        TipoMuestra.AGUA -> "Agua"
        else -> "Foliar"
    }
}

fun compartirTexto(context: Context, texto: String) {
    val intento = Intent(Intent.ACTION_SEND)
    intento.type = "text/plain"
    intento.putExtra(Intent.EXTRA_TEXT, texto)
    context.startActivity(Intent.createChooser(intento, "Compartir programa"))
}
