package com.chatbot.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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

    NavHost(
        navController = navController,
        startDestination = Route.MainScreen
    ) {
        composable<Route.MainScreen> {
            MainScreenRoot(
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
            ChatRoot(args.useExistingChat)
        }
    }
}