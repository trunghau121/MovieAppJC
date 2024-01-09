package com.movieappjc.presentation.screen.trailer_movie

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.common.constants.watchTrailersText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.viewmodel.trailer_movie.TrailerMovieViewModel
import com.movieappjc.theme.fontCustomMedium

@Composable
fun AppBarTrailerMovie(viewModel: TrailerMovieViewModel){
    Column(
        Modifier.statusBarsPadding()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(10.dp),
                onClick = { viewModel.onBack() }
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = Color.White,
                )
            }
            Text(
                text = LocalLanguages.current.watchTrailersText(),
                color = Color.White,
                style = MaterialTheme.typography.fontCustomMedium,
                fontSize = 20.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}