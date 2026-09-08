package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_recall_notes")
data class ActiveRecallNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val contentMarkdown: String,
    val tag: String, // "#MathOverlap", "#Algorithms", "#DBMS/SQL", "#TCS-NQT", "#Snowflake"
    val reviewTomorrow: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
) {
    val content: String get() = contentMarkdown
    val isPinned: Boolean get() = reviewTomorrow
}
