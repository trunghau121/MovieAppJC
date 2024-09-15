package com.movieappjc.presentation.viewmodel.person

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.usecases.PersonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val personDetailUseCase: PersonDetailUseCase,
    appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(appNavigator) {

    private val _person = MutableStateFlow<DataState<PersonEntity>>(DataState.Loading())
    val person = _person.asStateFlow()
    private var personId: Int = 0

    init {
        personId = savedStateHandle.toRoute<Routes.PersonDetailScreen>().personId
        getPersonDetail()
    }

    fun getPersonDetail(){
        personDetailUseCase(personId).executeTask(_person)
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        navigateTo(Routes.MovieDetail(movieId))
    }
}