package com.example.psychoapp.data.local.users.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.psychoapp.data.local.users.model.User

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE login = :login LIMIT 1")
    suspend fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE login = :login AND password_hash = :passwordHash LIMIT 1")
    suspend fun checkUser(login: String, passwordHash: String): User?
}