package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an item in the rigorous 3-Phase GATE CS/DA syllabus allocation,
 * plus the Daily Invariant (TCS NQT Aptitude / Maths).
 * Serves as persistent contextual memory for the AI tutor and topic tracker.
 */
@Entity(tableName = "syllabus_topics")
data class SyllabusTopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val phaseNumber: Int, // 1 = Overlap, 2 = Core Non-Overlapped, 3 = Revision & COA/DL, 0 = Invariant
    val paperCategory: String, // "GATE CS & DA Overlap", "GATE CS Unique", "GATE DA Unique", "Crash Course / Revision", "Daily Invariant (TCS NQT)"
    val subject: String, // e.g., "Data Structures & Algorithms", "Database Management & SQL", "Machine Learning"
    val topicName: String,
    val isCompleted: Boolean = false,
    val struggled: Boolean = false,
    val struggleNotes: String = "",
    val lastRevisedDate: String = ""
)
