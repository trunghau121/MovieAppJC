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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.R
import com.movieappjc.app.common.constants.okayText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.fontCustomNormal
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.app.theme.kColorViolet
import com.movieappjc.app.theme.kColorVulcan

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
                        shape = RoundedCornerShape(25.aDp, 25.aDp, 25.aDp, 25.aDp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.aDp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.aDp))
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomSemiBold,
                        color = Color.White,
                        fontSize = 18.aSp
                    )
                    Spacer(modifier = Modifier.height(8.aDp))
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.aDp, start = 15.aDp, end = 15.aDp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.fontCustomNormal,
                        color = Color.White,
                        fontSize = 16.aSp
                    )
                    Spacer(modifier = Modifier.height(15.aDp))
                    AsyncImage(
                        modifier = Modifier.height(20.aDp),
                        model = R.drawable.ic_horizontal_logo,
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight
                    )
                    Spacer(modifier = Modifier.height(30.aDp))
                    ElevatedButton(
                        onClick = onDismiss,
                        colors= ButtonDefaults.buttonColors(containerColor = kColorViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.aDp))
                    ) {
                        Text(
                            text = LocalLanguages.current.okayText(),
                            style = MaterialTheme.typography.fontCustomMedium,
                            color = Color.White,
                            fontSize = 17.aSp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.aDp))
                }
            }
        }
    }
}

@Preview(
    name = "CustomTabBar",
    showBackground = true,
    backgroundColor = 0xFF141221
)
@Composable
fun PreviewAboutDialog() {
    AboutDialog(title = "Dialog", description = "This is description dialog"){}
}