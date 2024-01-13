package com.movieappjc.presentation.screen.detail.cast_crew

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.utils.ImmutableHolder
import com.movieappjc.common.constants.castText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.theme.fontCustomMedium

@Composable
fun CastCrewComponent(cast: ImmutableHolder<List<CastEntity>>) {
    val glidePreload = rememberGlidePreloadingData(
        data = cast(), preloadImageSize = Size(60f, 60f)
    ) { item, requestBuilder ->
        requestBuilder.load(item.getProfileUrl())
    }
    Column(modifier = Modifier.height(210.dp)) {
        Text(
            text = LocalLanguages.current.castText(),
            modifier = Modifier.padding(start = 15.dp, top = 20.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 17.sp,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyHorizontalGrid(
            modifier = Modifier.height(140.dp),
            rows = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(glidePreload.size, key = { cast()[it].id }) {
                val (item, preloadRequest) = glidePreload[it]
                ItemCastComponent(
                    castEntity = item,
                    preloadRequest = { preloadRequest }
                )
            }
        }
    }
}