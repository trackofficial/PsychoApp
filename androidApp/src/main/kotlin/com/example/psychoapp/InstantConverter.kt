package com.example.psychoapp

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {
    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(millisSinceEpoch: Long?): Instant? {
        return millisSinceEpoch?.let { Instant.ofEpochMilli(it) }
    }
}