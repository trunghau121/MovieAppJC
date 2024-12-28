package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomSemiBold

@Composable
fun TitleMovieDetail(modifier: Modifier = Modifier, title: String) {
    Text(
        modifier = modifier.padding(horizontal = 10.aDp, vertical = 5.aDp),
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.fontCustomSemiBold,
        fontSize = 18.aSp,
        textAlign = TextAlign.Start
    )
}