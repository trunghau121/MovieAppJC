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
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.domain.entities.GenreEntity
import com.movieappjc.app.theme.fontCustomMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreList(modifier: Modifier = Modifier, genres: () -> List<GenreEntity>) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        genres().forEach {
            key(it.id) {
                Spacer(modifier = Modifier.width(5.aDp))
                Text(
                    modifier = Modifier
                        .padding(vertical = 5.aDp)
                        .border(
                            border = BorderStroke(1.aDp, color = Color.LightGray),
                            shape = RoundedCornerShape(16.aDp)
                        )
                        .padding(horizontal = 20.aDp, vertical = 7.aDp),
                    text = it.name,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.fontCustomMedium,
                    fontSize = 14.aSp,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(5.aDp))
            }
        }
    }
}