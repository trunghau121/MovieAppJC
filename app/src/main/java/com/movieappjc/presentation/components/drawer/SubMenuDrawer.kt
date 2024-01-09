package com.movieappjc.presentation.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.common.constants.VIETNAMESE
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorPrimarySecond
import com.movieappjc.theme.kColorVulcan
import java.util.Locale

@Composable
fun SubMenuDrawer(onClickLanguage: (Locale) -> Unit){
    Column(
        Modifier.background(color = kColorVulcan.copy(alpha = 0.8f))) {
        Text(
            modifier = Modifier
                .clickable {
                    onClickLanguage(VIETNAMESE)
                }
                .padding(start = 40.dp, top = 15.dp, end = 10.dp, bottom = 15.dp),
            text = "Vietnamese",
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.sp
        )
        Divider(color = kColorPrimarySecond)
        Text(
            modifier = Modifier
                .padding(start = 40.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                .clickable {
                    onClickLanguage(Locale.ENGLISH)
                },
            text = "English",
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.sp
        )
        Spacer(Modifier.width(25.dp))
    }
}