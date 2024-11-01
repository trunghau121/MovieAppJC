package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.common.constants.enterSearchText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.fontCustomNormal
import com.movieappjc.app.theme.kColorViolet

@Composable
fun SearchAppBar(
    valueText: String,
    onChangeText: (String) -> Unit,
    onBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .padding(end = 10.aDp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.padding(10.aDp),
            onClick = {
                keyboardController?.hide()
                onBack()
            }
        ) {
            Icon(
                modifier = Modifier.size(28.aDp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                tint = Color.White,
            )
        }

        BasicTextField(
            value = valueText,
            onValueChange = onChangeText,
            cursorBrush = SolidColor(Color.White),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions { keyboardController?.hide() },
            textStyle = MaterialTheme.typography.fontCustomMedium.copy(
                fontSize = 16.aSp,
                color = Color.White,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            singleLine = true
        ) { innerTextField ->
            Row(
                modifier = Modifier
                    .padding(end = 10.aDp)
                    .fillMaxWidth()
                    .background(
                        color = kColorViolet.copy(alpha = .2f),
                        shape = RoundedCornerShape(size = 16.aDp)
                    )
                    .padding(horizontal = 10.aDp, vertical = 12.aDp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(25.aDp),
                    imageVector = Icons.Filled.Search,
                    contentDescription = "",
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(width = 8.aDp))

                if (valueText.isEmpty()) {
                    Text(
                        text = LocalLanguages.current.enterSearchText(),
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                        fontSize = 16.aSp,
                        style = MaterialTheme.typography.fontCustomNormal.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    innerTextField()
                }

                if (valueText.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(width = 5.aDp))
                    Icon(
                        modifier = Modifier
                            .size(25.aDp)
                            .background(color = Color.Gray, shape = CircleShape)
                            .padding(5.aDp)
                            .clickable { onChangeText("") },
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "",
                        tint = Color.White
                    )
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
fun PreviewSearchAppBar() {
    SearchAppBar(
        valueText = "",
        onChangeText = {},
        onBack = {}
    )
}