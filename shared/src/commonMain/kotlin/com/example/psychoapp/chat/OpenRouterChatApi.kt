package com.example.psychoapp.chat

import com.example.psychoapp.model.ChatMessage
import com.example.psychoapp.model.ChatRequest
import com.example.psychoapp.model.ChatResponse
import com.example.psychoapp.model.ChatStreamChunk
import com.example.psychoapp.network.ChatJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.sse.SSEClientException
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString

class OpenRouterChatApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val authToken: String
) : ChatApi {

    override fun streamReply(model: String, messages: List<ChatMessage>): Flow<ChatStreamChunk> =
        flow {
            try {
                httpClient.sse(
                    request = {
                        url("$baseUrl/chat/completions")
                        method = HttpMethod.Post

                        headers {
                            header("Authorization", "Bearer $authToken")
                            append("Accept", "text/event-stream")
                            header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        }

                        contentType(ContentType.Application.Json)
                        setBody(ChatRequest(model = model, messages = messages, stream = true))
                    }
                ) {
                    incoming.collect { event ->
                        val data = event.data ?: return@collect
                        if (data == "[DONE]") return@collect

                        val chunk = try {
                            ChatJson.default.decodeFromString<ChatStreamChunk>(data)
                        } catch (e: SerializationException) {
                            throw ChatException.InvalidResponse("Не удалось обработать SSE-chunk", e)
                        }

                        emit(chunk)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ChatException) {
                throw e
            } catch (e: SSEClientException) {
                throw mapSseException(e)
            } catch (e: HttpRequestTimeoutException) {
                throw ChatException.Network("Таймаут соединения с сервером", e)
            } catch (e: SerializationException) {
                throw ChatException.InvalidResponse("Некорректный JSON в ответе", e)
            } catch (e: Throwable) {
                throw ChatException.Unknown("Ошибка при стриминге ответа", e)
            }
        }

    override suspend fun complete(model: String, messages: List<ChatMessage>): ChatResponse {
        try {
            val response = httpClient.post("$baseUrl/chat/completions") {
                headers {
                    header("Authorization", "Bearer $authToken")
                    header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                }

                contentType(ContentType.Application.Json)
                setBody(ChatRequest(model = model, messages = messages, stream = false))
            }

            if (!response.status.isSuccess()) {
                throw mapHttpStatus(response.status.value, response.bodyAsText())
            }

            return response.body()
        } catch (e: CancellationException) {
            throw e
        } catch (e: ChatException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            throw ChatException.Network("Таймаут соединения с сервером", e)
        } catch (e: ResponseException) {
            throw mapHttpStatus(e.response.status.value, e.response.bodyAsText())
        } catch (e: SerializationException) {
            throw ChatException.InvalidResponse("Некорректный JSON в ответе", e)
        } catch (e: Throwable) {
            throw ChatException.Unknown("Ошибка при получении ответа", e)
        }
    }

    private fun mapSseException(e: SSEClientException): ChatException {
        val status: Int? = e.response?.status?.value

        return when {
            status == 401 || status == 403 ->
                ChatException.Unauthorized("Доступ к ИИ запрещён (HTTP $status)", e)
            status == 429 ->
                ChatException.RateLimited("Превышен лимит запросов", e)
            status != null && status in 500..599 ->
                ChatException.ServerError(status, "Ошибка сервера $status", e)
            else ->
                ChatException.InvalidResponse(
                    "SSE-соединение не установлено (HTTP ${status ?: "?"})",
                    e,
                )
        }
    }

    private fun mapHttpStatus(status: Int, body: String): ChatException =
        when (status) {
            401, 403 ->
                ChatException.Unauthorized("$status: $body", null)

            429 ->
                ChatException.RateLimited("Превышен лимит запросов (HTTP 429)", null)

            in 500..599 ->
                ChatException.ServerError(status, "Ошибка сервера $status", null)

            else -> ChatException.InvalidResponse("Неожиданный статус HTTP $status: $body", null)
        }
}