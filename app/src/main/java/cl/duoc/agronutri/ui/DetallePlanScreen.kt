package cl.duoc.agronutri.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.Perfil

@Composable
fun DetallePlanScreen(
    vm: DetallePlanViewModel,
    perfil: String,
    onAtras: () -> Unit,
    onAnalisis: () -> Unit,
    onFotos: () -> Unit,
    onPrograma: () -> Unit,
    onHistorial: () -> Unit
) {
    val context = LocalContext.current
    val plan by vm.plan.collectAsState()
    val analisis by vm.analisis.collectAsState()
    val fotos by vm.fotos.collectAsState()
    val programas by vm.programas.collectAsState()
    var confirmarBorrarPlan by remember { mutableStateOf(false) }
    var analisisABorrar by remember { mutableStateOf<AnalisisEntity?>(null) }

    val p = plan
    if (p == null) {
        Scaffold(topBar = { BarraAtras("Predio", onAtras) }) { pad ->
            Text("Cargando…", modifier = Modifier.padding(pad).padding(16.dp))
        }
        return
    }

    val siguiente = if (analisis.isEmpty()) 1 else if (programas.isEmpty()) 3 else 0
    val esAsesor = perfil == Perfil.ASESOR

    Scaffold(
        topBar = {
            BarraAtras(p.predio, onAtras) {
                IconButton(onClick = { confirmarBorrarPlan = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar predio")
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
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(p.cultivo, style = MaterialTheme.typography.titleLarge)
                            Text("${p.cuartel} · ${p.etapaFenologica}")
                        }
                    }
                }
            }

            item { Text("Pasos", style = MaterialTheme.typography.titleMedium) }

            item {
                PasoCard(
                    numero = 1,
                    titulo = "Cargar análisis",
                    detalle = if (analisis.isEmpty()) "Suelo, agua o foliar" else "${analisis.size} registrados",
                    completo = analisis.isNotEmpty(),
                    destacado = siguiente == 1,
                    onClick = onAnalisis
                )
            }
            item {
                PasoCard(
                    numero = 2,
                    titulo = "Fotos de hojas (opcional)",
                    detalle = if (fotos.isEmpty()) "Registra síntomas de deficiencias" else "${fotos.size} fotos",
                    completo = fotos.isNotEmpty(),
                    destacado = false,
                    onClick = onFotos
                )
            }
            item {
                PasoCard(
                    numero = 3,
                    titulo = "Ver programa de fertirriego",
                    detalle = if (programas.isEmpty()) "Calculado con tus análisis" else "${programas.size} guardados",
                    completo = programas.isNotEmpty(),
                    destacado = siguiente == 3,
                    onClick = onPrograma
                )
            }
            item {
                OutlinedButton(onClick = onHistorial, modifier = Modifier.fillMaxWidth()) {
                    Text("Historial de programas (${programas.size})")
                }
            }

            item {
                Text("Análisis registrados", style = MaterialTheme.typography.titleMedium)
            }
            if (analisis.isEmpty()) {
                item { Text("Todavía no hay análisis. Empieza por el paso 1.") }
            }
            items(analisis, key = { it.id }) { a ->
                AnalisisCard(
                    a = a,
                    esAsesor = esAsesor,
                    onVerificar = {
                        vm.verificarAnalisis(a.id)
                        Toast.makeText(context, "Análisis verificado", Toast.LENGTH_SHORT).show()
                    },
                    onBorrar = { analisisABorrar = a }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (confirmarBorrarPlan) {
        AlertDialog(
            onDismissRequest = { confirmarBorrarPlan = false },
            title = { Text("¿Eliminar predio?") },
            text = { Text("Se borrarán también sus análisis, fotos y programas guardados en este teléfono.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrarPlan = false
                    vm.eliminarPlan { onAtras() }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrarPlan = false }) { Text("Cancelar") }
            }
        )
    }

    val aBorrar = analisisABorrar
    if (aBorrar != null) {
        AlertDialog(
            onDismissRequest = { analisisABorrar = null },
            title = { Text("¿Eliminar análisis?") },
            text = { Text("Se eliminará el análisis de ${nombreTipo(aBorrar.tipo).lowercase()} del ${aBorrar.creadoEn.fecha()}.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.borrarAnalisis(aBorrar.id)
                    analisisABorrar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { analisisABorrar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PasoCard(
    numero: Int,
    titulo: String,
    detalle: String,
    completo: Boolean,
    destacado: Boolean,
    onClick: () -> Unit
) {
    val colores = if (destacado) {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        CardDefaults.cardColors()
    }

    Card(onClick = onClick, colors = colores, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (completo) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Paso completado",
                    modifier = Modifier.width(40.dp)
                )
            } else {
                Text(
                    "$numero",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.width(40.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodyMedium)
            }
            if (destacado) {
                Text("Siguiente", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun AnalisisCard(
    a: AnalisisEntity,
    esAsesor: Boolean,
    onVerificar: () -> Unit,
    onBorrar: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${nombreTipo(a.tipo)} · ${a.creadoEn.fecha()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onBorrar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar análisis")
                }
            }

            var texto = ""
            if (a.ph != null) texto += "pH ${a.ph}   "
            if (a.ce != null) texto += "CE ${a.ce} dS/m   "
            if (a.n != null) texto += "N ${a.n}   "
            if (a.p != null) texto += "P ${a.p}   "
            if (a.k != null) texto += "K ${a.k}   "
            if (a.ca != null) texto += "Ca ${a.ca}   "
            if (a.mg != null) texto += "Mg ${a.mg}"
            Text(texto)

            Spacer(Modifier.height(8.dp))
            if (a.verificado) {
                EtiquetaVerde("Verificado por asesor")
            } else {
                EtiquetaGris("Sin verificar")
                if (esAsesor) {
                    TextButton(onClick = onVerificar) { Text("Marcar como verificado") }
                }
            }
        }
    }
}
