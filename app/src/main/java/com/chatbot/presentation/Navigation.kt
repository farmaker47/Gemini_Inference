package com.chatbot.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.chatbot.presentation.base.findActivity
import com.chatbot.presentation.base.scaffold.ApplicationScaffold
import com.chatbot.presentation.base.scaffold.ScaffoldViewModel
import com.chatbot.presentation.screens.chat.ChatRoot
import com.chatbot.presentation.screens.main.MainScreenRoot
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object MainScreen

    @Serializable
    data class ChatScreen(
        val useExistingChat: Boolean
    )
}

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()
    val scaffoldViewModel: ScaffoldViewModel = hiltViewModel(LocalContext.current.findActivity())

    ApplicationScaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.MainScreen
        ) {
            composable<Route.MainScreen> {
                MainScreenRoot(
                    paddingValues = paddingValues,
                    onStartNewChat = {
                        navController.navigate(
                            Route.ChatScreen(useExistingChat = false)
                        )
                    },

                    onContinueExistingChat = {
                        navController.navigate(
                            Route.ChatScreen(useExistingChat = true)
                        )
                    }
                )
            }

            composable<Route.ChatScreen> {
                val args = it.toRoute<Route.ChatScreen>()
                ChatRoot(
                    paddingValues = paddingValues,
                    useExistingChat = args.useExistingChat
                ) {
                    navController.popBackStack()
                }
            }
        }
    }
}