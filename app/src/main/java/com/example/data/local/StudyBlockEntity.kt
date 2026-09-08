package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_blocks")
data class StudyBlockEntity(
    @PrimaryKey val id: String, // e.g. "2026-09-07_1"
    val dateStr: String,
    val blockIndex: Int, // 1, 2, or 3
    val targetSeconds: Long, // 9000L, 5400L, 3600L
    val timeSpentSeconds: Long = 0L,
    val isCompleted: Boolean = false,
    val selectedTopic: String = "",
    val subCategory: String = "",
    val notes: String = ""
)
