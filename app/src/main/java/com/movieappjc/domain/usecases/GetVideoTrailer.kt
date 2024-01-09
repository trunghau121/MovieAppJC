package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetVideoTrailer(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<Resource<List<VideoEntity>>> {
        return movieRepository.getVideos(movieId)
    }
}