package cl.duoc.agronutri.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AgroDao {

    @Query("SELECT * FROM plan ORDER BY creadoEn DESC")
    fun observarPlanes(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM plan WHERE id = :id")
    fun observarPlan(id: String): Flow<PlanEntity?>

    @Upsert
    suspend fun guardarPlan(plan: PlanEntity)

    @Query("DELETE FROM plan WHERE id = :id")
    suspend fun borrarPlan(id: String)

    @Query("SELECT * FROM analisis WHERE planId = :planId ORDER BY creadoEn DESC")
    fun observarAnalisis(planId: String): Flow<List<AnalisisEntity>>

    @Query("SELECT * FROM analisis")
    fun observarTodosAnalisis(): Flow<List<AnalisisEntity>>

    @Insert
    suspend fun insertarAnalisis(a: AnalisisEntity)

    @Query("DELETE FROM analisis WHERE id = :id")
    suspend fun borrarAnalisis(id: String)

    @Query("UPDATE analisis SET verificado = 1, sincronizado = 0 WHERE id = :id")
    suspend fun marcarVerificado(id: String)

    @Query("SELECT * FROM programa WHERE planId = :planId ORDER BY creadoEn DESC")
    fun observarProgramas(planId: String): Flow<List<ProgramaEntity>>

    @Query("SELECT * FROM programa")
    fun observarTodosProgramas(): Flow<List<ProgramaEntity>>

    @Upsert
    suspend fun guardarPrograma(p: ProgramaEntity)

    @Query("SELECT * FROM foto_foliar WHERE planId = :planId ORDER BY creadoEn DESC")
    fun observarFotos(planId: String): Flow<List<FotoFoliarEntity>>

    @Query("SELECT * FROM foto_foliar WHERE planId = :planId")
    suspend fun fotosDe(planId: String): List<FotoFoliarEntity>

    @Insert
    suspend fun insertarFoto(f: FotoFoliarEntity)

    @Query("DELETE FROM foto_foliar WHERE id = :id")
    suspend fun borrarFoto(id: String)

    @Query(
        "SELECT " +
            "(SELECT COUNT(*) FROM plan WHERE sincronizado = 0) + " +
            "(SELECT COUNT(*) FROM analisis WHERE sincronizado = 0) + " +
            "(SELECT COUNT(*) FROM programa WHERE sincronizado = 0) + " +
            "(SELECT COUNT(*) FROM foto_foliar WHERE sincronizado = 0)"
    )
    fun contarPendientes(): Flow<Int>

    @Query("SELECT * FROM plan WHERE sincronizado = 0")
    suspend fun planesPendientes(): List<PlanEntity>

    @Query("SELECT * FROM analisis WHERE sincronizado = 0")
    suspend fun analisisPendientes(): List<AnalisisEntity>

    @Query("SELECT * FROM programa WHERE sincronizado = 0")
    suspend fun programasPendientes(): List<ProgramaEntity>

    @Query("SELECT * FROM foto_foliar WHERE sincronizado = 0")
    suspend fun fotosPendientes(): List<FotoFoliarEntity>

    @Query("UPDATE plan SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun marcarPlanesSincronizados(ids: List<String>)

    @Query("UPDATE analisis SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun marcarAnalisisSincronizados(ids: List<String>)

    @Query("UPDATE programa SET sincronizado = 1 WHERE id IN (:ids)")
    suspend fun marcarProgramasSincronizados(ids: List<String>)

    @Query("UPDATE foto_foliar SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarFotoSincronizada(id: String)
}
