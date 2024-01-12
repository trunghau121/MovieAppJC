package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.common.constants.showLessText
import com.movieappjc.common.constants.showMoreText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.theme.fontCustomLight
import com.webtoonscorp.android.readmore.foundation.ToggleArea
import com.webtoonscorp.android.readmore.material.ReadMoreText

@Composable
fun DescriptionMovieDetail(overview: String) {
    val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
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