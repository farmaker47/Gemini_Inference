package com.chatbot.presentation.screens.chat

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatbot.data.GeminiMessageManager
import com.chatbot.domain.ChatMessage
import com.chatbot.domain.ChatRepository
import com.chatbot.domain.MODEL_PREFIX
import com.chatbot.domain.USER_PREFIX
import com.chatbot.domain.speech2text.IWhisperEngine
import com.chatbot.domain.speech2text.Recorder
import com.chatbot.domain.speech2text.WhisperEngine
import com.chatbot.domain.util.map
import com.chatbot.presentation.base.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.random.Random

sealed interface ChatAction {
    data class OnSendMessage(val text: String) : ChatAction
    data object OnMicPressed : ChatAction
}

data class ChatState(
    val textInput: String = "",
    val textInputEnabled: Boolean = true,
    val isMicPressed: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ChatEvent {
    data class Error(val error: UiText) : ChatEvent
    data object OnSendMessage : ChatEvent
    data class OnMessageReceived(val message: String) : ChatEvent
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageManager: GeminiMessageManager,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var chatState by mutableStateOf(ChatState())
        private set

    private val eventChannel = Channel<ChatEvent>()
    val events = eventChannel.receiveAsFlow()

    private lateinit var whisperEngine: IWhisperEngine
    private lateinit var recorder: Recorder
    private lateinit var outputFileWav: File

    init {
        observeMessages()
    }

    fun initialize(useExistingChat: Boolean) {
        viewModelScope.launch {
            chatState = chatState.copy(
                isLoading = true
            )
            if (useExistingChat) {
                chatRepository.getMessages().collect { messages ->
                    // Log.d("LocalCache", "initialize: $messages")
                    chatState = chatState.copy(
                        messages = messages,
                        isLoading = false
                    )
                }
            } else {
                chatRepository.deleteAllMessages()
                //addDummyMessages()
            }
            chatState = chatState.copy(
                isLoading = false
            )
        }
    }

    fun initializeSpeechModel(context: Context) {
        chatState = chatState.copy(
            isLoading = true
        )
        viewModelScope.launch {
            outputFileWav = File(context.filesDir, RECORDING_FILE_WAV)
            whisperEngine = WhisperEngine(context)
            recorder = Recorder(context)
            withContext(Dispatchers.IO) {
                copyAssets(
                    context,
                    arrayOf("filters_vocab_en.bin", "whisper_tiny_english_14.tflite")
                )
                whisperEngine.initialize(MODEL_PATH, getFilePath(VOCAB_PATH, context), false)
                recorder.setFilePath(getFilePath(RECORDING_FILE_WAV, context))
            }
            chatState = chatState.copy(
                isLoading = false
            )
        }
    }

    private suspend fun addMessage(messageText: String, author: String = USER_PREFIX) {
        val message = messageManager.addMessage(messageText, author)
        val result = chatRepository.addMessage(message)
        result.map { messageId ->
            Log.d("LocalCache", "addMessage: id -> $messageId")
        }
    }

    private fun addDummyMessages() {
        val fromMs = 500L
        val toMs = 1500L

        // demo of adding messages through the messageManager that reflect back on viewModel's "state"
        viewModelScope.launch(Dispatchers.IO) {
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Hey Gemini, have you ever thought about how AI will change the world?",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Hi! Absolutely, AI has the potential to revolutionize many aspects of our lives.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Yeah, I've read about that. What do you think will be the most significant change?",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "One major change could be in healthcare, with AI improving diagnostics and personalized treatment plans.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "That's true. Imagine getting a diagnosis faster and more accurately than ever before.",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Exactly. It can also assist doctors in managing patient care more efficiently.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage("What about in our daily lives? How will AI impact that?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "AI will likely automate routine tasks, making our daily lives more convenient and giving us more free time.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Like smart homes adjusting lighting and temperature based on our preferences?",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Exactly! Smart homes will become more intuitive and responsive to our needs.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "I'm also excited about how AI can improve transportation with self-driving cars.",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Self-driving cars will make commuting safer and more efficient, reducing accidents caused by human error.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage("Do you think AI will take over many jobs though?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "AI will certainly change the job landscape, but it will also create new opportunities in tech and other fields.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "That's reassuring. So, there will be a shift rather than a loss of jobs.",
                USER_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Exactly. The key is for people to adapt and acquire new skills to stay relevant.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage("What about education? How can AI help there?", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "AI can personalize learning experiences, providing tailored resources and feedback to students.",
                MODEL_PREFIX
            )
            delay(Random.nextLong(fromMs, toMs))
            addMessage("That would make learning much more efficient and engaging.", USER_PREFIX)
            delay(Random.nextLong(fromMs, toMs))
            addMessage(
                "Definitely. AI has the potential to make education more accessible and effective for everyone.",
                MODEL_PREFIX
            )
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageManager.messagesFlow.collect { messages ->
                // when the messageManager gets a new message, add it to the state
                chatState = chatState.copy(messages = messages)
                Log.d("MESSAGE ADDED", "observeMessages: ${chatState.messages.map { it.author }}")
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnSendMessage -> {
                // Handle send message action
                sendMessage(action.text)
            }

            is ChatAction.OnMicPressed -> {
                if (!chatState.isMicPressed) {
                    startRecordingWav()
                    chatState = chatState.copy(isMicPressed = true)
                } else {
                    stopRecordingWav()
                    chatState = chatState.copy(isMicPressed = false)
                }
            }
        }
    }

    private fun sendMessage(userMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addMessage(userMessage, USER_PREFIX)
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
        chatState = chatState.copy(
            textInputEnabled = isEnabled
        )
    }

    private fun startRecordingWav() {
        recorder.start()
    }

    private fun stopRecordingWav() {
        recorder.stop()

        try {
            viewModelScope.launch(Dispatchers.Default) {
                // Offline speech to text
                val transcribedText = whisperEngine.transcribeFile(outputFileWav.absolutePath)
                // Log.v( "APP" , "Transcribed text: ${transcribedText}" )
                onAction(ChatAction.OnSendMessage(transcribedText))
                chatState = chatState.copy(textInput = transcribedText)
            }
        } catch (e: RuntimeException) {
            Log.e("APP", e.toString())
        } catch (e: IllegalStateException) {
            Log.e("APP", e.toString())
        }
    }

    // Returns file path for vocab .bin file
    private fun getFilePath(assetName: String, context: Context): String? {
        val outfile = File(context.filesDir, assetName)
        if (!outfile.exists()) {
            Log.d("APP", "File not found - " + outfile.absolutePath)
        }
        Log.d("APP", "Returned asset path: " + outfile.absolutePath)
        return outfile.absolutePath
    }

    private fun copyAssets(context: Context, listFiles: Array<String>): String {
        val extFolder = context.filesDir.absolutePath
        try {
            context.assets.list("")
                ?.filter { listFiles.contains(it) }
                ?.filter { !File(extFolder, it).exists() }
                ?.forEach {
                    val target = File(extFolder, it)
                    context.assets.open(it).use { input ->
                        FileOutputStream(target).use { output ->
                            input.copyTo(output)
                            Log.i(
                                "Utils",
                                "Copied from apk assets folder to ${target.absolutePath}"
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("Utils", "asset copy failed", e)
        }
        return extFolder
    }

    companion object {
        private const val MODEL_PATH = "whisper_tiny_english_14.tflite"
        private const val VOCAB_PATH = "filters_vocab_en.bin"
        private const val RECORDING_FILE_WAV = "recording.wav"
    }
}
