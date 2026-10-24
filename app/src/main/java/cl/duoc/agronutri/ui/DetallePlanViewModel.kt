package cl.duoc.agronutri.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.EstadoPrograma
import cl.duoc.agronutri.data.FotoFoliarEntity
import cl.duoc.agronutri.data.PlanEntity
import cl.duoc.agronutri.data.ProgramaEntity
import cl.duoc.agronutri.domain.ProgramaFertirriego
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetallePlanViewModel(
    private val repo: AgroRepository,
    val planId: String
) : ViewModel() {

    val plan: StateFlow<PlanEntity?> =
        repo.plan(planId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val analisis: StateFlow<List<AnalisisEntity>> =
        repo.analisis(planId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fotos: StateFlow<List<FotoFoliarEntity>> =
        repo.fotos(planId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val programas: StateFlow<List<ProgramaEntity>> =
        repo.programas(planId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarAnalisis(a: AnalisisEntity, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.guardarAnalisis(a)
            onListo()
        }
    }

    fun borrarAnalisis(id: String) {
        viewModelScope.launch {
            repo.borrarAnalisis(id)
        }
    }

    fun verificarAnalisis(id: String) {
        viewModelScope.launch {
            repo.verificarAnalisis(id)
        }
    }

    fun agregarFoto(ruta: String, nota: String?) {
        viewModelScope.launch {
            repo.guardarFoto(FotoFoliarEntity(planId = planId, ruta = ruta, nota = nota))
        }
    }

    fun borrarFoto(f: FotoFoliarEntity) {
        viewModelScope.launch {
            repo.borrarFoto(f)
        }
    }

    fun guardarPrograma(
        plan: PlanEntity,
        programa: ProgramaFertirriego,
        estado: String,
        observacion: String,
        onListo: () -> Unit
    ) {
        viewModelScope.launch {
            val registro = ProgramaEntity(
                planId = plan.id,
                cultivo = plan.cultivo,
                etapa = plan.etapaFenologica,
                frecuencia = programa.frecuencia,
                detalle = programa.detalleComoTexto(),
                alertas = programa.alertas.joinToString("\n"),
                estado = estado,
                observacionAsesor = observacion.ifBlank { null }
            )
            repo.guardarPrograma(registro)
            onListo()
        }
    }

    fun validarPrograma(p: ProgramaEntity, observacion: String) {
        viewModelScope.launch {
            val validado = p.copy(
                estado = EstadoPrograma.VALIDADO,
                observacionAsesor = observacion.ifBlank { null },
                sincronizado = false
            )
            repo.guardarPrograma(validado)
        }
    }

    fun eliminarPlan(onListo: () -> Unit) {
        viewModelScope.launch {
            repo.eliminarPlan(planId)
            onListo()
        }
    }
}
