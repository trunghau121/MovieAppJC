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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.app.common.constants.castText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.domain.entities.CastEntity

@Composable
fun CastCrewList(
    modifier: Modifier = Modifier,
    cast: () -> List<CastEntity>,
    openPersonDetailScreen: (Int) -> Unit
) {
    val sizeItem = 60.dp
    val casts = cast()
    Column(modifier = modifier.height(210.dp)) {
        Text(
            text = LocalLanguages.current.castText(),
            modifier = Modifier.padding(start = 15.dp, top = 15.dp),
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
            items(casts.size, key = { casts[it].id }) {
                val item = casts[it]
                CastCrewItem(
                    castEntity = item,
                    sizeItem = sizeItem,
                    openPersonDetailScreen = openPersonDetailScreen
                )
            }
        }
    }
}