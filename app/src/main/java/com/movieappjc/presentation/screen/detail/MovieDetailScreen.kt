package com.movieappjc.presentation.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.movieappjc.common.constants.watchTrailersText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.components.CircularProgressBar
import com.movieappjc.presentation.components.ErrorApp
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet
import com.movieappjc.theme.kColorVulcan

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val isMovieFavorite by viewModel.isMovieFavorite.collectAsStateWithLifecycle()
    val castState by viewModel.castMovie.collectAsStateWithLifecycle()
    val stateMovieDetail = viewModel.movieDetail.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var isFabVisible by rememberSaveable { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) {
                    isFabVisible = false
                }
                if (available.y > 1) {
                    isFabVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Box(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        when (val state = stateMovieDetail.value) {
            is Resource.Success -> {
                val data = state.data
                MainMovieDetail(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    data = data,
                    castState = { castState },
                    isMovieFavorite = isMovieFavorite,
                    openTrailerMovie = viewModel::openTrailerMovie,
                    saveFavoriteMovie = viewModel::saveFavoriteMovie,
                    onBack = viewModel::onBack
                )
            }

            is Resource.Error -> {
                isFabVisible = false
                ErrorApp(error = state.error, onRetry = viewModel::getMovieDetail)
            }

            else -> {
                CircularProgressBar()
            }
        }
        ToggleBookmarkFab(
            modifier = Modifier.align(Alignment.BottomCenter),
            isVisible = isFabVisible,
            openTrailerMovie = viewModel::openTrailerMovie
        )
    }
}

@Composable
private fun ToggleBookmarkFab(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    openTrailerMovie: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier.padding(end = 15.dp, bottom = 15.dp),
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it * 2 }),
        exit = slideOutVertically(targetOffsetY = { it * 2 }),
    ) {
        Text(
            modifier = modifier
                .background(color = kColorVulcan, shape = RoundedCornerShape(20.dp))
                .border(
                    border = BorderStroke(1.dp, color = kColorViolet),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 30.dp, vertical = 5.dp)
                .clickable { openTrailerMovie() },
            text = LocalLanguages.current.watchTrailersText(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}