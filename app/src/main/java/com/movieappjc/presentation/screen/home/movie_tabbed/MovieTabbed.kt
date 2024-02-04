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
import com.movieappjc.app.common.constants.nowText
import com.movieappjc.app.common.constants.popularText
import com.movieappjc.app.common.constants.soonText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.components.CustomTabBar

@Composable
fun MovieTabbed(loadMovieTabbed: (Int) -> Unit) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    val tabs = with(LocalLanguages.current) {
        listOf(popularText(), nowText(), soonText())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        CustomTabBar(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            selectedItemIndex = tabIndex,
            onSelectedTab = { tabIndex = it },
            items = { tabs }
        )
    }

    LaunchedEffect(tabIndex) {
        loadMovieTabbed(tabIndex)
    }
}