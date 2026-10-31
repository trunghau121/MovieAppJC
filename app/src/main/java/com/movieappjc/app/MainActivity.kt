package com.movieappjc.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.core_app.extension.ADp
import com.movieappjc.R
import com.movieappjc.app.common.constants.supportedLocalesNow
import com.movieappjc.app.common.localization.LocalizationApp
import com.movieappjc.app.theme.MovieAppJCTheme
import com.movieappjc.app.theme.kColorVulcan
import com.movieappjc.domain.repositories.AppRepository
import com.movieappjc.presentation.screen.main.MainScreen
import com.movieappjc.steptracker.manager.StepTrackingManager
import com.movieappjc.steptracker.ui.StepPermissionDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appRepository: AppRepository

    lateinit var stepTrackingManager: StepTrackingManager
    override fun onCreate(savedInstanceState: Bundle?) {
        ADp.initializeAdp(this.resources, 390)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        requestedOrientation = if (resources.getBoolean(R.bool.isTablet)) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        stepTrackingManager = StepTrackingManager(this)
        stepTrackingManager.startTracking()

        setContent {
            MyApp(appRepository = appRepository) {
                Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                    MainScreen(onClickLanguage = it)
                }
            }

            val dialogState = stepTrackingManager.dialogState
            if (dialogState != null) {
                StepPermissionDialog(
                    title = stringResource(dialogState.titleRes),
                    message = stringResource(dialogState.messageRes),
                    buttonText = stringResource(dialogState.buttonTextRes),
                    onConfirm = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
        }
    }
}

@Composable
fun MyApp(appRepository: AppRepository, content: @Composable ((Locale) -> Unit) -> Unit) {
    MovieAppJCTheme {
        val coroutineScope = rememberCoroutineScope()
        var locale by remember { mutableStateOf(Locale.getDefault()) }
        LaunchedEffect(true) {
            val language = appRepository.getPreferredLanguage().first()
            if (locale != language)
                locale = language
        }
        supportedLocalesNow
        LocalizationApp(locale = { locale }) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = kColorVulcan
            ) {
                content {
                    if (locale != it) {
                        coroutineScope.launch {
                            appRepository.updateLanguage(it.language)
                            locale = it
                        }
                    }
                }
            }
        }
    }
}
