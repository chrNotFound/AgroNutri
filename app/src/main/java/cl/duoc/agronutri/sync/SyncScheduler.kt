package cl.duoc.agronutri.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {

    private val necesitaRed = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun sincronizarAhora(context: Context) {
        val tarea = OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .setConstraints(necesitaRed)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("sync_ahora", ExistingWorkPolicy.APPEND_OR_REPLACE, tarea)
    }

    fun programarPeriodica(context: Context) {
        val tarea = PeriodicWorkRequest.Builder(SyncWorker::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(necesitaRed)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("sync_periodica", ExistingPeriodicWorkPolicy.KEEP, tarea)
    }
}
