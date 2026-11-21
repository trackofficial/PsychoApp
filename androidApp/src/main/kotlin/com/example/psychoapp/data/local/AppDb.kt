package com.example.psychoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.psychoapp.data.local.users.dao.UsersDao
import com.example.psychoapp.data.local.users.model.User

@Database(
    entities = [
        User::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDb : RoomDatabase() {
    abstract fun usersDao(): UsersDao
}