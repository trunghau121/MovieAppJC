package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core_app.extension.useDebounce
import com.movieappjc.common.constants.enterSearchText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.fontCustomNormal
import com.movieappjc.theme.kColorViolet

@Composable
fun AppBarSearch(viewModel: SearchMovieViewModel) {
    var valueText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    valueText.useDebounce { viewModel.searchMovie(it) }
    Column(
        Modifier
            .statusBarsPadding()
            .padding(end = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(10.dp),
                onClick = {
                    keyboardController?.hide()
                    viewModel.onBack()
                }
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = Color.White,
                )
            }

            BasicTextField(
                value = valueText,
                onValueChange = { newText ->
                    valueText = newText
                },
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { keyboardController?.hide() },
                textStyle = MaterialTheme.typography.fontCustomMedium.copy(
                    fontSize = 16.sp,
                    color = Color.White
                ),
                singleLine = true
            ) { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .fillMaxWidth()
                        .background(
                            color = kColorViolet.copy(alpha = .2f),
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        imageVector = Icons.Filled.Search,
                        contentDescription = "",
                        tint = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(width = 8.dp))

                    if (valueText.isEmpty()) {
                        Text(
                            text = LocalLanguages.current.enterSearchText(),
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.fontCustomNormal,
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }

                    if (valueText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(width = 5.dp))
                        Icon(
                            modifier = Modifier
                                .size(25.dp)
                                .background(color = Color.Gray, shape = CircleShape)
                                .padding(5.dp)
                                .clickable { valueText = "" },
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}