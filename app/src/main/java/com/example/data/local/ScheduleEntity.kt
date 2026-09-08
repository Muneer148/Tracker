package com.example.data.local

data class ScheduleEntity(
    val id: String,
    val blockType: String,
    val durationMinutes: Int,
    val isCompleted: Boolean,
    val topicStudied: String?
)

fun StudyBlockEntity.toScheduleEntity(): ScheduleEntity {
    val typeName = when (blockIndex) {
        1 -> "Block 1: Overlap Core (GATE CS & DA)"
        2 -> "Block 2: Paper Rotation & Deep Practice"
        3 -> "Block 3: Locked Mandatory Invariant (TCS NQT)"
        else -> "Study Block $blockIndex"
    }
    return ScheduleEntity(
        id = id,
        blockType = typeName,
        durationMinutes = (targetSeconds / 60).toInt(),
        isCompleted = isCompleted,
        topicStudied = selectedTopic.ifBlank { null }
    )
}
