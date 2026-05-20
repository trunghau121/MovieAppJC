package com.movieappjc.presentation.screen.account_setting.componemt

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaceHolderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DashedLine(modifier = Modifier.weight(1f).height(1.dp), colorLine = Color(0xFFE1E3E9))
        Text(
            text = "Accounts shown on home end here",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color(0xFF9CA3AF),
            fontSize = 12.sp
        )
        DashedLine(modifier = Modifier.weight(1f).height(1.dp), colorLine = Color(0xFFE1E3E9))
    }
}