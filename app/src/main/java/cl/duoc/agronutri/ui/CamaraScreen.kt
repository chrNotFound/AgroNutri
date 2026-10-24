package cl.duoc.agronutri.ui

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

@Composable
fun CamaraScreen(onFotoTomada: (String) -> Unit, onCerrar: () -> Unit) {
    val context = LocalContext.current
    val actividad = context as ComponentActivity
    val imageCapture = remember { ImageCapture.Builder().build() }
    val futuroProveedor = remember { ProcessCameraProvider.getInstance(context) }

    BackHandler(onBack = onCerrar)

    DisposableEffect(Unit) {
        onDispose {
            try {
                futuroProveedor.get().unbindAll()
            } catch (e: Exception) {
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val vista = PreviewView(ctx)
                futuroProveedor.addListener({
                    val proveedor = futuroProveedor.get()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(vista.surfaceProvider)
                    proveedor.unbindAll()
                    proveedor.bindToLifecycle(
                        actividad,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                }, ContextCompat.getMainExecutor(ctx))
                vista
            }
        )

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(24.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onCerrar) { Text("Cancelar") }
            Button(onClick = {
                val carpeta = File(context.filesDir, "fotos")
                carpeta.mkdirs()
                val archivo = File(carpeta, "hoja_${System.currentTimeMillis()}.jpg")
                val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()

                imageCapture.takePicture(
                    opciones,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                            onFotoTomada(archivo.absolutePath)
                        }

                        override fun onError(error: ImageCaptureException) {
                            Toast.makeText(context, "No se pudo sacar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }) { Text("Sacar foto") }
        }
    }
}
