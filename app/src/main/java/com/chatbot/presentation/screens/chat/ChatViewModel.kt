package com.chatbot.presentation.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatbot.data.GeminiMessageManager
import com.chatbot.domain.ChatMessage
import com.chatbot.domain.MODEL_PREFIX
import com.chatbot.domain.USER_PREFIX
import com.chatbot.presentation.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

sealed interface ChatAction {
    data object OnSendMessage: ChatAction
    data object OnMicPressed: ChatAction
}

data class ChatState(
    val textInput: String = "",
    val textInputEnabled: Boolean = true,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ChatEvent {
    data class Error(val error: UiText): ChatEvent
    data object OnSendMessage: ChatEvent
    data class OnMessageReceived(val message: String): ChatEvent
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageManager: GeminiMessageManager
) : ViewModel() {

    var state by mutableStateOf(ChatState())
        private set

    private val eventChannel = Channel<ChatEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        observeMessages()

        val fromMs: Long = 500L
        val toMs: Long = 1500L

        // demo of adding messages through the messageManager that reflect back on viewModel's "state"
        viewModelScope.launch {
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Hey, how are you doing today?", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("I'm doing well, thanks! How about you?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("I'm great. I've been working on a new project.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("It's a chatbot that can assist with coding tasks. The progress has been steady so far.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("That sounds really interesting! What kind of coding tasks can it help with?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("It can help with debugging, providing code snippets, and even explaining complex concepts.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("I'm hoping it will make coding a bit easier for beginners.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("That's amazing. How do you plan to test it?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("I'm going to run a series of test cases and get feedback from some beta users.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Sounds like a solid plan.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Yeah, I'm excited to see how it performs.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("By the way, have you finished those reports you were working on?", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Almost. Just need to wrap up a few more details.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Got it. Reports can be tedious, but they're important.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Absolutely. It's part of the job.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Anyway, I should get back to work on the chatbot.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Sure, let me know if you need any help.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Will do. Talk to you later!", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Bye!", USER_PREFIX)

        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageManager.messagesFlow.collect { messages ->
                // when the messageManager gets a new message, add it to the state
                state = state.copy(messages = messages)
                Log.d("MESSAGE ADDED", "observeMessages: ${state.messages.map { it.author }}")
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnSendMessage -> {
                // Handle send message action
                Log.d("IOANNIS", "onAction -> OnSendMessage: ")
                sendMessage(state.textInput)
            }
            is ChatAction.OnMicPressed -> {
                Log.d("IOANNIS", "onAction -> OnMicPressed: ")
                // Handle mic pressed action
            }
        }
    }

    fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageManager.addMessage(userMessage, USER_PREFIX)
            // var currentMessageId: String? = _uiState.value.createLoadingMessage()
            setInputEnabled(false)
            try {
                val fullPrompt = messageManager.fullPrompt
                /*inferenceModel.generateResponseAsync(fullPrompt)
                inferenceModel.partialResults
                    .collectIndexed { index, (partialResult, done) ->
                        currentMessageId?.let {
                            if (index == 0) {
                                _uiState.value.appendFirstMessage(it, partialResult)
                            } else {
                                _uiState.value.appendMessage(it, partialResult, done)
                            }
                            if (done) {
                                currentMessageId = null
                                // Re-enable text input
                                setInputEnabled(true)
                            }
                        }
                    }*/
            } catch (e: Exception) {
                messageManager.addMessage(e.localizedMessage ?: "Unknown Error", MODEL_PREFIX)
                setInputEnabled(true)
            }
        }
    }

    private fun setInputEnabled(isEnabled: Boolean) {
        state = state.copy(
            textInputEnabled = isEnabled
        )
    }
}
