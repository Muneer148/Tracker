package com.example.data.repository

import com.example.BuildConfig
import com.example.ai.AIContextBuilder
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AIRepository(
    private val studyRepository: StudyRepository,
    private val aiContextBuilder: AIContextBuilder = AIContextBuilder()
) {
    suspend fun generateContextualResponse(userQuery: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val userContext = studyRepository.getFullUserContext()
            val formattedPrompt = aiContextBuilder.buildSystemPromptWithMemory(userContext, userQuery)
            val apiKey = BuildConfig.GEMINI_API_KEY

            if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = formattedPrompt))
                        )
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.6f,
                        maxOutputTokens = 1024
                    )
                )
                val response = GeminiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                responseText ?: throw IllegalStateException("Empty response from AI service")
            } else {
                // Offline fallback using local database memory
                studyRepository.generateOfflineAnswerFromMemory(userQuery, userContext)
            }
        }
    }
}
