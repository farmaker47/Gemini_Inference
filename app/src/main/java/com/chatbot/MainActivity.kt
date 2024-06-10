package com.chatbot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chatbot.presentation.NavigationRoot
import com.chatbot.ui.theme.GeminiInferenceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make your model initializations
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.loading
            }
        }

        setContent {
            GeminiInferenceTheme {
                NavigationRoot()
            }
        }
    }
}
