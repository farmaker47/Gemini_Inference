package com.chatbot.domain

import java.util.UUID

/**
 * Used to represent a ChatMessage
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val timestamp: Long = 0,
    val message: String = "",
    val author: String,
    val isLoading: Boolean = false
) {
    val isFromUser: Boolean
        get() = author == USER_PREFIX

//    val messageWithoutPrefixPostfix: String
//        get() = message.removePrefix(messagePrefix).removeSuffix(messagePostfix)
}
