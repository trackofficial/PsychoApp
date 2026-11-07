package com.example.psychoapp.data.local.users

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = User.TABLE_NAME,
    indices = [Index(value = ["login"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COLUMN)
    val id: Long = 0,
    @ColumnInfo(name = LOGIN_COLUMN)
    val login: String,
    @ColumnInfo(name = PASSWORD_HASH_COLUMN)
    val password: String
) {
    companion object {
        const val TABLE_NAME = "users"

        const val ID_COLUMN = "id"
        const val LOGIN_COLUMN = "login"
        const val PASSWORD_HASH_COLUMN = "password_hash"
    }
}