package com.movieappjc.presentation.screen.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.movieappjc.app.common.constants.cancelText
import com.movieappjc.app.common.constants.descriptionFeedbackText
import com.movieappjc.app.common.constants.sendText
import com.movieappjc.app.common.constants.titleFeedbackText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.components.AppButton
import com.movieappjc.app.theme.fontCustomNormal
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.app.theme.kColorPrimarySecond
import com.movieappjc.app.theme.kColorVulcan

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    val textFeedBack = remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Column {
            Box(
                modifier = Modifier
                    .background(
                        color = kColorVulcan,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = LocalLanguages.current.titleFeedbackText(),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomSemiBold,
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = textFeedBack.value,
                        placeholder = {
                            Text(
                                text = LocalLanguages.current.descriptionFeedbackText(),
                                color = Color.Gray
                            )
                        },
                        textStyle = MaterialTheme.typography.fontCustomNormal.copy(fontSize = 16.sp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = kColorPrimarySecond.copy(alpha = .4f),
                            unfocusedContainerColor = kColorPrimarySecond.copy(alpha = .4f),
                            disabledContainerColor = kColorPrimarySecond.copy(alpha = .4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        onValueChange = {
                            textFeedBack.value = it
                        },
                        shape = RoundedCornerShape(8.dp),
                        minLines = 8,
                        maxLines = 8,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppButton(text = LocalLanguages.current.cancelText()) { onDismiss() }
                        Spacer(modifier = Modifier.weight(1f))
                        AppButton(text = LocalLanguages.current.sendText()) { onDismiss() }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}