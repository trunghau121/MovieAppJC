package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MovieDetailAppBar(
    modifier: Modifier = Modifier,
    isMovieFavorite: Boolean,
    onSaveMovie: () -> Unit,
    onBack: () -> Unit
) {
    val tintColor = remember(isMovieFavorite) {
        if(isMovieFavorite) Color.Red else Color.White
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.padding(10.dp),
            onClick = onBack
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.padding(10.dp),
            onClick = onSaveMovie
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Filled.Favorite,
                contentDescription = "",
                tint = tintColor
            )
        }
    }
}