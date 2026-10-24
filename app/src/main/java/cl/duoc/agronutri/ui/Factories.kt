package cl.duoc.agronutri.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.sync.Conectividad

class PlanesViewModelFactory(
    private val repo: AgroRepository,
    private val conectividad: Conectividad
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlanesViewModel(repo, conectividad) as T
    }
}

class DetallePlanViewModelFactory(
    private val repo: AgroRepository,
    private val planId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DetallePlanViewModel(repo, planId) as T
    }
}
