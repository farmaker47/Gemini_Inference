package com.chatbot.data

import androidx.compose.runtime.toMutableStateList
import com.chatbot.domain.ChatMessage
import com.chatbot.domain.MODEL_PREFIX
import com.chatbot.domain.MessageManager
import javax.inject.Inject

/**
 * A sample implementation of [MessageManager] that can be used with any model.
 */
class ChatMessageManager @Inject constructor(
    messages: List<ChatMessage>
) : MessageManager {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    override val messages: List<ChatMessage> = _messages.reversed()

    // Prompt the model with the current chat history
    override val fullPrompt: String
        get() = _messages.joinToString(separator = "\n") { it.message }

    override fun createLoadingMessage(): String {
        val chatMessage = ChatMessage(author = MODEL_PREFIX, isLoading = true)
        _messages.add(chatMessage)
        return chatMessage.id
    }

    override fun appendMessage(id: String, text: String, done: Boolean) {
        val index = _messages.indexOfFirst { it.id == id }
        if (index != -1) {
            val newText = _messages[index].message + text
            _messages[index] = _messages[index].copy(message = newText, isLoading = false)
        }
    }

    override fun addMessage(text: String, author: String): ChatMessage {
        val chatMessage = ChatMessage(
            message = text,
            author = author
        )
        _messages.add(chatMessage)
        return chatMessage
    }
}
