package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.value
import com.core_app.repository.Resource
import com.movieappjc.common.constants.noTrailerVideoText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.presentation.components.ErrorAppComponent
import com.movieappjc.presentation.components.LoadingCircle
import com.movieappjc.presentation.viewmodel.trailer_movie.TrailerMovieViewModel
import com.movieappjc.theme.fontCustomSemiBold

@Composable
fun TrailerMovieScreen(viewModel: TrailerMovieViewModel = hiltViewModel()) {
    Column {
        AppBarTrailerMovie(viewModel)
        when (val state = viewModel.videos.collectAsStateWithLifecycle().value) {
            is Resource.Success -> {
                val videos = state.data.value()
                if (videos.isNotEmpty()) {
                    ListTrailer(videos)
                }else {
                    Spacer(modifier = Modifier.height(100.dp))
                    Text(
                        text = LocalLanguages.current.noTrailerVideoText(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomSemiBold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
            is Resource.Error -> {
                ErrorAppComponent(error = state.error) {
                    viewModel.getTrailer()
                }
            }
            else -> {
                LoadingCircle()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ListTrailer(
    videos: List<VideoEntity>
) {
    var videoId by remember { mutableStateOf("") }
    if (videoId.isEmpty()) videoId = videos[0].key

    LazyColumn {
        stickyHeader {
            YoutubePlayerComponent(
                videoId = videoId,
                lifecycleOwner = LocalLifecycleOwner.current
            )
        }
        items(videos) { item ->
            ItemVideo(videoId, item){
                videoId = it
            }
        }
    }
}