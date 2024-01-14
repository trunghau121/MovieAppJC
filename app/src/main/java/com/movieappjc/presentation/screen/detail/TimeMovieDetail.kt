package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.R
import com.movieappjc.common.constants.minutesText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.theme.fontCustomMedium

@Composable
fun TimeMovieDetail(movieDetailEntity: MovieDetailEntity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_date),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = movieDetailEntity.releaseDate,
            modifier = Modifier.padding(start = 4.dp, end = 10.dp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
        Divider(
            modifier = Modifier.width(1.dp).height(15.dp),
            color = Color.LightGray,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_time),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = "${movieDetailEntity.duration} ${LocalLanguages.current.minutesText()}",
            modifier = Modifier.padding(start = 4.dp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}