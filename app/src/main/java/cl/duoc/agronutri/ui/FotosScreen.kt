package cl.duoc.agronutri.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.agronutri.data.FotoFoliarEntity
import coil.compose.AsyncImage
import java.io.File

private val sintomas = listOf("Amarillamiento", "Borde quemado", "Manchas", "Deformación")

@Composable
fun FotosScreen(vm: DetallePlanViewModel, onAtras: () -> Unit) {
    val context = LocalContext.current
    val fotos by vm.fotos.collectAsState()

    var mostrarCamara by rememberSaveable { mutableStateOf(false) }
    var fotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var nota by rememberSaveable { mutableStateOf("") }
    var fotoGrande by remember { mutableStateOf<FotoFoliarEntity?>(null) }
    var fotoABorrar by remember { mutableStateOf<FotoFoliarEntity?>(null) }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            mostrarCamara = true
        } else {
            Toast.makeText(context, "Sin permiso de cámara no se pueden tomar fotos", Toast.LENGTH_LONG).show()
        }
    }

    if (mostrarCamara) {
        CamaraScreen(
            onFotoTomada = { ruta ->
                fotoPendiente = ruta
                nota = ""
                mostrarCamara = false
            },
            onCerrar = { mostrarCamara = false }
        )
    } else {
        Scaffold(topBar = { BarraAtras("Fotos de hojas", onAtras) }) { pad ->
            LazyColumn(
                modifier = Modifier.padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val pendiente = fotoPendiente
                if (pendiente != null) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Nueva foto", style = MaterialTheme.typography.titleMedium)
                                AsyncImage(
                                    model = File(pendiente),
                                    contentDescription = "Foto recién tomada",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(220.dp)
                                )
                                ChipsSelector("Síntoma frecuente", sintomas, nota) { nota = it }
                                OutlinedTextField(
                                    value = nota,
                                    onValueChange = { nota = it },
                                    label = { Text("Nota (opcional)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        vm.agregarFoto(pendiente, nota.ifBlank { null })
                                        fotoPendiente = null
                                        nota = ""
                                        Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
                                    }) { Text("Guardar foto") }
                                    OutlinedButton(onClick = {
                                        File(pendiente).delete()
                                        fotoPendiente = null
                                        nota = ""
                                    }) { Text("Descartar") }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permiso == PackageManager.PERMISSION_GRANTED) {
                                mostrarCamara = true
                            } else {
                                pedirPermiso.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sacar foto de una hoja") }
                }

                if (fotos.isEmpty()) {
                    item { Text("Todavía no hay fotos. Toca el botón para registrar síntomas en las hojas.") }
                }

                items(fotos, key = { it.id }) { foto ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = File(foto.ruta),
                                contentDescription = "Foto de hoja",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(88.dp).clickable { fotoGrande = foto }
                            )
                            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(foto.nota ?: "Sin nota", style = MaterialTheme.typography.titleSmall)
                                Text(foto.creadoEn.fecha(), style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { fotoABorrar = foto }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar foto")
                            }
                        }
                    }
                }
            }
        }

        val grande = fotoGrande
        if (grande != null) {
            AlertDialog(
                onDismissRequest = { fotoGrande = null },
                text = {
                    AsyncImage(
                        model = File(grande.ruta),
                        contentDescription = "Foto ampliada",
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = { TextButton(onClick = { fotoGrande = null }) { Text("Cerrar") } }
            )
        }

        val aBorrar = fotoABorrar
        if (aBorrar != null) {
            AlertDialog(
                onDismissRequest = { fotoABorrar = null },
                title = { Text("¿Eliminar foto?") },
                text = { Text("La foto se borrará de este teléfono.") },
                confirmButton = {
                    TextButton(onClick = {
                        vm.borrarFoto(aBorrar)
                        fotoABorrar = null
                    }) { Text("Eliminar") }
                },
                dismissButton = { TextButton(onClick = { fotoABorrar = null }) { Text("Cancelar") } }
            )
        }
    }
}
