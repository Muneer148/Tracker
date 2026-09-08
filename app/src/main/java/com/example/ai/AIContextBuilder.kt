package com.example.ai

import com.example.data.model.UserStudyContext
import java.util.Locale

class AIContextBuilder {

    fun buildSystemPromptWithMemory(context: UserStudyContext, userQuery: String): String {
        val notesFormatted = if (context.recentNotes.isEmpty()) {
            "No active recall notes recorded yet."
        } else {
            context.recentNotes.joinToString("\n") { note ->
                "- [${note.tag}] ${note.title}: ${note.contentMarkdown}"
            }
        }

        val completedFormatted = if (context.completedTopics.isEmpty()) {
            "None"
        } else {
            context.completedTopics.joinToString(", ")
        }

        val pendingFormatted = if (context.pendingTopics.isEmpty()) {
            "All core syllabus topics completed."
        } else {
            context.pendingTopics.take(10).joinToString(", ")
        }

        val struggledFormatted = if (context.struggledTopics.isEmpty()) {
            "No active struggle markers flagged."
        } else {
            context.struggledTopics.joinToString("\n") { "- $it" }
        }

        return """
            SYSTEM ROLE: You are an elite AI Tutor specializing in GATE CS, GATE DA, TCS NQT, and Snowflake SQL preparation.
            You have direct access to the user's real-time local study logs, 3-phase schedule allocation, and Room database memory.

            === USER APP DATA & STUDY MEMORY ===
            - Current Study Phase: Phase ${context.currentPhase}
            - Current Streak: ${context.currentStreak} Consecutive Days
            - Overall Adherence Rate: ${String.format(Locale.US, "%.1f", context.adherenceRatePercentage)}%
            - Completed Syllabus Topics: $completedFormatted
            - Upcoming Priority Topics: $pendingFormatted
            
            === STRUGGLED CONCEPTS & LOGGED WEAK SPOTS ===
            $struggledFormatted

            === RECENT ACTIVE RECALL FORMULAS & NOTES ===
            $notesFormatted

            === INSTRUCTIONS ===
            1. Provide direct, highly technical, and exam-focused responses.
            2. When explaining core concepts, reference topics from Phase ${context.currentPhase} first.
            3. If the user asks about their progress, review, or weak spots (e.g., data cleaning concepts or statistics), utilize the provided APP DATA & STUDY MEMORY.
            4. Keep responses concise, scannable, and structured for fast learning.

            USER QUERY: $userQuery
        """.trimIndent()
    }
}
