package com.movieappjc.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.core_app.utils.StableHolder
import com.movieappjc.common.constants.supportedLocalesNow
import com.movieappjc.common.localization.Localization
import com.movieappjc.domain.repositories.AppRepository
import com.movieappjc.presentation.screen.main.MainScreen
import com.movieappjc.theme.MovieAppJCTheme
import com.movieappjc.theme.kColorVulcan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appRepository: AppRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            MyApp(appRepository = appRepository){
                MainScreen(onClickLanguage = it)
            }
        }
    }
}

@Composable
fun MyApp(appRepository: AppRepository, content: @Composable ((Locale) -> Unit) -> Unit){
    MovieAppJCTheme {
        val coroutineScope = rememberCoroutineScope()
        var locale by remember { mutableStateOf(Locale.getDefault()) }
        LaunchedEffect(true){
            val language = appRepository.getPreferredLanguage().first()
            if (locale != language)
                locale = language
        }
        supportedLocalesNow
        Localization(locale = StableHolder(locale)) {
            Surface(modifier = Modifier.fillMaxSize().navigationBarsPadding(), color = kColorVulcan) {
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
