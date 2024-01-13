package com.movieappjc.presentation.screen.trailer_movie

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemVideo(
    videoId: String,
    item: VideoEntity,
    preloadRequest: () -> RequestBuilder<Drawable>,
    onClick: (String) -> Unit
) {
    val backgroundColor = remember(videoId) {
        if (item.key == videoId) kColorViolet else Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .clickable { onClick(item.key) }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            modifier = Modifier
                .size(80.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = Color.LightGray),
            model = item.getThumbnail(),
            contentScale = ContentScale.Crop,
            transition = CrossFade,
            contentDescription = item.name
        ) { primaryRequest ->
            primaryRequest.thumbnail(preloadRequest())
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.name,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 18.sp,
            textAlign = TextAlign.Start
        )
    }
}