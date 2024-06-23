# Gemini + Whisper and Context-Caching in Android

> An Android app that uses Room to persist chat conversions and provide them as context to Google's Gemini Cloud API. It also uses OpenAI's Whisper to transcribe voice messages.

## Setup

1. Clone the `master` branch,

```bash
$> git clone --depth=1 https://github.com/farmaker47/Gemini_Inference
```

2. [Get an API key from Google AI Studio](https://ai.google.dev/gemini-api/docs/api-key) to use the Gemini API. Copy
   the key and paste it in `app/src/main/java/com/chatbot/presentation/screens/chat/ChatViewModel.kt` file,

```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageManager: GeminiMessageManager,
    private val chatRepository: ChatRepository
) : ViewModel() {

    // ...

    companion object {
        private const val MODEL_PATH = "whisper_tiny_english_14.tflite"
        private const val VOCAB_PATH = "filters_vocab_en.bin"
        private const val RECORDING_FILE_WAV = "recording.wav"
        // TODO: Add the Gemini API key here
        // TODO DO NOT UPLOAD TO GITHUB OR ANYWHERE ONLINE ALSO DO NOT SHIP IT WITH THE APP.
        private const val GEMINI_API_KEY = ""
    }
}
```

Perform a Gradle sync, and run the application.

## Discussion

### What is context-caching and how is it implemented in the app

Context-caching comprises of methods that allow persisting conversations between the user (human) and the LLM to provide personalized responses by referring to the past conversations as context. They provide a long-term memory to the LLM which helps it maintain relevance and flow in the conversation. 

The Gemini API allows developers to pass previous messages, that were exchanged between the LLM and the user, as an argument. The app uses Room to persist messages across app-restarts and passes these messages to the Gemini API each message a new user query is initiated.

```kotlin
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = ApiKeyString
)

// Passing previous messages as `history` or context
val chat = generativeModel.startChat(
    history = listOf(
        content(role = "user") { text("Hello, I have 2 dogs in my house.") },
        content(role = "model") { text("Great to meet you. What would you like to know?") }
    )
)

chat.sendMessage("How many paws are in my house?")
```

Here's the database entity used to store a single message in Room,

```kotlin
@Entity(tableName = "chat_message")
data class ChatMessageDB(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val text: String = "",
    val message: String = "",
    val author: String,
)
```

### What is OpenAI Whisper and how is it integrated in the app

[Whisper](https://github.com/openai/whisper) is a general-purpose speech recognition model trained on a diverse dataset of audio and serves as a multitasking model capable of multilingual speech recognition, speech translation, and language identification. The original implementation is written in Python.

[whisper.cpp](https://github.com/ggerganov/whisper.cpp) is a popular C++ implementation of Whisper built with the [ggml](https://github.com/ggerganov/ggml) framework by [Georgi Gerganov](https://github.com/ggerganov). Extending it, [nyadla-sys/whisper.tflite](https://github.com/nyadla-sys/whisper.tflite) and [farmaker47/Talk_and_execute](https://github.com/farmaker47/Talk_and_execute/tree/local_llm) provide inference on Whisper with [TensorFlow Lite](https://www.tensorflow.org/lite). The C++ source files in `app/src/main/cpp` provide JNI functions to derive the [Mel Spectrogram](https://medium.com/analytics-vidhya/understanding-the-mel-spectrogram-fca2afa2ce53) from the audio signal which acts as the input to the TFLite Whisper model.

## Tools

- [Gemini Cloud API](https://ai.google.dev/gemini-api) - LLM that provides responses to user messages
- [Android Room](https://developer.android.com/jetpack/androidx/releases/room) - Primary SQLite database for Android applications
- [Compose](https://developer.android.com/develop/ui/compose) - Declarative UI framework for Android applications
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp) - C++ implementation of OpenAI's speech-to-text model
 
## Contributors

* [George Soloupis](https://github.com/farmaker47)
* [Ioannis Anifantakis](https://github.com/ianyfantakis)
* [Shubham Panchal](https://github.com/shubham0204)
