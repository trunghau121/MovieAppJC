package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.R
import com.movieappjc.app.common.constants.minutesText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium

@Composable
fun TimeMovieDetail(
    modifier: Modifier = Modifier,
    releaseDate: String,
    duration: Int
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 30.aDp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(18.aDp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_date),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = releaseDate,
            modifier = Modifier.padding(start = 4.aDp, end = 10.aDp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.aSp,
            textAlign = TextAlign.Start
        )
        VerticalDivider(
            modifier = Modifier.width(1.aDp).height(15.aDp),
            color = Color.LightGray,
        )
        Spacer(modifier = Modifier.width(10.aDp))
        Icon(
            modifier = Modifier.size(18.aDp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_time),
            contentDescription = "",
            tint = Color.LightGray,
        )
        Text(
            text = "$duration ${LocalLanguages.current.minutesText()}",
            modifier = Modifier.padding(start = 4.aDp),
            color = Color.LightGray,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 14.aSp,
            textAlign = TextAlign.Start
        )
    }
}