package com.movieappjc.presentation.screen.account_setting.componemt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountSettingHeader() {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Set accounts for Home screen",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Accounts will be displayed on the Home Screen in the order you select. You can change the account order by selecting the Handle button on the right side of account",
            fontSize = 14.sp,
            color = Color(0xFF6B7280),
            lineHeight = 20.sp
        )
    }
}