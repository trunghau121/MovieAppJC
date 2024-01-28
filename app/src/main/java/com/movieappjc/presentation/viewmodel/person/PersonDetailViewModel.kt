package com.movieappjc.presentation.viewmodel.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.core_app.navigation.AppNavigator
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.usecases.PersonDetailUseCase
import com.movieappjc.presentation.route.DestinationApp
import com.movieappjc.presentation.route.DestinationKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailViewModel @Inject constructor(
    private val personDetailUseCase: PersonDetailUseCase,
    appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(appNavigator) {

    private val _person = MutableStateFlow<Resource<PersonEntity>>(Resource.Loading())
    val person = _person.asStateFlow()
    private var personId: Int = 0

    init {
        personId = savedStateHandle[DestinationKey.PERSON_ID_KEY] ?: -1
        getPersonDetail()
    }

    fun getPersonDetail(){
        executeTask(request = { personDetailUseCase(personId) }, onSuccess = _person)
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.MovieDetail(movieId))
        }
    }
}