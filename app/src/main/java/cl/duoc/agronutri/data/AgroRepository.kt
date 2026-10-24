package cl.duoc.agronutri.data

import android.content.Context
import cl.duoc.agronutri.data.remote.ApiClient
import cl.duoc.agronutri.data.remote.SyncRequest
import cl.duoc.agronutri.sync.SyncScheduler
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AgroRepository(private val dao: AgroDao, context: Context) {

    private val appContext = context.applicationContext

    val planes = dao.observarPlanes()
    val todosAnalisis = dao.observarTodosAnalisis()
    val todosProgramas = dao.observarTodosProgramas()
    val pendientes = dao.contarPendientes()

    fun plan(id: String) = dao.observarPlan(id)
    fun analisis(planId: String) = dao.observarAnalisis(planId)
    fun programas(planId: String) = dao.observarProgramas(planId)
    fun fotos(planId: String) = dao.observarFotos(planId)

    suspend fun guardarPlan(plan: PlanEntity) {
        dao.guardarPlan(plan)
        pedirSincronizacion()
    }

    suspend fun eliminarPlan(id: String) {
        val fotos = dao.fotosDe(id)
        for (foto in fotos) {
            File(foto.ruta).delete()
        }
        dao.borrarPlan(id)
    }

    suspend fun guardarAnalisis(a: AnalisisEntity) {
        dao.insertarAnalisis(a)
        pedirSincronizacion()
    }

    suspend fun borrarAnalisis(id: String) {
        dao.borrarAnalisis(id)
    }

    suspend fun verificarAnalisis(id: String) {
        dao.marcarVerificado(id)
        pedirSincronizacion()
    }

    suspend fun guardarPrograma(p: ProgramaEntity) {
        dao.guardarPrograma(p)
        pedirSincronizacion()
    }

    suspend fun guardarFoto(f: FotoFoliarEntity) {
        dao.insertarFoto(f)
        pedirSincronizacion()
    }

    suspend fun borrarFoto(f: FotoFoliarEntity) {
        File(f.ruta).delete()
        dao.borrarFoto(f.id)
    }

    fun pedirSincronizacion() {
        SyncScheduler.sincronizarAhora(appContext)
    }

    suspend fun sincronizar(): Int {
        val planes = dao.planesPendientes()
        val analisis = dao.analisisPendientes()
        val programas = dao.programasPendientes()
        val fotos = dao.fotosPendientes()

        val total = planes.size + analisis.size + programas.size + fotos.size
        if (total == 0) {
            return 0
        }

        val hayDatos = planes.isNotEmpty() || analisis.isNotEmpty() || programas.isNotEmpty()
        if (hayDatos) {
            val respuesta = ApiClient.api.sincronizar(SyncRequest(planes, analisis, programas))
            if (!respuesta.isSuccessful) {
                throw Exception("El servidor respondió ${respuesta.code()}")
            }
            if (planes.isNotEmpty()) {
                dao.marcarPlanesSincronizados(planes.map { it.id })
            }
            if (analisis.isNotEmpty()) {
                dao.marcarAnalisisSincronizados(analisis.map { it.id })
            }
            if (programas.isNotEmpty()) {
                dao.marcarProgramasSincronizados(programas.map { it.id })
            }
        }

        for (foto in fotos) {
            val archivo = File(foto.ruta)
            if (archivo.exists()) {
                val cuerpo = archivo.asRequestBody("image/jpeg".toMediaType())
                val parte = MultipartBody.Part.createFormData("archivo", archivo.name, cuerpo)
                val texto = "text/plain".toMediaType()
                val respuesta = ApiClient.api.subirFoto(
                    parte,
                    foto.id.toRequestBody(texto),
                    foto.planId.toRequestBody(texto),
                    (foto.nota ?: "").toRequestBody(texto)
                )
                if (!respuesta.isSuccessful) {
                    throw Exception("No se pudo subir la foto (${respuesta.code()})")
                }
            }
            dao.marcarFotoSincronizada(foto.id)
        }

        return total
    }
}
