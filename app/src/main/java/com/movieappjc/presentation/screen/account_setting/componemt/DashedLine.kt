package com.movieappjc.presentation.screen.account_setting.componemt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashedLine(modifier: Modifier, colorLine: Color) {
    Canvas(modifier) {
        drawLine(
            color = colorLine,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f), // [dash length, gap length] in pixels
                phase = 0f
            ),
            strokeWidth = 2f
        )
    }
}

@Preview
@Composable
fun DashedLinePreview() {
    DashedLine(Modifier.fillMaxWidth().height(1.dp), Color.White)
}