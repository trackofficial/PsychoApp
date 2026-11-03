package com.example.psychoapp

import java.time.Instant

data class AiMessage(
    val id: Long = 0L,
    val userLogin: String,
    val role: String,
    val content: String,
    val timestamp: Instant
)