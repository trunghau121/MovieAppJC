package com.movieappjc.app.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.kColorViolet

@Composable
fun AppButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 20.aDp),
        shape = RoundedCornerShape(20.aDp),
        colors = ButtonDefaults.buttonColors(
            containerColor = kColorViolet,
            contentColor = Color.White
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.aSp,
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
fun PreviewAppButton() {
    AppButton("Button"){}
}