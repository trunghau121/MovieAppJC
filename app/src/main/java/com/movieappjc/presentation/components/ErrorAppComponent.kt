package com.movieappjc.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core_app.repository.ErrorType
import com.movieappjc.common.constants.feedbackText
import com.movieappjc.common.constants.noNetworkText
import com.movieappjc.common.constants.retryText
import com.movieappjc.common.constants.somethingWentWrongText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.screen.feedback.FeedbackDialog
import com.movieappjc.theme.fontCustomNormal

@Composable
fun ErrorAppComponent(error: Pair<ErrorType, String>, onRetry: () -> Unit) {
    val convertText = if (error.first == ErrorType.FROM_API) {
        error.second
    }else {
        when(error.first){
            ErrorType.INTERNET -> LocalLanguages.current.noNetworkText()
            else -> LocalLanguages.current.somethingWentWrongText()
        }
    }
    val feedbackDialog = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = convertText,
                style = MaterialTheme.typography.fontCustomNormal,
                color = Color.White,
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ButtonApp(text = LocalLanguages.current.feedbackText()) { feedbackDialog.value = true }
                ButtonApp(text = LocalLanguages.current.retryText(), onClick = onRetry)
            }
        }
    }

    if (feedbackDialog.value) {
        FeedbackDialog { feedbackDialog.value = false }
    }
}