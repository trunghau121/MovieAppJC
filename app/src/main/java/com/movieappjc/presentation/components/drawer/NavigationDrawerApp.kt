package com.movieappjc.presentation.components.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.R
import com.movieappjc.common.constants.aboutDescriptionText
import com.movieappjc.common.constants.aboutText
import com.movieappjc.common.constants.favoriteMoviesText
import com.movieappjc.common.constants.feedbackText
import com.movieappjc.common.constants.languageText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.screen.about.AboutDialog
import com.movieappjc.presentation.screen.feedback.FeedbackDialog
import com.movieappjc.theme.kColorVulcan
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NavigationDrawerApp(
    drawerState: DrawerState,
    openFavoriteMovie: () -> Unit,
    onClickLanguage: (Locale) -> Unit = {},
    content: @Composable () -> Unit
) {
    val infoDialog = remember { mutableStateOf(false) }
    val feedbackDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val localization = LocalLanguages.current
    val showSubMenu = rememberSaveable { mutableStateOf(false) }
    val onClick: (() -> Unit) = {
        coroutineScope.launch {
            drawerState.close()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(color = kColorVulcan.copy(alpha = 0.8f))
                    .statusBarsPadding()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(40.dp))
                    GlideImage(
                        modifier = Modifier.fillMaxWidth().height(23.dp),
                        model = R.drawable.ic_horizontal_logo,
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    DrawerItem(
                        NavigationDrawerData(
                            label = localization.favoriteMoviesText(),
                            icon = Icons.Filled.Favorite
                        )
                    ) {
                        openFavoriteMovie()
                        onClick()
                    }
                    DrawerItem(
                        NavigationDrawerData(
                            label = localization.languageText(),
                            icon = Icons.Filled.Settings
                        )
                    ) {
                        showSubMenu.value = !showSubMenu.value
                    }
                    AnimatedVisibility(visible = showSubMenu.value) {
                        SubMenuDrawer {
                            onClick()
                            onClickLanguage(it)
                            showSubMenu.value = !showSubMenu.value
                        }
                    }
                    DrawerItem(
                        NavigationDrawerData(
                            label = localization.feedbackText(),
                            icon = Icons.Filled.Edit
                        )
                    ) {
                        onClick()
                        feedbackDialog.value = true
                    }
                    DrawerItem(
                        NavigationDrawerData(
                            label = localization.aboutText(),
                            icon = Icons.Filled.Info
                        )
                    ) {
                        onClick()
                        infoDialog.value = true
                    }
                }
            }
        },
        content = content,
    )
    if (infoDialog.value) {
        AboutDialog(
            title = LocalLanguages.current.aboutText(),
            description = LocalLanguages.current.aboutDescriptionText(),
            onDismiss = {
                infoDialog.value = false
            }
        )
    }

    if (feedbackDialog.value) {
        FeedbackDialog { feedbackDialog.value = false }
    }
}

data class NavigationDrawerData(val label: String, val icon: ImageVector)