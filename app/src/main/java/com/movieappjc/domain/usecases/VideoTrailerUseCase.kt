package com.movieappjc.domain.usecases

import com.core_app.network.DataState
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VideoTrailerUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<DataState<List<VideoEntity>>> {
        return movieRepository.getVideos(movieId)
    }
}