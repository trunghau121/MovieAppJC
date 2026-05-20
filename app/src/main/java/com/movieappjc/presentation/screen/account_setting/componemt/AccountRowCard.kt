package com.movieappjc.presentation.screen.account_setting.componemt

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.account_setting.data.AccountItem
import com.movieappjc.presentation.screen.account_setting.data.AccountItemBlank
import com.movieappjc.presentation.screen.account_setting.data.AccountItemEmpty

@Composable
fun AccountRowCard(modifier: Modifier, item: AccountItem) {
    val interactionSource = remember { MutableInteractionSource() }
    val isBlank = item is AccountItemBlank
    val isEmpty = item is AccountItemEmpty
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isEmpty || isBlank) {
                    Modifier
                } else {
                    Modifier.shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                        ambientColor = DefaultShadowColor.copy(alpha = 0.2f),
                        spotColor = DefaultShadowColor.copy(alpha = 0.2f)
                    )
                }
            )
            .drawBehind {
                if (isEmpty) {
                    val stroke = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                    )
                    drawRoundRect(
                        color = Color(0xFFE1E3E9),
                        style = stroke,
                        cornerRadius = CornerRadius(13.dp.toPx())
                    )
                }
            },
        colors = CardDefaults.cardColors(containerColor = if (isEmpty || isBlank) {
            Color.Transparent
        } else if (item.isAccountHome || item.isAccountDefault) {
            Color.White
        } else {
            Color(0xFFEBEFF5)
        }),
        interactionSource = interactionSource,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .alpha(if (isBlank || isEmpty) 0f else 1.0f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.number,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF9CA3AF)
                )
            }
            IconButton(onClick = { }, enabled = false) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Reorder handle",
                    tint = Color(0xFF6B7280),
                    modifier = Modifier
                )
            }
        }
    }
}