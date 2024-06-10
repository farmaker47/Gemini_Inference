package com.chatbot.domain

import com.chatbot.domain.util.DataError
import com.chatbot.domain.util.DataResult
import kotlinx.coroutines.flow.Flow

typealias MessageId = String

interface LocalChatDataSource {

    fun getMessages(): Flow<List<ChatMessage>>

    suspend fun addMessage(message: ChatMessage): DataResult<MessageId, DataError.Local>

    suspend fun addMessages(messages: List<ChatMessage>): DataResult<List<MessageId>, DataError.Local>

    suspend fun deleteAllMessages()

}