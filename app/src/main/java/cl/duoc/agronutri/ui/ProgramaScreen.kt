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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.EstadoPrograma
import cl.duoc.agronutri.data.Perfil
import cl.duoc.agronutri.data.TipoMuestra
import cl.duoc.agronutri.domain.CalculadoraFertirriego

@Composable
fun ProgramaScreen(vm: DetallePlanViewModel, perfil: String, onAtras: () -> Unit) {
    val context = LocalContext.current
    val plan by vm.plan.collectAsState()
    val analisis by vm.analisis.collectAsState()
    var observacion by rememberSaveable { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    val p = plan
    if (p == null) {
        Scaffold(topBar = { BarraAtras("Programa", onAtras) }) { pad ->
            Text("Cargando…", modifier = Modifier.padding(pad).padding(16.dp))
        }
        return
    }

    val suelo = analisis.firstOrNull { it.tipo == TipoMuestra.SUELO }
    val agua = analisis.firstOrNull { it.tipo == TipoMuestra.AGUA }
    val programa = CalculadoraFertirriego.calcular(p.cultivo, p.etapaFenologica, suelo, agua)
    val esAsesor = perfil == Perfil.ASESOR
    val textoCompartir = programa.comoTexto(p.cultivo, p.etapaFenologica, p.predio, p.cuartel)

    Scaffold(
        topBar = {
            BarraAtras("Programa de fertirriego", onAtras) {
                IconButton(onClick = { compartirTexto(context, textoCompartir) }) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir programa")
                }
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Esta semana aplica", style = MaterialTheme.typography.titleLarge)
                        for (linea in programa.lineas) {
                            Text("• ${linea.nutriente}: ${linea.kgProductoPorAplicacion.fmt()} kg/ha de ${linea.fertilizante}")
                        }
                        Text(programa.frecuencia, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                var base = if (suelo != null) {
                    "Ajustado con tu análisis de suelo del ${suelo.creadoEn.fecha()}."
                } else {
                    "Sin análisis de suelo: se usan las dosis base de la etapa."
                }
                if (agua != null) {
                    base += " Se consideró el análisis de agua del ${agua.creadoEn.fecha()} para las alertas."
                }
                Text(base, style = MaterialTheme.typography.bodyMedium)
            }

            if (programa.alertas.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Alertas", style = MaterialTheme.typography.titleMedium)
                            for (alerta in programa.alertas) {
                                Text("• $alerta")
                            }
                        }
                    }
                }
            }

            items(programa.lineas, key = { it.nutriente }) { linea ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${linea.nutriente} · ${linea.fertilizante}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            EtiquetaNivel(linea.nivel)
                        }
                        Text("Nutriente necesario: ${linea.kgNutrienteHa.fmt()} kg/ha")
                        Text("Producto en toda la etapa: ${linea.kgProductoTotal.fmt()} kg/ha")
                        Text(
                            "Por aplicación: ${linea.kgProductoPorAplicacion.fmt()} kg/ha",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(linea.observacion, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Text(
                    "Valores referenciales (versión beta). No reemplazan la recomendación de un asesor. " +
                        "No se descuentan los aportes cruzados de nitrógeno de otros fertilizantes.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (esAsesor) {
                item {
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        label = { Text("Observación del asesor (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (!guardando) {
                                guardando = true
                                vm.guardarPrograma(p, programa, EstadoPrograma.VALIDADO, observacion) {
                                    Toast.makeText(context, "Programa validado y guardado", Toast.LENGTH_SHORT).show()
                                    onAtras()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Validar y emitir recomendación") }
                }
            } else {
                item {
                    Button(
                        onClick = {
                            if (!guardando) {
                                guardando = true
                                vm.guardarPrograma(p, programa, EstadoPrograma.PENDIENTE, "") {
                                    Toast.makeText(context, "Programa guardado. Queda pendiente de validación", Toast.LENGTH_SHORT).show()
                                    onAtras()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Guardar y enviar a validación del asesor") }
                }
            }
            item {
                OutlinedButton(
                    onClick = { compartirTexto(context, textoCompartir) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Compartir por WhatsApp o correo") }
            }
        }
    }
}
