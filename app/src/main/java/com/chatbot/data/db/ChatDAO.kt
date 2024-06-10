package com.chatbot.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(message: List<ChatMessageDB>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: ChatMessageDB)

    @Query(value = "DELETE FROM chat_message")
    suspend fun deleteAllMessages()

    @Query(value = "SELECT * FROM chat_message ORDER BY timeStamp")
    fun getMessages(): Flow<List<ChatMessageDB>>
}