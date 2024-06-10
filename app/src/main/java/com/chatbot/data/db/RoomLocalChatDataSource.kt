package com.chatbot.data.db

import android.database.sqlite.SQLiteFullException
import com.chatbot.domain.LocalChatDataSource
import com.chatbot.domain.ChatMessage
import com.chatbot.domain.MessageId
import com.chatbot.domain.util.DataError
import com.chatbot.domain.util.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomLocalChatDataSource @Inject constructor(
    private val chatDao: ChatDAO
) : LocalChatDataSource {
    override fun getMessages(): Flow<List<ChatMessage>> {
        return chatDao.getMessages()
            .map { chatMessageEntities ->
                chatMessageEntities.map { chatMessageEntity ->
                    chatMessageEntity.toDomainObject()
                }
            }
    }

    override suspend fun addMessage(message: ChatMessage): DataResult<MessageId, DataError.Local> {
        return try {
            val entity = message.toDBObject()
            chatDao.addMessage(entity)
            DataResult.Success(entity.id)
        } catch (e: SQLiteFullException) {
            DataResult.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun addMessages(messages: List<ChatMessage>): DataResult<List<MessageId>, DataError.Local> {
        return try {
            val entities = messages.map { it.toDBObject() }
            chatDao.saveAll(entities)
            DataResult.Success(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            DataResult.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteAllMessages() {
        chatDao.deleteAllMessages()
    }
}