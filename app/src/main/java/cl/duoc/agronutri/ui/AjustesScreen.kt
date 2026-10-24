package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.Perfil

@Composable
fun AjustesScreen(
    tema: Int,
    perfil: String,
    onTema: (Int) -> Unit,
    onPerfil: (String) -> Unit,
    onAtras: () -> Unit
) {
    val nombresTema = listOf("Según el sistema", "Claro", "Oscuro")
    val perfiles = listOf(Perfil.AGRICULTOR, Perfil.ASESOR)

    Scaffold(topBar = { BarraAtras("Ajustes", onAtras) }) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChipsSelector(
                titulo = "Mi perfil",
                opciones = perfiles,
                seleccion = perfil,
                etiqueta = { if (it == Perfil.ASESOR) "Asesor / técnico" else "Agricultor / productor" }
            ) { onPerfil(it) }
            Text(
                "El asesor puede verificar análisis y validar programas.",
                style = MaterialTheme.typography.bodySmall
            )

            ChipsSelector(
                titulo = "Tema",
                opciones = nombresTema,
                seleccion = nombresTema[tema]
            ) { onTema(nombresTema.indexOf(it)) }

            Text(
                "AgroNutri 1.0 · Datos simulados para pruebas académicas.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}
