package cl.duoc.agronutri.data.remote

import cl.duoc.agronutri.data.AnalisisEntity
import cl.duoc.agronutri.data.PlanEntity
import cl.duoc.agronutri.data.ProgramaEntity

data class SyncRequest(
    val planes: List<PlanEntity>,
    val analisis: List<AnalisisEntity>,
    val programas: List<ProgramaEntity>
)
