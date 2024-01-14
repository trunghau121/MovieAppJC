package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.core_app.utils.StableHolder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TrailerYoutubePlayer(
    videoId: String,
    lifecycleOwner: StableHolder<LifecycleOwner>
){
    var player by remember{
        mutableStateOf<YouTubePlayer?>(null)
    }
    player?.loadVideo(videoId, 0f)
    AndroidView(factory = { context ->
        YouTubePlayerView(context = context).apply {
            lifecycleOwner().lifecycle.addObserver(this)
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    super.onReady(youTubePlayer)
                    player = youTubePlayer
                    player?.loadVideo(videoId, 0f)
                }
            })
        }
    })
}