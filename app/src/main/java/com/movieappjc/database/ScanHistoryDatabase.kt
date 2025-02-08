package com.yashas.chequescanner.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.movieappjc.database.ScanHistoryEntity

@Database(entities = [ScanHistoryEntity::class], version = 1)
abstract class ScanHistoryDatabase: RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
}