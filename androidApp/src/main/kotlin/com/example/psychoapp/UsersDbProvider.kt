package com.example.psychoapp

import android.content.Context
import androidx.room.Room

object UsersDbProvider {
    @Volatile private var instance: UsersDb? = null

    fun get(context: Context): UsersDb =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                UsersDb::class.java,
                "users_database"
            ).build().also { instance = it }
        }
}