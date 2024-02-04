package com.movieappjc.presentation.viewmodel.person

import androidx.lifecycle.SavedStateHandle
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.usecases.PersonDetailUseCase
import com.movieappjc.app.route.DestinationApp
import com.movieappjc.app.route.DestinationKey
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
        personId = savedStateHandle[DestinationKey.PERSON_ID_KEY] ?: -1
        getPersonDetail()
    }

    fun getPersonDetail(){
        executeTask({ personDetailUseCase(personId) }, _person)
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        navigateTo(DestinationApp.MovieDetail(movieId))
    }
}