package com.movieappjc.data.local

import androidx.room.Dao
import androidx.room.Query
import com.core_app.room.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao: BaseDao<MovieTable> {
    @Query("DELETE FROM MovieTable WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM MovieTable")
    suspend fun getMovies(): List<MovieTable>

    @Query("SELECT * FROM MovieTable WHERE id=:id")
    fun checkMovie(id: Int): Flow<MovieTable?>
}