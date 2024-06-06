package com.chatbot.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chatbot.presentation.screens.chat.ChatRoute
import com.chatbot.presentation.screens.loading.LoadingRoute
import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    object SplashScreen

    @Serializable
    object ChatScreen
}

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.SplashScreen
    ) {
        composable<Route.SplashScreen> {
            LoadingRoute(
                onModelLoaded = {
                    navController.navigate(Route.ChatScreen) {
                        popUpTo(Route.SplashScreen) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Route.ChatScreen> {
            ChatRoute()
        }
    }
}