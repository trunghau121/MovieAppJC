package com.movieappjc.app.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.common.constants.VIETNAMESE
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.kColorPrimarySecond
import com.movieappjc.app.theme.kColorVulcan
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
                .padding(start = 40.aDp, top = 15.aDp, end = 10.aDp, bottom = 15.aDp),
            text = "Vietnamese",
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.sp
        )
        HorizontalDivider(color = kColorPrimarySecond)
        Text(
            modifier = Modifier
                .padding(start = 40.aDp, top = 10.aDp, end = 10.aDp, bottom = 10.aDp)
                .clickable {
                    onClickLanguage(Locale.ENGLISH)
                },
            text = "English",
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.aSp
        )
        Spacer(Modifier.width(25.aDp))
    }
}