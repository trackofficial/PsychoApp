package com.example.psychoapp.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal object ChatJson {
    val default: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }
}

fun createChatHttpClient(): HttpClient = HttpClient {
    expectSuccess = false

    install(ContentNegotiation) {
        json(ChatJson.default)
    }
    install(SSE)
    install(HttpTimeout) {
        requestTimeoutMillis = 120_000L
        connectTimeoutMillis = 15_000L
        socketTimeoutMillis = 120_000L
    }
}