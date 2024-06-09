package com.chatbot.data.db

import com.chatbot.domain.ChatMessage

fun ChatMessageDB.toDomainObject(): ChatMessage {
    return ChatMessage(
        id = this.id,
        text = this.text,
        message = this.message,
        author = this.author,
        timestamp = this.timestamp
    )
}

fun ChatMessage.toDBObject(): ChatMessageDB {
    return ChatMessageDB(
        id = this.id,
        text = this.text,
        message = this.message,
        author = this.author,
        timestamp = this.timestamp
    )
}