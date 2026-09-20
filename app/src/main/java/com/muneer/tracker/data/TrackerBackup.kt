package com.muneer.tracker.data

import org.json.JSONArray
import org.json.JSONObject

object TrackerBackup {
    const val FORMAT_VERSION = 1
    const val DATABASE_VERSION = 1

    fun createJson(
        days: List<DayEntity>,
        tasks: List<TaskEntity>,
        habits: List<HabitEntity>,
        habitLogs: List<HabitLogEntity>,
        activities: List<ActivityEntity>,
        goals: List<GoalEntity>,
        projects: List<ProjectEntity>,
        journalEntries: List<JournalEntryEntity>,
        studySubjects: List<StudySubjectEntity>,
        studyTopics: List<StudyTopicEntity>,
        studySessions: List<StudySessionEntity>,
        assessments: List<AssessmentEntity>,
        knowledgeNotes: List<KnowledgeNoteEntity>,
        weeklyReviews: List<WeeklyReviewEntity>
    ): String {
        val root = JSONObject()
            .put("formatVersion", FORMAT_VERSION)
            .put("databaseVersion", DATABASE_VERSION)
            .put("exportedAt", System.currentTimeMillis())

        root.put("days", JSONArray().also { a -> days.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("note", it.note)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("tasks", JSONArray().also { a -> tasks.forEach { a.put(JSONObject()
            .put("id", it.id).put("dayId", it.dayId).put("title", it.title)
            .put("description", it.description).put("priority", it.priority)
            .put("completed", it.completed).putNullableLong("dueAt", it.dueAt)
            .putNullableString("projectId", it.projectId).put("createdAt", it.createdAt)
            .put("updatedAt", it.updatedAt)) } })

        root.put("habits", JSONArray().also { a -> habits.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("description", it.description)
            .put("targetPerDay", it.targetPerDay).put("active", it.active)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("habitLogs", JSONArray().also { a -> habitLogs.forEach { a.put(JSONObject()
            .put("habitId", it.habitId).put("date", it.date)
            .put("completedCount", it.completedCount).put("updatedAt", it.updatedAt)) } })

        root.put("activities", JSONArray().also { a -> activities.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("type", it.type).put("title", it.title)
            .put("durationMinutes", it.durationMinutes).put("metadata", it.metadata)
            .put("createdAt", it.createdAt)) } })

        root.put("goals", JSONArray().also { a -> goals.forEach { a.put(JSONObject()
            .put("id", it.id).put("title", it.title).put("description", it.description)
            .putNullableString("targetDate", it.targetDate).put("progress", it.progress)
            .put("status", it.status).put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("projects", JSONArray().also { a -> projects.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("description", it.description)
            .put("status", it.status).putNullableString("goalId", it.goalId)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("journalEntries", JSONArray().also { a -> journalEntries.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("title", it.title).put("content", it.content)
            .putNullableInt("mood", it.mood).put("createdAt", it.createdAt)
            .put("updatedAt", it.updatedAt)) } })

        root.put("studySubjects", JSONArray().also { a -> studySubjects.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("category", it.category)
            .put("targetMinutes", it.targetMinutes).put("active", it.active)) } })

        root.put("studyTopics", JSONArray().also { a -> studyTopics.forEach { a.put(JSONObject()
            .put("id", it.id).put("subjectId", it.subjectId).put("name", it.name)
            .put("status", it.status).put("difficulty", it.difficulty).put("notes", it.notes)
            .put("updatedAt", it.updatedAt)) } })

        root.put("studySessions", JSONArray().also { a -> studySessions.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).putNullableString("subjectId", it.subjectId)
            .putNullableString("topicId", it.topicId).put("title", it.title)
            .put("durationMinutes", it.durationMinutes).put("quality", it.quality)
            .put("notes", it.notes).put("createdAt", it.createdAt)) } })

        root.put("assessments", JSONArray().also { a -> assessments.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).putNullableString("subjectId", it.subjectId)
            .put("title", it.title).put("score", it.score).put("total", it.total)
            .put("mistakes", it.mistakes).put("createdAt", it.createdAt)) } })

        root.put("knowledgeNotes", JSONArray().also { a -> knowledgeNotes.forEach { a.put(JSONObject()
            .put("id", it.id).putNullableString("topicId", it.topicId).put("title", it.title)
            .put("content", it.content).put("tags", it.tags).putNullableString("reviewDate", it.reviewDate)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("weeklyReviews", JSONArray().also { a -> weeklyReviews.forEach { a.put(JSONObject()
            .put("id", it.id).put("weekStart", it.weekStart).put("wins", it.wins)
            .put("blockers", it.blockers).put("nextFocus", it.nextFocus).put("createdAt", it.createdAt)) } })

        return root.toString(2)
    }

    private fun JSONObject.putNullableString(key: String, value: String?): JSONObject =
        put(key, value ?: JSONObject.NULL)

    private fun JSONObject.putNullableLong(key: String, value: Long?): JSONObject =
        put(key, value ?: JSONObject.NULL)

    private fun JSONObject.putNullableInt(key: String, value: Int?): JSONObject =
        put(key, value ?: JSONObject.NULL)
}
