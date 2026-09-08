package com.example.data.model

import com.example.data.local.ActiveRecallNoteEntity

typealias NoteEntity = ActiveRecallNoteEntity

data class UserStudyContext(
    val currentPhase: Int,
    val completedTopics: List<String>,
    val pendingTopics: List<String>,
    val recentNotes: List<ActiveRecallNoteEntity>,
    val currentStreak: Int,
    val adherenceRatePercentage: Double,
    val struggledTopics: List<String> = emptyList()
)
