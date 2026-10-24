package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.Perfil

@Composable
fun BienvenidaScreen(onElegir: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("AgroNutri", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Registro y estimación nutricional en terreno, incluso sin internet.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(32.dp))
        Text("¿Cómo vas a usar la app?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        Card(onClick = { onElegir(Perfil.AGRICULTOR) }, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Soy agricultor / productor", style = MaterialTheme.typography.titleMedium)
                Text("Registro mis predios, análisis y fotos, y consulto mi programa.")
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(onClick = { onElegir(Perfil.ASESOR) }, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Soy asesor / técnico agrónomo", style = MaterialTheme.typography.titleMedium)
                Text("Verifico los análisis y valido las dosis que emite la app.")
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Puedes cambiarlo después en Ajustes.", style = MaterialTheme.typography.bodySmall)
    }
}
