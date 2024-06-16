package com.chatbot.data

import androidx.compose.runtime.toMutableStateList
import com.chatbot.domain.ChatMessage
import com.chatbot.domain.MODEL_PREFIX
import com.chatbot.domain.MessageManager
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GeminiMessageManager @Inject constructor(
    messages: List<ChatMessage>
) : MessageManager {
    private val START_TURN = "<start_of_turn>"
    private val END_TURN = "<end_of_turn>"

    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()

    // Expose messages as StateFlow
    private val _messagesFlow = MutableStateFlow<List<ChatMessage>>(_messages)
    val messagesFlow: StateFlow<List<ChatMessage>> get() = _messagesFlow

    override val messages: List<ChatMessage>
        get() = _messages
            .map {
                // Remove the prefix and suffix before showing a message in the UI
                it.copy(
                    message = it.message.replace(START_TURN + it.author + "\n", "")
                        .replace(END_TURN, "")
                )
            }.reversed()

    override val fullPrompt: String
        get() = _messages.joinToString(separator = "\n") { it.message }
            //_messages.takeLast(4).joinToString(separator = "\n") { it.message }

    override fun createLoadingMessage(): String {
        val chatMessage = ChatMessage(author = MODEL_PREFIX, isLoading = true)
        _messages.add(chatMessage)
        _messagesFlow.value = _messages.toList() // update the flow with a new list
        return chatMessage.id
    }

    fun createFirstList(listOfMessages: List<ChatMessage>) {
        _messages.clear()
        _messages.addAll(listOfMessages)
    }

    fun convertMessagesToGeminiPrompt(): List<Content> {
        val messages = mutableListOf<Content>()
        this._messages.forEach { chatMessage ->
            if (chatMessage.author == "user") {
                messages.add(content(role = "user") { text(chatMessage.text) })
            } else {
                messages.add(content(role = "model") { text(chatMessage.text) })
            }
        }
        return messages
    }

    fun appendFirstMessage(id: String, text: String) {
        appendMessage(id, "$START_TURN$MODEL_PREFIX\n$text", false)
    }

    override fun appendMessage(id: String, text: String, done: Boolean) {
        val index = _messages.indexOfFirst { it.id == id }
        if (index != -1) {
            val pureText = text
            val newText = if (done) {
                // Append the Suffix when model is done generating the response
                _messages[index].message + text + END_TURN
            } else {
                // Append the text
                _messages[index].message + text
            }
            _messages[index] = _messages[index].copy(text = pureText, message = newText, isLoading = false)
            _messagesFlow.value = _messages.toList() // update the flow with a new list
        }
    }

    override fun addMessage(text: String, author: String): ChatMessage {
        val chatMessage = ChatMessage(
            text = text,
            message = "$START_TURN$author\n$text$END_TURN",
            author = author
        )
        _messages.add(chatMessage)
        _messagesFlow.value = _messages.toList() // update the flow with a new list
        return chatMessage
    }
}