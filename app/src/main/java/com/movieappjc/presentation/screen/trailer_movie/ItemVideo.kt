package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemVideo(videoId: String, item: VideoEntity, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = if (item.key == videoId) kColorViolet else Color.Transparent)
            .clickable { onClick(item.key) }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = "https://i3.ytimg.com/vi_webp/${item.key}/hqdefault.webp",
                contentScale = ContentScale.Crop,
                transition = CrossFade,
                contentDescription = item.name
            )
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