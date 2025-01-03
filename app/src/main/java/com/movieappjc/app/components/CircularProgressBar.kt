package com.movieappjc.app.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.core_app.extension.aDp
import com.movieappjc.app.theme.kColorViolet

@Composable
fun CircularProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(50.aDp), color = kColorViolet)
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewCircularProgressBar() {
    CircularProgressBar()
}