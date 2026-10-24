package cl.duoc.agronutri.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.domain.Catalogo

@Composable
fun CatalogoScreen(onAtras: () -> Unit) {
    Scaffold(topBar = { BarraAtras("Catálogo", onAtras) }) { pad ->
        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Fertilizantes comerciales", style = MaterialTheme.typography.titleLarge) }
            items(Catalogo.fertilizantes, key = { it.nombre }) { f ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(f.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Aporta: ${f.nutriente}")
                        Text("Composición: ${f.composicion}")
                    }
                }
            }

            item { Text("Requerimiento base por cultivo (kg/ha)", style = MaterialTheme.typography.titleLarge) }
            items(Catalogo.cultivos, key = { it }) { cultivo ->
                val r = Catalogo.requerimientoBase(cultivo)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(cultivo, style = MaterialTheme.typography.titleMedium)
                        Text("N ${r.n.fmt()} · P ${r.p.fmt()} · K ${r.k.fmt()} · Ca ${r.ca.fmt()} · Mg ${r.mg.fmt()}")
                    }
                }
            }

            item {
                Text(
                    "Datos ficticios y referenciales para pruebas académicas.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
