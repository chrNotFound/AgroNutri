package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.EstadoPrograma
import cl.duoc.agronutri.data.PlanEntity
import cl.duoc.agronutri.data.ProgramaEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(
    vm: PlanesViewModel,
    onNuevo: () -> Unit,
    onAbrir: (String) -> Unit,
    onCatalogo: () -> Unit,
    onAjustes: () -> Unit
) {
    val planes by vm.planes.collectAsState()
    val analisis by vm.analisis.collectAsState()
    val programas by vm.programas.collectAsState()
    val enLinea by vm.enLinea.collectAsState()
    var busqueda by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis predios") },
                actions = {
                    TextButton(onClick = onCatalogo) { Text("Fertilizantes") }
                    IconButton(onClick = onAjustes) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onNuevo) {
                Text("+ Nuevo predio")
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {

            BannerConexion(enLinea)

            if (planes.isEmpty()) {
                EstadoVacio(onNuevo)
            } else {
                val filtrados = planes.filter {
                    it.predio.contains(busqueda, ignoreCase = true) ||
                        it.cuartel.contains(busqueda, ignoreCase = true) ||
                        it.cultivo.contains(busqueda, ignoreCase = true)
                }

                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            label = { Text("Buscar predio, sector o cultivo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (filtrados.isEmpty()) {
                        item { Text("No hay predios que coincidan con la búsqueda.") }
                    }
                    items(filtrados, key = { it.id }) { plan ->
                        PlanCard(plan, analisis, programas) { onAbrir(plan.id) }
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@Composable
private fun BannerConexion(enLinea: Boolean) {
    if (!enLinea) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sin conexión: todo se guarda en el teléfono.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EstadoVacio(onNuevo: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Aún no tienes predios", style = MaterialTheme.typography.titleLarge)
        Text("Crea el primero para registrar análisis y calcular tu programa de fertirriego.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNuevo) { Text("Crear mi primer predio") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlanCard(
    plan: PlanEntity,
    analisis: List<AnalisisEntity>,
    programas: List<ProgramaEntity>,
    onClick: () -> Unit
) {
    val susAnalisis = analisis.filter { it.planId == plan.id }
    val susProgramas = programas.filter { it.planId == plan.id }
    val tieneValidado = susProgramas.any { it.estado == EstadoPrograma.VALIDADO }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Column {
                Text(plan.predio, style = MaterialTheme.typography.titleMedium)
                Text("${plan.cuartel} · ${plan.cultivo} · ${plan.etapaFenologica}")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (susAnalisis.isEmpty()) {
                        EtiquetaAmbar("Sin análisis")
                    } else {
                        EtiquetaVerde("Con análisis")
                    }
                    if (tieneValidado) {
                        EtiquetaVerde("Programa validado")
                    } else if (susProgramas.isNotEmpty()) {
                        EtiquetaAmbar("Programa por validar")
                    }
                }
            }
        }
    }
}
