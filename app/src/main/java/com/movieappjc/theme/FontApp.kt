package com.movieappjc.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.movieappjc.R
import androidx.compose.material3.Typography

val sansFamily = FontFamily(
    Font(R.font.sf_light, FontWeight.Light),
    Font(R.font.sf_regular, FontWeight.Normal),
    Font(R.font.sf_medium, FontWeight.Medium),
    Font(R.font.sf_bold, FontWeight.Bold),
    Font(R.font.sf_semibold, FontWeight.SemiBold),
)

val Typography.fontCustomLight: TextStyle
    @Composable
    get() {
        return  TextStyle(
            fontFamily = sansFamily,
            fontWeight = FontWeight.Light,
        )
    }

val Typography.fontCustomNormal: TextStyle
    @Composable
    get() {
        return  TextStyle(
            fontFamily = sansFamily,
            fontWeight = FontWeight.Normal,
        )
    }

val Typography.fontCustomMedium: TextStyle
    @Composable
    get() {
        return  TextStyle(
            fontFamily = sansFamily,
            fontWeight = FontWeight.Medium,
        )
    }

val Typography.fontCustomBold: TextStyle
    @Composable
    get() {
        return  TextStyle(
            fontFamily = sansFamily,
            fontWeight = FontWeight.Bold,
        )
    }


val Typography.fontCustomSemiBold: TextStyle
    @Composable
    get() {
        return  TextStyle(
            fontFamily = sansFamily,
            fontWeight = FontWeight.SemiBold,
        )
    }