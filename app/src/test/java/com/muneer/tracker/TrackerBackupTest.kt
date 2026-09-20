package com.muneer.tracker

import com.muneer.tracker.data.*
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TrackerBackupTest {
    @Test
    fun exportContainsVersionAndAllCollections() {
        val json = TrackerBackup.createJson(
            days = listOf(DayEntity(id="d1", date="2026-09-20")),
            tasks = listOf(TaskEntity(id="t1", dayId="d1", title="Test")),
            habits = listOf(HabitEntity(id="h1", name="Study")),
            habitLogs = listOf(HabitLogEntity(habitId="h1", date="2026-09-20", completedCount=1)),
            activities = listOf(ActivityEntity(id="a1", date="2026-09-20", type="GENERAL", title="Activity")),
            goals = listOf(GoalEntity(id="g1", title="Goal")),
            projects = listOf(ProjectEntity(id="p1", name="Project")),
            journalEntries = listOf(JournalEntryEntity(id="j1", date="2026-09-20", title="Journal", content="Entry")),
            studySubjects = listOf(StudySubjectEntity(id="s1", name="CS")),
            studyTopics = listOf(StudyTopicEntity(id="st1", subjectId="s1", name="DBMS")),
            studySessions = listOf(StudySessionEntity(id="ss1", date="2026-09-20", subjectId="s1", topicId="st1", title="Session", durationMinutes=30)),
            assessments = listOf(AssessmentEntity(id="as1", date="2026-09-20", subjectId="s1", title="Quiz", score=8.0, total=10.0)),
            knowledgeNotes = listOf(KnowledgeNoteEntity(id="n1", topicId="st1", title="Note", content="Content")),
            weeklyReviews = listOf(WeeklyReviewEntity(id="w1", weekStart="2026-09-14"))
        )

        val root = JSONObject(json)

        assertEquals(TrackerBackup.FORMAT_VERSION, root.getInt("formatVersion"))
        assertEquals(TrackerBackup.DATABASE_VERSION, root.getInt("databaseVersion"))
        assertTrue(root.getLong("exportedAt") > 0)
        assertEquals(1, root.getJSONArray("days").length())
        assertEquals("Test", root.getJSONArray("tasks").getJSONObject(0).getString("title"))
        assertEquals(1, root.getJSONArray("habits").length())
        assertEquals(1, root.getJSONArray("habitLogs").length())
        assertEquals(1, root.getJSONArray("activities").length())
        assertEquals(1, root.getJSONArray("goals").length())
        assertEquals(1, root.getJSONArray("projects").length())
        assertEquals(1, root.getJSONArray("journalEntries").length())
        assertEquals(1, root.getJSONArray("studySubjects").length())
        assertEquals(1, root.getJSONArray("studyTopics").length())
        assertEquals(1, root.getJSONArray("studySessions").length())
        assertEquals(1, root.getJSONArray("assessments").length())
        assertEquals(1, root.getJSONArray("knowledgeNotes").length())
        assertEquals(1, root.getJSONArray("weeklyReviews").length())
    }
    @Test
    fun parseAndValidateRejectsMissingCollection() {
        val json = JSONObject()
            .put("formatVersion", TrackerBackup.FORMAT_VERSION)
            .put("databaseVersion", TrackerBackup.DATABASE_VERSION)
            .put("exportedAt", 1L)
            .toString()
        try {
            TrackerBackup.parseAndValidate(json)
            throw AssertionError("Expected validation failure")
        } catch (e: IllegalStateException) {
            assertTrue(e.message!!.contains("Backup is missing"))
        }
    }

    @Test
    fun parseAndValidateRejectsBrokenReferences() {
        val json = JSONObject()
            .put("formatVersion", TrackerBackup.FORMAT_VERSION)
            .put("databaseVersion", TrackerBackup.DATABASE_VERSION)
            .put("exportedAt", 1L)
            .put("days", org.json.JSONArray().put(JSONObject()
                .put("id","d1").put("date","2026-09-20").put("note","")
                .put("createdAt",1L).put("updatedAt",1L)))
            .put("tasks", org.json.JSONArray().put(JSONObject()
                .put("id","t1").put("dayId","missing").put("title","Task")
                .put("description","").put("priority",2).put("completed",false)
                .put("dueAt",JSONObject.NULL).put("projectId",JSONObject.NULL)
                .put("createdAt",1L).put("updatedAt",1L)))
            .put("habits", org.json.JSONArray())
            .put("habitLogs", org.json.JSONArray())
            .put("activities", org.json.JSONArray())
            .put("goals", org.json.JSONArray())
            .put("projects", org.json.JSONArray())
            .put("journalEntries", org.json.JSONArray())
            .put("studySubjects", org.json.JSONArray())
            .put("studyTopics", org.json.JSONArray())
            .put("studySessions", org.json.JSONArray())
            .put("assessments", org.json.JSONArray())
            .put("knowledgeNotes", org.json.JSONArray())
            .put("weeklyReviews", org.json.JSONArray())
            .toString()
        try {
            TrackerBackup.parseAndValidate(json)
            throw AssertionError("Expected validation failure")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Task references"))
        }
    }

}
