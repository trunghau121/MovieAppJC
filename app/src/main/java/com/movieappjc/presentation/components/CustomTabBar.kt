package com.movieappjc.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet

@Composable
fun CustomTabBar(items: () -> List<String>,
                 modifier: Modifier,
                 selectedItemIndex: Int = 0,
                 onSelectedTab: (index: Int) -> Unit) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        items().forEachIndexed { index, title ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)) {
                MyTabItem(
                    modifier = Modifier,
                    onClick = {
                        onSelectedTab(index)
                    },
                    title = title
                )
                if (index == selectedItemIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .padding(horizontal = 10.dp)
                            .background(
                                color = kColorViolet,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }

        }
    }
}

@Composable
private fun MyTabItem(
    modifier: Modifier,
    onClick: () -> Unit,
    title: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}