package com.example.psychoapp

import android.content.Context
import androidx.room.Room

object ChatMessagesDbProvider {
    @Volatile private var instance: ChatMessagesDb? = null

    fun get(context: Context): ChatMessagesDb =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                ChatMessagesDb::class.java,
                "chat_messages_database"
            ).build().also { instance = it }
        }
}