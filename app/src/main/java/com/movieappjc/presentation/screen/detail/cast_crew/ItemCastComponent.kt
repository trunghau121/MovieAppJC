package com.movieappjc.presentation.screen.detail.cast_crew

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemCastComponent(castEntity: CastEntity) {
    Box(
        modifier = Modifier.width(230.dp).padding(horizontal = 10.dp),
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
                style = MaterialTheme.typography.fontCustomMedium,
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = castEntity.character,
                color = Color.LightGray,
                style = MaterialTheme.typography.fontCustomMedium,
                fontSize = 12.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Card(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            border = BorderStroke(2.dp, color = kColorViolet)
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = "${Endpoints.baseUrlImage200}${castEntity.profilePath}",
                transition = CrossFade,
                contentScale = ContentScale.Crop,
                contentDescription = castEntity.name
            )
        }
    }
}