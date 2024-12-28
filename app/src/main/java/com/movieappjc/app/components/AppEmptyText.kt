package com.movieappjc.app.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomNormal

@Composable
fun AppEmptyText(emptyText: String, top: Dp = 150.aDp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.aDp, top = top, end = 20.aDp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emptyText,
            style = MaterialTheme.typography.fontCustomNormal,
            color = Color.Gray,
            fontSize = 17.aSp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewAppEmptyText() {
    AppEmptyText(emptyText = "There are no data")
}