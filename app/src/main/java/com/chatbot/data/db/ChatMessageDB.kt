package com.chatbot.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessageDB(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timeStamp: Long = System.currentTimeMillis(),
    val text: String = "",
    val message: String = "",
    val author: String,
)
