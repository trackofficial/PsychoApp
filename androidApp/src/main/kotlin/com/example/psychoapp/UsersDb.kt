package com.example.psychoapp

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [
        User::class,
    ], version = 1
)
abstract class UsersDb : RoomDatabase() {
    abstract fun getUsersDao(): UsersDao
}

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE login = :login AND password_hash = :passwordHash LIMIT 1")
    suspend fun checkUser(login: String, passwordHash: String): User?
}