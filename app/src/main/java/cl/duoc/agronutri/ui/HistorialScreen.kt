package cl.duoc.agronutri.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.EstadoPrograma
import cl.duoc.agronutri.data.Perfil
import cl.duoc.agronutri.data.ProgramaEntity

@Composable
fun HistorialScreen(vm: DetallePlanViewModel, perfil: String, onAtras: () -> Unit) {
    val context = LocalContext.current
    val programas by vm.programas.collectAsState()
    var programaAValidar by remember { mutableStateOf<ProgramaEntity?>(null) }
    var observacion by remember { mutableStateOf("") }
    val esAsesor = perfil == Perfil.ASESOR

    Scaffold(topBar = { BarraAtras("Historial de programas", onAtras) }) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (programas.isEmpty()) {
                item { Text("Aún no hay programas guardados para este predio.") }
            }
            items(programas, key = { it.id }) { prog ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${prog.cultivo} · ${prog.etapa}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            if (prog.estado == EstadoPrograma.VALIDADO) {
                                EtiquetaVerde("Validado")
                            } else {
                                EtiquetaAmbar("Por validar")
                            }
                        }
                        Text(prog.creadoEn.fecha(), style = MaterialTheme.typography.bodySmall)
                        Text(prog.detalle)
                        if (prog.alertas.isNotBlank()) {
                            Text("Alertas:\n${prog.alertas}", style = MaterialTheme.typography.bodySmall)
                        }
                        if (prog.observacionAsesor != null) {
                            Text("Asesor: ${prog.observacionAsesor}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (esAsesor && prog.estado == EstadoPrograma.PENDIENTE) {
                            Button(onClick = {
                                observacion = ""
                                programaAValidar = prog
                            }) { Text("Validar este programa") }
                        }
                        OutlinedButton(onClick = {
                            val texto = "AgroNutri · ${prog.cultivo} (${prog.etapa})\n${prog.detalle}"
                            compartirTexto(context, texto)
                        }) { Text("Compartir") }
                    }
                }
            }
        }
    }

    val aValidar = programaAValidar
    if (aValidar != null) {
        AlertDialog(
            onDismissRequest = { programaAValidar = null },
            title = { Text("Validar programa") },
            text = {
                OutlinedTextField(
                    value = observacion,
                    onValueChange = { observacion = it },
                    label = { Text("Observación (opcional)") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.validarPrograma(aValidar, observacion)
                    programaAValidar = null
                    Toast.makeText(context, "Programa validado", Toast.LENGTH_SHORT).show()
                }) { Text("Validar") }
            },
            dismissButton = {
                TextButton(onClick = { programaAValidar = null }) { Text("Cancelar") }
            }
        )
    }
}
