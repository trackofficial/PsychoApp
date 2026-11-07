// * ошибки, которые может выбросить OpenRouterChatApi

package com.example.psychoapp.shared.chat

sealed class ChatException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    class Unauthorized(
        message: String = "Unauthorized",
        cause: Throwable? = null
    ) : ChatException(message, cause)

    class RateLimited(
        message: String = "Rate Limit",
        cause: Throwable? = null
    ) : ChatException(message, cause)

    class ServerError(
        val status: Int?,
        message: String = "Server error $status",
        cause: Throwable? = null
    ) : ChatException(message, cause)

    class Network(
        message: String = "Network error",
        cause: Throwable? = null
    ) : ChatException(message, cause)

    class InvalidResponse(
        message: String = "Invalid response",
        cause: Throwable? = null
    ) : ChatException(message, null)

    class Unknown(
        message: String = "Unknown error",
        cause: Throwable? = null
    ) : ChatException(message, cause)
}