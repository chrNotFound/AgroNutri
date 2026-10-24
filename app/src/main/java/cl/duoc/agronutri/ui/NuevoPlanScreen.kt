package cl.duoc.agronutri.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.domain.Catalogo

@Composable
fun NuevoPlanScreen(vm: PlanesViewModel, onAtras: () -> Unit) {
    val context = LocalContext.current
    var predio by rememberSaveable { mutableStateOf("") }
    var cuartel by rememberSaveable { mutableStateOf("") }
    var cultivo by rememberSaveable { mutableStateOf(Catalogo.cultivos[0]) }
    var etapa by rememberSaveable { mutableStateOf(Catalogo.etapas[0]) }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraAtras("Nuevo predio", onAtras) }) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = predio,
                onValueChange = { predio = it },
                label = { Text("Nombre del predio") },
                supportingText = {
                    if (intentoGuardar && predio.isBlank()) Text("Escribe el nombre del predio")
                    else Text("Ejemplo: Fundo Los Olivos")
                },
                isError = intentoGuardar && predio.isBlank(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cuartel,
                onValueChange = { cuartel = it },
                label = { Text("Sector de riego o cuartel") },
                supportingText = {
                    if (intentoGuardar && cuartel.isBlank()) Text("Escribe el sector de riego")
                    else Text("Ejemplo: Cuartel 3")
                },
                isError = intentoGuardar && cuartel.isBlank(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ChipsSelector("Cultivo", Catalogo.cultivos, cultivo) { cultivo = it }

            ChipsSelector("Etapa fenológica", Catalogo.etapas, etapa) { etapa = it }

            Button(
                onClick = {
                    intentoGuardar = true
                    if (predio.isNotBlank() && cuartel.isNotBlank() && !guardando) {
                        guardando = true
                        vm.crear(predio, cuartel, cultivo, etapa) {
                            Toast.makeText(context, "Predio guardado", Toast.LENGTH_SHORT).show()
                            onAtras()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar predio") }
        }
    }
}
