package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.R
import com.movieappjc.common.constants.minutesText
import com.movieappjc.common.constants.showLessText
import com.movieappjc.common.constants.showMoreText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.theme.fontCustomLight
import com.movieappjc.theme.fontCustomMedium
import com.webtoonscorp.android.readmore.foundation.ToggleArea
import com.webtoonscorp.android.readmore.material.ReadMoreText

@Composable
fun ContentMovieDetail(releaseDate: String, duration: Int, overview: String) {
    val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.icon_date),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = releaseDate,
            modifier = Modifier.padding(start = 4.dp, end = 10.dp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
        Divider(
            modifier = Modifier
                .width(1.dp)
                .height(15.dp),
            color = Color.LightGray,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.icon_time),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = "$duration ${LocalLanguages.current.minutesText()}",
            modifier = Modifier.padding(start = 4.dp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
    ReadMoreText(
        text = overview,
        expanded = expanded,
        modifier = Modifier.fillMaxWidth(),
        onExpandedChange = onExpandedChange,
        contentPadding = PaddingValues(start = 15.dp, top = 15.dp, end = 15.dp),
        color = Color.LightGray,
        fontSize = 15.sp,
        style = MaterialTheme.typography.fontCustomLight,
        /* read more */
        readMoreText = LocalLanguages.current.showMoreText(),
        readMoreMaxLines = 3,
        readMoreColor = Color.Gray,
        readMoreFontSize = 15.sp,
        readMoreFontWeight = FontWeight.SemiBold,
        /* read less */
        readLessText = LocalLanguages.current.showLessText(),
        toggleArea = ToggleArea.More
    )
}