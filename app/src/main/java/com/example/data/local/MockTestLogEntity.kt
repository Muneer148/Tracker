package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mock_test_logs")
data class MockTestLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val testName: String,
    val paperCategory: String, // "GATE CS", "GATE DA", "TCS NQT", "Snowflake"
    val scoreObtained: Float,
    val totalMarks: Float,
    val dateTaken: String,
    val keyMistakes: String = ""
) {
    val percentage: Float
        get() = if (totalMarks > 0f) (scoreObtained / totalMarks) * 100f else 0f
}
