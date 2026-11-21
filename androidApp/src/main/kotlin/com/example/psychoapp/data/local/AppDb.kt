package com.example.psychoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.psychoapp.data.local.chat.dao.ChatMessagesDao
import com.example.psychoapp.data.local.chat.model.ChatMessageEntity
import com.example.psychoapp.data.local.notes.dao.NotesDao
import com.example.psychoapp.data.local.notes.model.Note
import com.example.psychoapp.data.local.users.dao.UsersDao
import com.example.psychoapp.data.local.users.model.User

@Database(
    entities = [
        User::class,
        Note::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDb : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun notesDao(): NotesDao
    abstract fun chatMessagesDao(): ChatMessagesDao
}