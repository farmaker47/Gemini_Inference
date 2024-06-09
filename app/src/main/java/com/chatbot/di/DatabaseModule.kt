package com.chatbot.di

import android.content.Context
import androidx.room.Room
import com.chatbot.data.db.ChatDAO
import com.chatbot.data.db.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideArticlesDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "articles_database"
        ).build()
    }

    @Provides
    fun provideArticlesDao(articlesDatabase: ChatDatabase): ChatDAO {
        return articlesDatabase.articlesDao()
    }
}