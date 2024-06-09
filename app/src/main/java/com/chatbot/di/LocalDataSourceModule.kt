package com.chatbot.di

import com.chatbot.data.db.RoomLocalChatDataSource
import com.chatbot.domain.LocalChatDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalChatDatasourceBinds {

    @Binds
    @Singleton
    abstract fun bindLocalChatDatasource(impl: RoomLocalChatDataSource): LocalChatDataSource
}