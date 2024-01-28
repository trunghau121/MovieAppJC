package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ReviewButton(modifier: Modifier, voteAverage: Double) {
    Row(modifier) {
        val maxVote = 10
        val starCount = 5
        repeat(starCount) { starIndex ->
            val voteStarCount = voteAverage / (maxVote / starCount)
            val asset = when {
                voteStarCount >= starIndex + 1 -> {
                    Icons.Filled.Star
                }
                voteStarCount in starIndex.toDouble()..(starIndex + 1).toDouble() -> {
                    Icons.AutoMirrored.Filled.StarHalf
                }
                else -> {
                    Icons.Filled.StarOutline
                }
            }
            Icon(
                imageVector = asset,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}