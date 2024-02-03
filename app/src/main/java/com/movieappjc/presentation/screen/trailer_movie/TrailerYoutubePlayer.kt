package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.core_app.utils.StableHolder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun TrailerYoutubePlayer(
    videoId: String,
    lifecycleOwner: StableHolder<LifecycleOwner>
) {
    AndroidView(
        factory = { context ->
            YouTubePlayerView(context = context).apply {
                lifecycleOwner().lifecycle.addObserver(this)
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
            }
        },
        update = { youtubePlayer ->
            youtubePlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback{
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
        }
    )
}