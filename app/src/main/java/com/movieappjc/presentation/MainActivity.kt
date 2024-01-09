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
import com.movieappjc.common.constants.supportedLocalesNow
import com.movieappjc.common.localization.Localization
import com.movieappjc.domain.repositories.AppRepository
import com.movieappjc.presentation.screen.main.MainScreen
import com.movieappjc.theme.MovieAppJCTheme
import com.movieappjc.theme.kColorVulcan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp{
                MainScreen(onClickLanguage = it)
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable ((Locale) -> Unit) -> Unit){
    MovieAppJCTheme {
        val coroutineScope = rememberCoroutineScope()
        val appRepository: AppRepository = koinInject()
        var locale by remember { mutableStateOf(Locale.getDefault()) }
        LaunchedEffect(true){
            val language = appRepository.getPreferredLanguage().first()
            if (locale != language)
                locale = language
        }
        supportedLocalesNow
        Localization(locale = locale) {
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
