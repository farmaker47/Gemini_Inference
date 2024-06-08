package com.chatbot.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Used for oneTime Events collection, ideally with Channels
 * https://www.youtube.com/watch?v=njchj9d_Lf8
 *
 * Example:
 *
 * (In ViewModel)
 * private val navigationChannel = Channel<NavigationEvent>()
 * val navigationEventsChannelFlow = navigationChannel.receiveAsFlow()
 *
 * (In Composable)
 * observeAsEvent(viewModel.navigationEventsChannelFlow) { event ->
 *     when(event) {
 *         is NavigationEvent.NavigateToProfile -> {
 *             navController.navigate("profile")
 *         }
 *     }
 * }
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}