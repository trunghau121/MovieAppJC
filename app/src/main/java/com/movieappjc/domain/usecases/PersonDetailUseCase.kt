package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonDetailUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(personId: Int): Flow<Resource<PersonEntity>> {
        return movieRepository.getPersonDetail(personId)
    }
}