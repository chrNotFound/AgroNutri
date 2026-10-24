package cl.duoc.agronutri.sync

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Notificaciones {

    private const val ID_CANAL = "sincronizacion"

    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            ID_CANAL,
            "Sincronización",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    fun avisarSincronizacion(context: Context, cantidad: Int) {
        val permiso = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        if (permiso != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val notificacion = NotificationCompat.Builder(context, ID_CANAL)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("AgroNutri")
            .setContentText("Se sincronizaron $cantidad registros con el servidor")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1, notificacion)
    }
}
