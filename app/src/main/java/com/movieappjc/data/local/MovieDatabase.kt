package com.movieappjc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieTable::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
}