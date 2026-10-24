package cl.duoc.agronutri.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Conectividad(context: Context) {

    private val _enLinea = MutableStateFlow(false)
    val enLinea: StateFlow<Boolean> = _enLinea

    init {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        _enLinea.value = manager.activeNetwork != null

        manager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _enLinea.value = true
            }

            override fun onLost(network: Network) {
                _enLinea.value = false
            }
        })
    }
}
