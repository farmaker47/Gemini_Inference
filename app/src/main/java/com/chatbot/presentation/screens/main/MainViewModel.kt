package com.chatbot.presentation.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatbot.domain.ChatRepository
import com.chatbot.presentation.base.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainViewAction {
    data object StartNewChat: MainViewAction
    data object ContinueExistingChat: MainViewAction
}

sealed interface MainViewEvent {
    data object StartNewChat: MainViewEvent
    data object ContinueLastChat: MainViewEvent
    data class Error(val error: UiText): MainViewEvent
}

data class MainViewState(
    val isLoading: Boolean = false,
    val continueChatButtonEnabled: Boolean = false
)


@HiltViewModel
class MainViewModel @Inject constructor(
    chatRepository: ChatRepository
): ViewModel() {

    var state by mutableStateOf(MainViewState())
        private set

    private val eventChannel = Channel<MainViewEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        // dummy delay to simulate initialization
        // in order to enabled the button for "Continuing on existing chat"
        viewModelScope.launch {
            // state to show loading indicator
            state = state.copy(
                isLoading = true
            )

            chatRepository.getMessages().collect { messages ->
                // Log.d("LocalCache", "initialize: $messages")

                state = state.copy(
                    continueChatButtonEnabled = messages.isNotEmpty(),
                    isLoading = false
                )
            }
        }
    }

    fun onAction(action: MainViewAction) {
        when (action) {
            MainViewAction.ContinueExistingChat -> TODO()
            MainViewAction.StartNewChat -> TODO()
        }
    }
}