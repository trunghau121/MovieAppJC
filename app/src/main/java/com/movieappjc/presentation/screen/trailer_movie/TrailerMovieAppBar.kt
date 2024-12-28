package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.common.constants.watchTrailersText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium

@Composable
fun TrailerMovieAppBar(onBack: () -> Unit){
    Row(
        modifier = Modifier.statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.padding(10.aDp),
            onClick = onBack
        ) {
            Icon(
                modifier = Modifier.size(28.aDp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.White,
            )
        }
        Text(
            text = LocalLanguages.current.watchTrailersText(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 20.aSp,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewTrailerMovieAppBar() {
    TrailerMovieAppBar{}
}