package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.extension.dpToPx
import com.core_app.extension.value
import com.core_app.repository.Resource
import com.core_app.utils.ImmutableHolder
import com.core_app.utils.StableHolder
import com.movieappjc.common.constants.noTrailerVideoText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.presentation.components.ErrorApp
import com.movieappjc.presentation.components.CircularProgressBar
import com.movieappjc.presentation.viewmodel.trailer_movie.TrailerMovieViewModel
import com.movieappjc.theme.fontCustomSemiBold

@Composable
fun TrailerMovieScreen(viewModel: TrailerMovieViewModel = hiltViewModel()) {
    Column {
        TrailerMovieAppBar(onBack = viewModel::onBack)
        when (val state = viewModel.videos.collectAsStateWithLifecycle().value) {
            is Resource.Success -> {
                val videos = state.data.value()
                if (videos.isNotEmpty()) {
                    ListTrailer(ImmutableHolder(videos))
                } else {
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
                ErrorApp(error = state.error, onRetry = viewModel::getTrailer)
            }

            else -> {
                CircularProgressBar()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ListTrailer(
    videos: ImmutableHolder<List<VideoEntity>>
) {
    val sizeItem = 80.dp
    var videoId by remember { mutableStateOf("") }
    if (videoId.isEmpty()) videoId = videos()[0].key

    val glidePreload = rememberGlidePreloadingData(
        data = videos(), preloadImageSize = Size(sizeItem.dpToPx(), sizeItem.dpToPx())
    ) { item, requestBuilder ->
        requestBuilder.load(item.getThumbnail())
    }

    LazyColumn {
        stickyHeader {
            TrailerYoutubePlayer(
                videoId = videoId,
                lifecycleOwner = StableHolder(LocalLifecycleOwner.current)
            )
        }
        items(glidePreload.size, key = { videos()[it].key }) { index ->
            val (item, preloadRequest) = glidePreload[index]
            TrailerItem(
                videoId = videoId,
                item = item,
                sizeItem = sizeItem,
                preloadRequest = { preloadRequest }
            ) {
                videoId = it
            }
        }
    }
}