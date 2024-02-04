package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.pxToDp
import com.core_app.network.DataState
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.components.CircularProgressBar
import com.movieappjc.app.components.ErrorApp
import com.movieappjc.app.common.utils.ComponentUtil
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel
import com.movieappjc.app.theme.kColorVulcan

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val isMovieFavorite by viewModel.isMovieFavorite.collectAsStateWithLifecycle()
    val castState by viewModel.castMovie.collectAsStateWithLifecycle()
    val stateMovieDetail = viewModel.movieDetail.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val statusBarHeight = ScreenUtil.getStatusBarHeight()
    val gradientColors = remember {
        mutableListOf(
            kColorVulcan.copy(alpha = 0.2f),
            kColorVulcan.copy(alpha = 0.1f),
            kColorVulcan.copy(alpha = 0.0f)
        )
    }

    var gradientHeight by remember { mutableIntStateOf(0) }

    val openPersonDetail = remember {
        { id: Int ->
            viewModel.openPersonDetailScreen(id)
        }
    }

    when (val state = stateMovieDetail.value) {
        is DataState.Success -> {
            val data = state.data
            Box(modifier = Modifier.fillMaxSize()){
                MainMovieDetail(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                    data = data,
                    castState = { castState },
                    openTrailerMovie = viewModel::openTrailerMovie,
                    openPersonDetailScreen = openPersonDetail
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gradientHeight.pxToDp() + statusBarHeight)
                        .background(brush = ComponentUtil.createGradientBrush(gradientColors))
                )

                MovieDetailAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = statusBarHeight)
                        .onGloballyPositioned { coordinates ->
                            gradientHeight = coordinates.size.height
                        },
                    isMovieFavorite = isMovieFavorite,
                    onSaveMovie = viewModel::saveFavoriteMovie,
                    onBack = viewModel::onBack
                )

            }
        }

        is DataState.Error -> {
            ErrorApp(error = state.error, onRetry = viewModel::getMovieDetail)
        }

        else -> {
            CircularProgressBar()
        }
    }
}