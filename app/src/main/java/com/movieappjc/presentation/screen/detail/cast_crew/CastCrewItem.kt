package com.movieappjc.presentation.screen.detail.cast_crew

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.kColorViolet
import com.movieappjc.domain.entities.CastEntity

@Composable
fun CastCrewItem(
    castEntity: CastEntity,
    sizeItem: Dp,
    openPersonDetailScreen: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(230.aDp)
            .padding(horizontal = 10.aDp)
            .clickable {
                openPersonDetailScreen(castEntity.id)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.aDp,
                    brush = SolidColor(kColorViolet),
                    shape = RoundedCornerShape(26.aDp)
                )
                .padding(start = 67.aDp, top = 7.aDp, end = 8.aDp, bottom = 7.aDp)
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
                fontSize = 15.aSp,
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
                fontSize = 12.aSp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AsyncImage(
            modifier = Modifier
                .size(sizeItem)
                .border(border = BorderStroke(2.aDp, color = kColorViolet), shape = CircleShape)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                }
                .background(color = Color.LightGray),
            model = ImageRequest.Builder(LocalContext.current)
                .data(castEntity.getProfileUrl())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = castEntity.name
        )
    }
}

@Preview(showBackground = true, device = Devices.NEXUS_5)
@Composable
fun SquareComposablePreview() {
    Text(
        text = "AAAA",
        color = Color.LightGray,
        style = MaterialTheme.typography.fontCustomMedium.copy(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        fontSize = 12.aSp,
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}