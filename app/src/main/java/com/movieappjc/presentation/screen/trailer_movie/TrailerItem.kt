package com.movieappjc.presentation.screen.trailer_movie

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.domain.entities.VideoEntity

@Composable
fun TrailerItem(
    modifier: Modifier = Modifier,
    item: VideoEntity,
    sizeItem: Dp,
    onClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick(item.key) }
            .padding(horizontal = 15.aDp, vertical = 10.aDp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(sizeItem)
                .clip(shape = RoundedCornerShape(16.aDp))
                .background(color = Color.LightGray),
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.getThumbnail())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = item.name
        )
        Spacer(modifier = Modifier.width(10.aDp))
        Text(
            text = item.name,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 18.aSp,
            textAlign = TextAlign.Start
        )
    }
}