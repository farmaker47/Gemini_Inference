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
    data class OnSendMessage(val text: String): ChatAction
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
            messageManager.addMessage("Hey Gemini, have you ever thought about how AI will change the world?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Hi! Absolutely, AI has the potential to revolutionize many aspects of our lives.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Yeah, I've read about that. What do you think will be the most significant change?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("One major change could be in healthcare, with AI improving diagnostics and personalized treatment plans.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("That's true. Imagine getting a diagnosis faster and more accurately than ever before.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Exactly. It can also assist doctors in managing patient care more efficiently.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("What about in our daily lives? How will AI impact that?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("AI will likely automate routine tasks, making our daily lives more convenient and giving us more free time.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Like smart homes adjusting lighting and temperature based on our preferences?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Exactly! Smart homes will become more intuitive and responsive to our needs.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("I'm also excited about how AI can improve transportation with self-driving cars.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Self-driving cars will make commuting safer and more efficient, reducing accidents caused by human error.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Do you think AI will take over many jobs though?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("AI will certainly change the job landscape, but it will also create new opportunities in tech and other fields.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("That's reassuring. So, there will be a shift rather than a loss of jobs.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Exactly. The key is for people to adapt and acquire new skills to stay relevant.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("What about education? How can AI help there?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("AI can personalize learning experiences, providing tailored resources and feedback to students.", MODEL_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("That would make learning much more efficient and engaging.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            messageManager.addMessage("Definitely. AI has the potential to make education more accessible and effective for everyone.", MODEL_PREFIX)
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
                Log.d("IOANNIS", "onAction -> OnSendMessage: ${action.text}")
                sendMessage(action.text)
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
