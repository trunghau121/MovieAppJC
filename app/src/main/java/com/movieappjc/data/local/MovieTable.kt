package com.movieappjc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.movieappjc.domain.entities.MovieEntity

@Entity
data class MovieTable(
    @PrimaryKey
    override val id: Int,
    override val backdropPath: String,
    override val overview: String,
    override val posterPath: String,
    override val releaseDate: String,
    override val title: String,
    override val voteAverage: Double
) : MovieEntity

fun MovieEntity.fromMovieEntity(): MovieTable {
    return MovieTable(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        overview = overview
    )
}