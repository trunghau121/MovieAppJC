package com.movieappjc.presentation.screen.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.movieappjc.R
import com.movieappjc.app.common.constants.movieText
import com.movieappjc.app.theme.fontCustomSemiBold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(drawerState: DrawerState, openSearchMovie: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = movieText,
                color = Color.White,
                style = MaterialTheme.typography.fontCustomSemiBold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        navigationIcon = {
            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu),
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        },
        actions = {
            IconButton(onClick = openSearchMovie) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        },
    )
}