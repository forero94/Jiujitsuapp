package com.sukata.bjj.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sukata.bjj.R
import com.sukata.bjj.data.daos.AlumnoDao
import com.sukata.bjj.data.daos.CronogramaDao
import com.sukata.bjj.data.daos.FiltroSemanalDao
import com.sukata.bjj.data.daos.PlanDao
import com.sukata.bjj.data.daos.TecnicaDao
import com.sukata.bjj.data.entities.AlumnoEntity
import com.sukata.bjj.data.entities.CronogramaEntity
import com.sukata.bjj.data.entities.FiltroSemanalEntity
import com.sukata.bjj.data.entities.Plan
import com.sukata.bjj.data.entities.TecnicaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AlumnoEntity::class,
        TecnicaEntity::class,
        CronogramaEntity::class,
        FiltroSemanalEntity::class,
        Plan::class
    ],
    version = 15, // Increment version if schema changes
    exportSchema = false
)
@TypeConverters(
    DificultadConverter::class,
    ClasesPorSemanaConverter::class,
    DateConverter::class,
    ListStringConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alumnoDao(): AlumnoDao
    abstract fun tecnicaDao(): TecnicaDao
    abstract fun filtroSemanalDao(): FiltroSemanalDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val TAG = "AppDatabase"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "Initializing database...")

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jiujitsu_db"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration() // Recreate database if schema changes
                    .build()

                INSTANCE = instance
                Log.d(TAG, "Database instance created successfully")
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "onCreate: Database created. Preloading data...")
                preloadData(context)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG, "onOpen: Database opened. Verifying preloaded data...")
                preloadData(context)
            }

            private fun preloadData(context: Context) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val database = INSTANCE ?: return@launch

                        // Preload Alumnos
                        val alumnoDao = database.alumnoDao()
                        if (alumnoDao.count() == 0) { // Only preload if the table is empty
                            val alumnos = loadJsonData<AlumnoEntity>(context, R.raw.alumnos_iniciales)
                            alumnoDao.insertAll(*alumnos.toTypedArray())
                            Log.d(TAG, "Preloaded ${alumnos.size} alumnos.")
                        }

                        // Preload Técnicas
                        val tecnicaDao = database.tecnicaDao()
                        if (tecnicaDao.count() == 0) {
                            val tecnicas = loadJsonData<TecnicaEntity>(context, R.raw.tecnicas_iniciales)
                            tecnicaDao.insertAll(*tecnicas.toTypedArray())
                            Log.d(TAG, "Preloaded ${tecnicas.size} técnicas.")
                        }

                        // Preload Cronogramas
                        val filtroSemanalDao = database.filtroSemanalDao()
                        if (filtroSemanalDao.count() == 0) { // Only preload if the table is empty
                            val filtrosSemanales = loadJsonData<FiltroSemanalEntity>(context, R.raw.filtros_semanales_iniciales)
                            filtroSemanalDao.insertAll(*filtrosSemanales.toTypedArray())
                            Log.d(TAG, "Preloaded ${filtrosSemanales.size} filtros semanales.")
                        }

                    } catch (e: Exception) {
                        Log.e(TAG, "Error preloading data", e)
                    }
                }
            }

            private inline fun <reified T> loadJsonData(context: Context, resId: Int): List<T> {
                return try {
                    val json = context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
                    val type = object : TypeToken<List<T>>() {}.type
                    Gson().fromJson(json, type)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing JSON resource $resId", e)
                    emptyList()
                }
            }
        }
    }
}

