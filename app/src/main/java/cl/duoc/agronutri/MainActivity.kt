package cl.duoc.agronutri

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import cl.duoc.agronutri.ui.AgroNavHost
import cl.duoc.agronutri.ui.theme.AgroNutriTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as AgroApp

        setContent {
            val modo by app.prefs.tema.collectAsState()
            val oscuro = when (modo) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            val pedirNotificaciones = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            LaunchedEffect(Unit) {
                val permiso = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.POST_NOTIFICATIONS
                )
                if (permiso != PackageManager.PERMISSION_GRANTED) {
                    pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            AgroNutriTheme(darkTheme = oscuro) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AgroNavHost()
                }
            }
        }
    }
}
