package com.chatbot.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_message")
data class ChatMessageDB(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val text: String = "",
    val message: String = "",
    val author: String,
)
