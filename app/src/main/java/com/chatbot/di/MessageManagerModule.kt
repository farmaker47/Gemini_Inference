package com.chatbot.di

import com.chatbot.data.GeminiMessageManager
import com.chatbot.domain.MessageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessageManagerBinds {

    @Binds
    @Singleton
    abstract fun bindMessageManager(impl: GeminiMessageManager): MessageManager
}