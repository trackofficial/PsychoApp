import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ---------- Модели данных ----------

@Serializable
data class OpenAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>
)

@Serializable
data class OpenAiResponse(
    val choices: List<Choice> = emptyList()
) {
    @Serializable
    data class Choice(
        val message: OpenAiMessage
    )
}

// ---------- Сервис ----------

class AiService(
    private val apiKey: String,
    private val model: String = "deepseek/deepseek-v4.1-flash"
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val systemPrompt = "Ты — дружелюбный и полезный ассистент. Отвечай кратко и по делу."

    suspend fun getAiResponse(userMessage: String): String {
        val request = OpenAiRequest(
            model = model,
            messages = listOf(
                OpenAiMessage("system", systemPrompt),
                OpenAiMessage("user", userMessage)
            )
        )

        val response: OpenAiResponse = client.post("https://openrouter.ai/api/v1/chat/completions") {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header("HTTP-Referer", "https://your-site-url.com")
            header("X-OpenRouter-Title", "PsychoApp")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        return response.choices.firstOrNull()?.message?.content
            ?: "Не удалось получить ответ."
    }
}