package com.example.psychoapp.data.local.chat

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        ChatMessageEntity::class,
    ], version = 1
)
abstract class ChatMessagesDb : RoomDatabase() {
    abstract fun getChatMessagesDao(): ChatMessageDao
}

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(message: ChatMessageEntity): Long

    @Query(
        "UPDATE ${ChatMessageEntity.TABLE_NAME} " +
                "SET content = :content, status = :status " +
                "WHERE id = :id"
    )
    suspend fun updateContentAndStatus(
        id: Long,
        content: String,
        status: String,
    )

    @Query(
        "SELECT * FROM ${ChatMessageEntity.TABLE_NAME} " +
                "WHERE ${ChatMessageEntity.USER_LOGIN} = :userLogin " +
                "ORDER BY ${ChatMessageEntity.TIMESTAMP_COLUMN} ASC"
    )
    fun observeByUser(userLogin: String): Flow<List<ChatMessageEntity>>

    @Query(
        "SELECT * FROM (" +
                "SELECT * FROM ${ChatMessageEntity.TABLE_NAME} " +
                "WHERE ${ChatMessageEntity.USER_LOGIN} = :userLogin " +
                "ORDER BY ${ChatMessageEntity.TIMESTAMP_COLUMN} DESC " +
                "LIMIT :limit" +
                ") ORDER BY ${ChatMessageEntity.TIMESTAMP_COLUMN} ASC"
    )
    suspend fun getHistoryByUser(
        userLogin: String,
        limit: Int = 50,
    ): List<ChatMessageEntity>

    @Query(
        "DELETE FROM ${ChatMessageEntity.TABLE_NAME} " +
                "WHERE ${ChatMessageEntity.USER_LOGIN} = :userLogin"
    )
    suspend fun clearByUser(userLogin: String)

    @Query(
        "DELETE FROM ${ChatMessageEntity.TABLE_NAME} " +
                "WHERE ${ChatMessageEntity.USER_LOGIN} = :userLogin " +
                "AND id = :id"
    )
    suspend fun deleteMessage(userLogin: String, id: Long)
}