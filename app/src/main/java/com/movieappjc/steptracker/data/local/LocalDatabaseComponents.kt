package com.movieappjc.steptracker.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Room entity to store local step data.
// Every query filters on endTime, so that is the only index we need.
@Entity(
    tableName = "local_steps",
    indices = [Index(value = ["endTime"])]
)
data class LocalStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val count: Long
)

// Data Access Object for local step operations.
// All ranges are half-open on endTime: [start, end). A block therefore belongs to exactly one window,
// even if it started before the window began (e.g. across midnight or across the last sync time).
@Dao
interface LocalStepDao {
    @Insert
    suspend fun insertStepBlock(stepBlock: LocalStepEntity)

    @Query("SELECT * FROM local_steps WHERE endTime >= :start AND endTime < :end AND count <= :maxCount ORDER BY endTime")
    suspend fun getStepsBetween(start: Long, end: Long, maxCount: Long): List<LocalStepEntity>

    @Query("SELECT COALESCE(SUM(count), 0) FROM local_steps WHERE endTime >= :start AND endTime < :end AND count <= :maxCount")
    suspend fun sumStepsBetween(start: Long, end: Long, maxCount: Long): Long

    @Query("DELETE FROM local_steps WHERE endTime <= :cutoff")
    suspend fun deleteOldSteps(cutoff: Long)
}

// Room Database setup (No TypeConverters needed since we use Long for timestamps)
@Database(entities = [LocalStepEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localStepDao(): LocalStepDao

    companion object {
        private const val DB_NAME = "step_tracker_db"

        // v1 -> v2: replace the two single-column indices by a single index on endTime
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP INDEX IF EXISTS `index_local_steps_startTime`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_local_steps_endTime` ON `local_steps` (`endTime`)")
            }
        }

        // Singleton scope is handled by Hilt (see StepTrackerModule), so this is a plain factory.
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
