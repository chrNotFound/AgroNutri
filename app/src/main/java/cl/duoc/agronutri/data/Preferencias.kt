package cl.duoc.agronutri.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Perfil {
    const val AGRICULTOR = "AGRICULTOR"
    const val ASESOR = "ASESOR"
}

class Preferencias(context: Context) {

    private val prefs = context.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    private val _tema = MutableStateFlow(prefs.getInt("tema", 0))
    val tema: StateFlow<Int> = _tema

    private val _perfil = MutableStateFlow(prefs.getString("perfil", "") ?: "")
    val perfil: StateFlow<String> = _perfil

    fun cambiarTema(valor: Int) {
        prefs.edit().putInt("tema", valor).apply()
        _tema.value = valor
    }

    fun cambiarPerfil(valor: String) {
        prefs.edit().putString("perfil", valor).apply()
        _perfil.value = valor
    }
}
