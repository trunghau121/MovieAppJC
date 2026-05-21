package com.movieappjc.presentation.screen.menu.compoment

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.menu.MenuItem

@Composable
fun MyMenuItemCard(
    item: MenuItem,
    modifier: Modifier = Modifier,
    onRemoveOrAdd: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        onClick = {},
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(15.dp),
                clip = false,
                ambientColor = DefaultShadowColor.copy(alpha = 0.4f),
                spotColor = DefaultShadowColor.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        interactionSource = interactionSource
    ) {
        Box(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 10.dp,
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 32.dp
                    )
            ) {
                Text(
                    item.title,
                    fontSize = 13.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (!item.isFixed) {
                    IconButton(
                        onClick = onRemoveOrAdd,
                        modifier = Modifier
                            .size(22.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isHome) Icons.Rounded.RemoveCircle else Icons.Rounded.AddCircle,
                            contentDescription = null,
                            tint = if (item.isHome) Color.Gray else Color(0xFF0056D2),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Image(
                painter = painterResource(item.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}