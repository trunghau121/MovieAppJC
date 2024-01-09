package com.movieappjc.presentation.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.R
import com.movieappjc.common.constants.okayText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.fontCustomNormal
import com.movieappjc.theme.fontCustomSemiBold
import com.movieappjc.theme.kColorViolet
import com.movieappjc.theme.kColorVulcan

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AboutDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column{
            Box(
                modifier = Modifier
                    .background(
                        color = kColorVulcan,
                        shape = RoundedCornerShape(25.dp, 25.dp, 25.dp, 25.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomSemiBold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomNormal,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    GlideImage(
                        modifier = Modifier.height(20.dp),
                        model = R.drawable.ic_horizontal_logo,
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    ElevatedButton(
                        onClick = onDismiss,
                        colors= ButtonDefaults.buttonColors(containerColor = kColorViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Text(
                            text = LocalLanguages.current.okayText(),
                            style = MaterialTheme.typography.fontCustomMedium,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}