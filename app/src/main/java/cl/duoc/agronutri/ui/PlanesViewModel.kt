package cl.duoc.agronutri.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.PlanEntity
import cl.duoc.agronutri.data.ProgramaEntity
import cl.duoc.agronutri.sync.Conectividad
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlanesViewModel(
    private val repo: AgroRepository,
    conectividad: Conectividad
) : ViewModel() {

    val planes: StateFlow<List<PlanEntity>> =
        repo.planes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analisis: StateFlow<List<AnalisisEntity>> =
        repo.todosAnalisis.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val programas: StateFlow<List<ProgramaEntity>> =
        repo.todosProgramas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enLinea: StateFlow<Boolean> = conectividad.enLinea

    fun crear(predio: String, cuartel: String, cultivo: String, etapa: String, onListo: () -> Unit) {
        viewModelScope.launch {
            val plan = PlanEntity(
                predio = predio.trim(),
                cuartel = cuartel.trim(),
                cultivo = cultivo,
                etapaFenologica = etapa
            )
            repo.guardarPlan(plan)
            onListo()
        }
    }
}
