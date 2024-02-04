package com.movieappjc.presentation.screen.detail.cast_crew

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CastCrewItem(
    castEntity: CastEntity,
    sizeItem: Dp,
    preloadRequest: () -> RequestBuilder<Drawable>,
    openPersonDetailScreen: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(230.dp)
            .padding(horizontal = 10.dp)
            .clickable {
                openPersonDetailScreen(castEntity.id)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = SolidColor(kColorViolet),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(start = 67.dp, top = 7.dp, end = 8.dp, bottom = 7.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = castEntity.name,
                color = Color.White,
                style = MaterialTheme.typography.fontCustomMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = castEntity.character,
                color = Color.LightGray,
                style = MaterialTheme.typography.fontCustomMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        GlideImage(
            modifier = Modifier
                .size(sizeItem)
                .border(border = BorderStroke(2.dp, color = kColorViolet), shape = CircleShape)
                .clip(shape = CircleShape)
                .background(color = Color.LightGray),
            model = castEntity.getProfileUrl(),
            transition = CrossFade,
            contentScale = ContentScale.Crop,
            contentDescription = castEntity.name
        ) { primaryRequest ->
            primaryRequest.thumbnail(preloadRequest())
        }
    }
}