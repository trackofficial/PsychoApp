package com.example.psychoapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.psychoapp.domain.chat.CrisisDetector
import com.example.psychoapp.domain.chat.SystemPrompt
import com.example.psychoapp.shared.chat.ChatApi
import com.example.psychoapp.shared.chat.ChatException
import com.example.psychoapp.shared.chat.ChatHistoryRepository
import com.example.psychoapp.shared.chat.ChatMessageUi
import com.example.psychoapp.shared.chat.ChatStatus
import com.example.psychoapp.shared.model.ChatMessage
import com.example.psychoapp.shared.model.ChatRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val isStreaming: Boolean = false,
    val errorMessage: String? = null,
)

class ChatViewModel(
    private val repository: ChatHistoryRepository,
    private val chatApi: ChatApi,
    private val userLogin: String,
    private val model: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var streamJob: Job? = null
    private var currentAssistantId: Long = -1L
    private val accumulatedText = StringBuilder()

    init {
        viewModelScope.launch {
            repository.observeByUser(userLogin).collect { roomMessages ->
                _uiState.update { current ->
                    val merged = roomMessages.map { roomMessage ->
                        if (roomMessage.id == currentAssistantId && streamJob?.isActive == true) {
                            roomMessage.copy(
                                content = accumulatedText.toString(),
                                status = ChatStatus.STREAMING,
                            )
                        } else {
                            roomMessage
                        }
                    }
                    current.copy(messages = merged)
                }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || streamJob?.isActive == true) return
        val trimmed = text.trim()

        streamJob = viewModelScope.launch {
            try {
                accumulatedText.clear()
                currentAssistantId = -1L

                repository.saveMessage(
                    userLogin = userLogin,
                    role = ChatRole.USER,
                    content = trimmed,
                    status = ChatStatus.DONE,
                )

                val history = repository.getHistoryForApi(userLogin, limit = 50)

                val isCrisis = CrisisDetector.isCrisis(trimmed)
                val systemPrompt = SystemPrompt.build(isCrisis = isCrisis)

                val messagesForApi = buildList {
                    add(ChatMessage(role = ChatRole.SYSTEM, content = systemPrompt))
                    addAll(history)
                }

                val assistantId = repository.saveMessage(
                    userLogin = userLogin,
                    role = ChatRole.ASSISTANT,
                    content = "",
                    status = ChatStatus.STREAMING,
                )
                currentAssistantId = assistantId

                _uiState.update { it.copy(isStreaming = true, errorMessage = null) }

                chatApi.streamReply(
                    model = model,
                    messages = messagesForApi,
                ).collect { chunk ->
                    val delta = chunk.choices.firstOrNull()?.delta?.content
                    if (delta.isNullOrEmpty()) return@collect
                    accumulatedText.append(delta)
                    _uiState.update { current ->
                        current.copy(
                            messages = current.messages.map { msg ->
                                if (msg.id == assistantId) {
                                    msg.copy(content = accumulatedText.toString())
                                } else {
                                    msg
                                }
                            },
                        )
                    }
                }

                repository.updateMessage(
                    id = assistantId,
                    content = accumulatedText.toString(),
                    status = ChatStatus.DONE,
                )
            } catch (e: CancellationException) {
                if (currentAssistantId > 0) {
                    repository.updateMessage(
                        id = currentAssistantId,
                        content = accumulatedText.toString(),
                        status = ChatStatus.ERROR,
                    )
                }
                throw e
            } catch (e: ChatException) {
                if (currentAssistantId > 0) {
                    repository.updateMessage(
                        id = currentAssistantId,
                        content = accumulatedText.toString(),
                        status = ChatStatus.ERROR,
                    )
                }
                _uiState.update { it.copy(errorMessage = humanReadable(e)) }
            } finally {
                _uiState.update { it.copy(isStreaming = false) }
                streamJob = null
            }
        }
    }

    fun stopStreaming() {
        streamJob?.cancel()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun humanReadable(e: ChatException): String = when (e) {
        is ChatException.Unauthorized -> "Проблема с доступом к ИИ"
        is ChatException.RateLimited -> "Слишком много запросов, подождите немного"
        is ChatException.ServerError -> "Сервис временно недоступен (${e.status})"
        is ChatException.Network -> "Нет соединения с интернетом"
        is ChatException.InvalidResponse -> "Некорректный ответ от ИИ"
        is ChatException.Unknown -> "Что-то пошло не так, попробуйте ещё раз"
    }
}

class ChatViewModelFactory(
    private val repository: ChatHistoryRepository,
    private val chatApi: ChatApi,
    private val userLogin: String,
    private val model: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ChatViewModel(repository, chatApi, userLogin, model) as T
}