package com.example.psychoapp.data.local

import android.content.Context
import androidx.room.Room


object AppDbProvider {
    @Volatile private var INSTANCE: AppDb? = null

    fun getDb(ctx: Context): AppDb =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                ctx.applicationContext,
                AppDb::class.java,
                "db.db"
            ).build().also { INSTANCE = it }
        }

    fun closeDb() {
        synchronized(this) {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}