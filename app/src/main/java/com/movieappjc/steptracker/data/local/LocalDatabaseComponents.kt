package com.movieappjc.steptracker.data.local

import android.content.Context
import androidx.room.*

// Room entity to store local step data
@Entity(tableName = "local_steps")
data class LocalStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val count: Long
)

// Data Access Object for local step operations
@Dao
interface LocalStepDao {
    @Insert
    suspend fun insertStepBlock(stepBlock: LocalStepEntity)

    @Query("SELECT * FROM local_steps WHERE startTime >= :start AND endTime <= :end")
    suspend fun getStepsBetween(start: Long, end: Long): List<LocalStepEntity>

    @Query("DELETE FROM local_steps WHERE endTime <= :cutoff")
    suspend fun deleteOldSteps(cutoff: Long)
}

// Room Database setup (No TypeConverters needed since we use Long for timestamps)
@Database(entities = [LocalStepEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localStepDao(): LocalStepDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "step_tracker_db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}