package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.core_app.extension.value
import com.core_app.utils.ImmutableHolder
import com.core_app.utils.StableHolder
import com.movieappjc.app.common.constants.noTrailerVideoText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.components.ToUI
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.app.theme.kColorViolet
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.presentation.viewmodel.trailer_movie.TrailerMovieViewModel

@Composable
fun TrailerMovieScreen(viewModel: TrailerMovieViewModel = hiltViewModel()) {
    val state by viewModel.videos.collectAsStateWithLifecycle()
    Column {
        TrailerMovieAppBar(onBack = viewModel::onBack)
        state.ToUI(
            content = {
                val videos = it.value()
                if (videos.isNotEmpty()) {
                    ListTrailer(ImmutableHolder(videos))
                } else {
                    Spacer(modifier = Modifier.height(100.aDp))
                    Text(
                        text = LocalLanguages.current.noTrailerVideoText(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomSemiBold,
                        color = Color.White,
                        fontSize = 18.aSp
                    )
                }
            },
            onRetry = viewModel::getTrailer
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ListTrailer(
    videos: ImmutableHolder<List<VideoEntity>>
) {
    val sizeItem = 80.aDp
    var videoId by remember { mutableStateOf("") }
    if (videoId.isEmpty()) videoId = videos()[0].key

    LazyColumn {
        stickyHeader {
            TrailerYoutubePlayer(
                videoId = videoId,
                lifecycleOwner = StableHolder(LocalLifecycleOwner.current)
            )
        }
        items(videos().size, key = { videos()[it].key }) { index ->
            val item = videos()[index]
            TrailerItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (item.key == videoId) kColorViolet else Color.Transparent),
                item = item,
                sizeItem = sizeItem
            ) {
                videoId = it
            }
        }
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewTrailerMovieScreen() {
    TrailerMovieScreen()
}