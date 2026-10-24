package cl.duoc.agronutri.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlanEntity::class,
        AnalisisEntity::class,
        FotoFoliarEntity::class,
        ProgramaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AgroDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            val actual = instancia
            if (actual != null) {
                return actual
            }
            return synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agronutri.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                instancia = nueva
                nueva
            }
        }
    }
}
