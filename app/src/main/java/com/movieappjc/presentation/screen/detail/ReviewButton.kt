package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.theme.fontCustomMedium

@Composable
fun ReviewButton(modifier: Modifier, voteAverage: Double) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Filled.Star,
                contentDescription = "",
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = voteAverage.toString(),
                color = Color.White,
                style = MaterialTheme.typography.fontCustomMedium,
                fontSize = 14.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}