package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel

@Composable
fun MovieDetailHeader(viewModel: MovieDetailViewModel) {
    val isMovieFavorite by viewModel.isMovieFavorite.collectAsState()
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
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.padding(10.dp),
                onClick = { viewModel.saveFavoriteMovie() }
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "",
                    tint = if(isMovieFavorite) Color.Red else Color.White
                )
            }
        }
    }
}