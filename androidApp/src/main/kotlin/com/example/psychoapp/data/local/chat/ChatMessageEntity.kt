package com.example.psychoapp.data.local.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = ChatMessageEntity.TABLE_NAME,
    indices = [
        Index(ChatMessageEntity.USER_LOGIN),
        Index(value = [ChatMessageEntity.USER_LOGIN, ChatMessageEntity.TIMESTAMP_COLUMN])
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_COLUMN)
    val id: Long = 0L,
    @ColumnInfo(name = USER_LOGIN)
    val userLogin: String,
    @ColumnInfo(name = ROLE_COLUMN)
    val role: String,
    @ColumnInfo(name = CONTENT_COLUMN)
    val content: String,
    @ColumnInfo(name = TIMESTAMP_COLUMN)
    val timestamp: Long,
    @ColumnInfo(name = STATUS_COLUMN)
    val status: String
) {
    companion object {
        const val TABLE_NAME = "chat_messages"

        const val ID_COLUMN = "id"
        const val USER_LOGIN = "user_login"
        const val ROLE_COLUMN = "role"
        const val CONTENT_COLUMN = "content"
        const val TIMESTAMP_COLUMN = "timestamp"
        const val STATUS_COLUMN = "status"

        const val STATUS_STREAMING = "STREAMING"
        const val STATUS_DONE = "DONE"
        const val STATUS_ERROR = "ERROR"
    }
}