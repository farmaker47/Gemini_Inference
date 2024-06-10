package com.chatbot.data

import com.chatbot.domain.ChatMessage
import com.chatbot.domain.ChatRepository
import com.chatbot.domain.LocalChatDataSource
import com.chatbot.domain.MessageId
import com.chatbot.domain.util.DataError
import com.chatbot.domain.util.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val dataSource: LocalChatDataSource
): ChatRepository {

    override fun getMessages(): Flow<List<ChatMessage>> {
        return dataSource.getMessages()
    }

    override suspend fun addMessage(message: ChatMessage): DataResult<MessageId, DataError.Local> {
        return dataSource.addMessage(message)
    }

    override suspend fun addMessages(messages: List<ChatMessage>): DataResult<List<MessageId>, DataError.Local> {
        return dataSource.addMessages(messages)
    }

    override suspend fun deleteAllMessages() {
        dataSource.deleteAllMessages()
    }
}