package com.chatbot.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatMessageDB::class], version = 1, exportSchema = false)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun articlesDao(): ChatDAO
}