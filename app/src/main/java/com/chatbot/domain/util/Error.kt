package com.chatbot.domain.util

sealed interface DataError {

    enum class Local: DataError {
        DISK_FULL
    }
}