package com.movieappjc.presentation.screen.menu.compoment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.menu.MyMenuPlaceHolder

@Composable
fun MyMenuPlaceHolder(item: MyMenuPlaceHolder) {
    Box(
        modifier = Modifier.padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFEEEEEE))
                .padding(vertical = 20.dp)
        )
        Text(
            item.title,
            color = Color.LightGray,
            fontSize = 14.sp,
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 8.dp, horizontal = 7.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF, showBackground = true)
@Composable
fun MyMenuPlaceHolderPreview() {
    MyMenuPlaceHolder(MyMenuPlaceHolder("Out of Home"))
}