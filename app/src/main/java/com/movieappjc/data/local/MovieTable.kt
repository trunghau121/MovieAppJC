package com.movieappjc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.MovieEntity

@Entity
data class MovieTable(
    @PrimaryKey
    val id: Int? = null,
    val backdropPath: String? = null,
    val overview: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val title: String? = null,
    val voteAverage: Double? = null
) : ResponseMapper<MovieEntity> {
    override fun mapTo(): MovieEntity {
        return MovieEntity(
            id = id.value(),
            title = title.value(),
            posterPath = posterPath.value(),
            backdropPath = backdropPath.value(),
            releaseDate = releaseDate.value(),
            voteAverage = voteAverage.value(),
            overview = overview.value()
        )
    }

}

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