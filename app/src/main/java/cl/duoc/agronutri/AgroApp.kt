package cl.duoc.agronutri

import android.app.Application
import cl.duoc.agronutri.data.AgroRepository
import cl.duoc.agronutri.data.AppDatabase
import cl.duoc.agronutri.data.Preferencias
import cl.duoc.agronutri.sync.Conectividad
import cl.duoc.agronutri.sync.Notificaciones
import cl.duoc.agronutri.sync.SyncScheduler

class AgroApp : Application() {

    val repo by lazy { AgroRepository(AppDatabase.get(this).dao(), this) }
    val prefs by lazy { Preferencias(this) }
    val conectividad by lazy { Conectividad(this) }

    override fun onCreate() {
        super.onCreate()
        Notificaciones.crearCanal(this)
        SyncScheduler.programarPeriodica(this)
    }
}
