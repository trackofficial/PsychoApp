package com.example.psychoapp.data.remote

import com.example.psychoapp.BuildConfig
import com.example.psychoapp.shared.chat.ChatApi
import com.example.psychoapp.shared.chat.OpenRouterChatApi
import com.example.psychoapp.shared.network.createChatHttpClient

object ChatApiProvider {
    private const val BASE_URL = "https://api.zubriq.by/v1"
    const val DEFAULT_MODEL = "zubr-flash"
    private val httpClient by lazy { createChatHttpClient() }

    val api: ChatApi by lazy {
        OpenRouterChatApi(
            httpClient = httpClient,
            baseUrl = BASE_URL,
            authToken = BuildConfig.AI_API_KEY
        )
    }
}