package com.chatbot.di

import com.chatbot.domain.ChatMessage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatMessages(): List<ChatMessage> {
        return emptyList()
    }
}