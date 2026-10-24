package cl.duoc.agronutri.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.TipoMuestra

private class Entrada(val nombre: String, val texto: String, val maximo: Double)

@Composable
fun AnalisisScreen(vm: DetallePlanViewModel, onAtras: () -> Unit) {
    val context = LocalContext.current

    var tipo by rememberSaveable { mutableStateOf(TipoMuestra.SUELO) }
    var ph by rememberSaveable { mutableStateOf("") }
    var ce by rememberSaveable { mutableStateOf("") }
    var n by rememberSaveable { mutableStateOf("") }
    var p by rememberSaveable { mutableStateOf("") }
    var k by rememberSaveable { mutableStateOf("") }
    var ca by rememberSaveable { mutableStateOf("") }
    var mg by rememberSaveable { mutableStateOf("") }

    var errores by remember { mutableStateOf(mapOf<String, String>()) }
    var guardando by remember { mutableStateOf(false) }

    fun guardar() {
        val entradas = listOf(
            Entrada("pH", ph, 14.0),
            Entrada("CE", ce, 50.0),
            Entrada("N", n, 100000.0),
            Entrada("P", p, 100000.0),
            Entrada("K", k, 100000.0),
            Entrada("Ca", ca, 100000.0),
            Entrada("Mg", mg, 100000.0)
        )

        val nuevosErrores = mutableMapOf<String, String>()
        val valores = mutableMapOf<String, Double?>()
        var hayAlgunDato = false

        for (entrada in entradas) {
            if (entrada.texto.isBlank()) {
                valores[entrada.nombre] = null
            } else {
                hayAlgunDato = true
                val numero = entrada.texto.trim().replace(',', '.').toDoubleOrNull()
                if (numero == null) {
                    nuevosErrores[entrada.nombre] = "Escribe un número, por ejemplo 6.5"
                } else if (numero < 0 || numero > entrada.maximo) {
                    nuevosErrores[entrada.nombre] = "Debe estar entre 0 y ${entrada.maximo}"
                } else {
                    valores[entrada.nombre] = numero
                }
            }
        }

        errores = nuevosErrores
        if (!hayAlgunDato) {
            Toast.makeText(context, "Ingresa al menos un valor", Toast.LENGTH_SHORT).show()
            return
        }
        if (nuevosErrores.isNotEmpty()) {
            return
        }

        if (guardando) {
            return
        }
        guardando = true

        val analisis = AnalisisEntity(
            planId = vm.planId,
            tipo = tipo,
            ph = valores["pH"],
            ce = valores["CE"],
            n = valores["N"],
            p = valores["P"],
            k = valores["K"],
            ca = valores["Ca"],
            mg = valores["Mg"]
        )
        vm.guardarAnalisis(analisis) {
            Toast.makeText(context, "Análisis guardado", Toast.LENGTH_SHORT).show()
            onAtras()
        }
    }

    Scaffold(topBar = { BarraAtras("Nuevo análisis", onAtras) }) { pad ->
        Column(
            modifier = Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChipsSelector(
                titulo = "¿Qué muestra analizaste?",
                opciones = listOf(TipoMuestra.SUELO, TipoMuestra.AGUA, TipoMuestra.FOLIAR),
                seleccion = tipo,
                etiqueta = { nombreTipo(it) }
            ) { tipo = it }
            Text(
                "Suelo y agua se usan para calcular el programa. Foliar se guarda como respaldo.",
                style = MaterialTheme.typography.bodySmall
            )

            Text("Datos básicos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            CampoNumero("pH", "Referencia suelo: 5.5 a 7.5", ph, errores["pH"]) { ph = it }
            CampoNumero("Conductividad eléctrica (dS/m)", "Referencia: menos de 2.0", ce, errores["CE"]) { ce = it }

            Text("Macronutrientes (ppm)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            CampoNumero("Nitrógeno (N)", "Referencia: 20 a 50", n, errores["N"]) { n = it }
            CampoNumero("Fósforo (P)", "Referencia: 10 a 30", p, errores["P"]) { p = it }
            CampoNumero("Potasio (K)", "Referencia: 100 a 250", k, errores["K"]) { k = it }
            CampoNumero("Calcio (Ca)", "Referencia: 800 a 2000", ca, errores["Ca"]) { ca = it }
            CampoNumero("Magnesio (Mg)", "Referencia: 100 a 400", mg, errores["Mg"], ultimo = true) { mg = it }

            Text(
                "Puedes dejar vacíos los datos que no mediste. Rangos de referencia ficticios (versión beta).",
                style = MaterialTheme.typography.bodySmall
            )
            Button(onClick = { guardar() }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar análisis")
            }
        }
    }
}

@Composable
private fun CampoNumero(
    etiqueta: String,
    ayuda: String,
    valor: String,
    error: String?,
    ultimo: Boolean = false,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        supportingText = { Text(error ?: ayuda) },
        isError = error != null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (ultimo) ImeAction.Done else ImeAction.Next
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
