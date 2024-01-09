package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core_app.extension.useDebounce
import com.movieappjc.common.constants.enterSearchText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.kColorViolet

@Composable
fun AppBarSearch(viewModel: SearchMovieViewModel) {
    var valueText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    valueText.useDebounce { viewModel.searchMovie(it) }
    Column(Modifier.statusBarsPadding().padding(end = 10.dp)) {
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

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                value = valueText,
                placeholder = {
                    Text(
                        text = LocalLanguages.current.enterSearchText(),
                        modifier = Modifier.fillMaxHeight(),
                        color = Color.Gray,
                    )
                },
                textStyle = MaterialTheme.typography.fontCustomMedium.copy(fontSize = 16.sp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = kColorViolet.copy(alpha = .2f),
                    unfocusedContainerColor = kColorViolet.copy(alpha = .2f),
                    disabledContainerColor = kColorViolet.copy(alpha = .2f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = { valueText = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { keyboardController?.hide() },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "",
                        tint = Color.LightGray
                    )
                },
                trailingIcon = {
                    if (valueText.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable { valueText = "" },
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "",
                            tint = Color.LightGray
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}