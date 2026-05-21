package com.movieappjc.presentation.screen.menu.compoment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashedSlotMenu() {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
    )
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun DashedSlotMenuPreview() {
    DashedSlotMenu()
}