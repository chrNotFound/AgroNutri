package cl.duoc.agronutri.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.duoc.agronutri.AgroApp

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as AgroApp
        try {
            val enviados = app.repo.sincronizar()
            if (enviados > 0) {
                Notificaciones.avisarSincronizacion(applicationContext, enviados)
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
