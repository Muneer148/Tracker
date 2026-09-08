package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oswaal_logs")
data class OswaalLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val chapterName: String,
    val subject: String, // "Mathematics" or "General Aptitude"
    val questionsAttempted: Int,
    val accuracyRate: Float, // e.g. 85.0f
    val reviewNeeded: Boolean,
    val dateLogged: String,
    val notes: String = ""
)
