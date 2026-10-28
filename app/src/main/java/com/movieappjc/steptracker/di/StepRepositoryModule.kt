package com.movieappjc.steptracker.di

import com.movieappjc.steptracker.data.repository.StepRepositoryImpl
import com.movieappjc.steptracker.domain.repository.StepRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StepRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(
        stepRepositoryImpl: StepRepositoryImpl
    ): StepRepository
}
