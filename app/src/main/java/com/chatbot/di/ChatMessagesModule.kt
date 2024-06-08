package com.chatbot.di

import com.chatbot.data.GeminiMessageManager
import com.chatbot.domain.ChatMessage
import com.chatbot.presentation.screens.chat.ChatViewModel
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatMessages(): List<ChatMessage> {
        return emptyList()
    }
}