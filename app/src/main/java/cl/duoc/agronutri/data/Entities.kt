package cl.duoc.agronutri.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plan")
data class PlanEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val predio: String,
    val cuartel: String,
    val cultivo: String,
    val etapaFenologica: String,
    val creadoEn: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)

object TipoMuestra {
    const val SUELO = "SUELO"
    const val AGUA = "AGUA"
    const val FOLIAR = "FOLIAR"
}

@Entity(
    tableName = "analisis",
    foreignKeys = [ForeignKey(
        entity = PlanEntity::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("planId")]
)
data class AnalisisEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val tipo: String,
    val ph: Double?,
    val ce: Double?,
    val n: Double?,
    val p: Double?,
    val k: Double?,
    val ca: Double?,
    val mg: Double?,
    val verificado: Boolean = false,
    val creadoEn: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)

@Entity(
    tableName = "foto_foliar",
    foreignKeys = [ForeignKey(
        entity = PlanEntity::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("planId")]
)
data class FotoFoliarEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val ruta: String,
    val nota: String? = null,
    val creadoEn: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)

object EstadoPrograma {
    const val PENDIENTE = "PENDIENTE_VALIDACION"
    const val VALIDADO = "VALIDADO"
}

@Entity(
    tableName = "programa",
    foreignKeys = [ForeignKey(
        entity = PlanEntity::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("planId")]
)
data class ProgramaEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val cultivo: String,
    val etapa: String,
    val frecuencia: String,
    val detalle: String,
    val alertas: String,
    val estado: String,
    val observacionAsesor: String? = null,
    val creadoEn: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)
