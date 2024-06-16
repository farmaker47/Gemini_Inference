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
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

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

    val config = generationConfig {
        //temperature = 0.9f
        //topK = 16
        //topP = 0.1f
        maxOutputTokens = 100
        //stopSequences = listOf("red")
    }
    private val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
    private val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = GEMINI_API_KEY,
        generationConfig = config,
        safetySettings = listOf(harassmentSafety, hateSpeechSafety)
    )

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
                    chatState = chatState.copy(
                        messages = messages,
                        isLoading = false
                    )
                    messageManager.createFirstList(messages)
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
            // Log.d("LocalCache", "addMessage: id -> $messageId")
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            messageManager.messagesFlow.collect { messages ->
                // when the messageManager gets a new message, add it to the state
                chatState = chatState.copy(messages = messages)
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
            setInputEnabled(false)

            try {
                val chat = generativeModel.startChat(
                    history = messageManager.convertMessagesToGeminiPrompt()
                )
                val response = chat.sendMessage("Answer based on the conversation. " +
                        "Pretend you are a car french pastry chef")
                response.text?.let { message ->
                    addMessage(message, MODEL_PREFIX)
                }
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
        // Log.d("APP", "Returned asset path: " + outfile.absolutePath)
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
                            /*Log.i(
                                "Utils",
                                "Copied from apk assets folder to ${target.absolutePath}"
                            )*/
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
        // TODO DO NOT UPLOAD TO GITHUB
        private const val GEMINI_API_KEY = ""
    }
}
