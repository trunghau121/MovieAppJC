package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movieappjc.common.constants.nowText
import com.movieappjc.common.constants.popularText
import com.movieappjc.common.constants.soonText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.components.CustomTabBar

@Composable
fun MovieTabbedCompose(loadMovieTabbed: (Int) -> Unit) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        LocalLanguages.current.popularText(),
        LocalLanguages.current.nowText(),
        LocalLanguages.current.soonText()
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        CustomTabBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            selectedItemIndex = tabIndex,
            onSelectedTab = { tabIndex = it },
            items = { tabs }
        )
    }

    LaunchedEffect(tabIndex) {
        loadMovieTabbed(tabIndex)
    }
}