package com.movieappjc.presentation.screen.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.app.common.constants.favoriteMoviesText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium

@Composable
fun FavoriteAppBar(onBack : () -> Unit) {
    Row(
        modifier = Modifier.statusBarsPadding(),
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
        Text(
            text = LocalLanguages.current.favoriteMoviesText(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 20.sp,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewFavoriteAppBar() {
    FavoriteAppBar{}
}