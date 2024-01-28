package com.movieappjc.presentation.screen.detail.genres

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.domain.entities.GenreEntity
import com.movieappjc.theme.fontCustomMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreList(modifier: Modifier = Modifier, genres: () -> List<GenreEntity>) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        genres().forEach {
            key(it.id) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .border(
                            border = BorderStroke(1.dp, color = Color.LightGray),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 7.dp),
                    text = it.name,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.fontCustomMedium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}