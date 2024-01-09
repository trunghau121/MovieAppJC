package com.movieappjc.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.theme.fontCustomNormal

@Composable
fun EmptyTextApp(emptyText: String, top: Dp = 150.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = top, end = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emptyText,
            style = MaterialTheme.typography.fontCustomNormal,
            color = Color.Gray,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}