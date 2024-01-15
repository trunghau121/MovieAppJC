package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.theme.fontCustomSemiBold

@Composable
fun TitleMovieDetail(modifier: Modifier = Modifier, title: String) {
    Text(
        modifier = modifier.fillMaxWidth().padding(10.dp),
        text = title,
        color = Color.White,
        style = MaterialTheme.typography.fontCustomSemiBold,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
}