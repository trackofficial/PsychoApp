package com.example.psychoapp

import android.content.Context
import androidx.room.Room

object NotesDbProvider {
    @Volatile private var instance: NotesDb? = null

    fun get(context: Context): NotesDb =
        instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                NotesDb::class.java,
                "notes_database"
            ).build().also { instance = it }
        }
}