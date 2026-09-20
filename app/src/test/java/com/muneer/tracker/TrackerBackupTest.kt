package com.muneer.tracker

import androidx.room.Room
import com.muneer.tracker.data.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import org.robolectric.RuntimeEnvironment

class TrackerBackupTest {
    private lateinit var database: TrackerDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            TrackerDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun sampleData(): TrackerBackup.RestoreData {
        val day = DayEntity("d1", "2026-09-20", "Day", 1L, 2L)
        val goal = GoalEntity("g1", "Goal", "Desc", "2026-12-31", 40, "ACTIVE", 1L, 2L)
        val project = ProjectEntity("p1", "Project", "Desc", "ACTIVE", "g1", 1L, 2L)
        val habit = HabitEntity("h1", "Study", "Daily", 1, true, 1L, 2L)
        val subject = StudySubjectEntity("s1", "CS", "GATE", 60, true)
        val topic = StudyTopicEntity("st1", "s1", "DBMS", "TODO", 2, "Notes", 2L)
        return TrackerBackup.RestoreData(
            days = listOf(day),
            tasks = listOf(TaskEntity("t1", "d1", "Task", "Desc", 1, true, 123L, "p1", 1L, 2L)),
            habits = listOf(habit),
            habitLogs = listOf(HabitLogEntity("h1", "2026-09-20", 1, 2L)),
            activities = listOf(ActivityEntity("a1", "2026-09-20", "GENERAL", "Activity", 30, "meta", 1L)),
            goals = listOf(goal),
            projects = listOf(project),
            journalEntries = listOf(JournalEntryEntity("j1", "2026-09-20", "Journal", "Entry", 4, 1L, 2L)),
            studySubjects = listOf(subject),
            studyTopics = listOf(topic),
            studySessions = listOf(StudySessionEntity("ss1", "2026-09-20", "s1", "st1", "Session", 30, 4, "Notes", 1L)),
            assessments = listOf(AssessmentEntity("as1", "2026-09-20", "s1", "Quiz", 8.0, 10.0, "Mistake", 1L)),
            knowledgeNotes = listOf(KnowledgeNoteEntity("n1", "st1", "Note", "Content", "tag", "2026-10-01", 1L, 2L)),
            weeklyReviews = listOf(WeeklyReviewEntity("w1", "2026-09-14", "Wins", "Blockers", "Next", 1L))
        )
    }

    private fun insert(data: TrackerBackup.RestoreData) {
        val d = database.trackerDao()
        d.insertDays(data.days); d.insertGoals(data.goals); d.insertProjects(data.projects)
        d.insertHabits(data.habits); d.insertTasks(data.tasks); d.insertHabitLogs(data.habitLogs)
        d.insertActivities(data.activities); d.insertJournalEntries(data.journalEntries)
        d.insertStudySubjects(data.studySubjects); d.insertStudyTopics(data.studyTopics)
        d.insertStudySessions(data.studySessions); d.insertAssessments(data.assessments)
        d.insertKnowledgeNotes(data.knowledgeNotes); d.insertWeeklyReviews(data.weeklyReviews)
    }

    private fun exportCurrent(): String {
        val d = database.trackerDao()
        return TrackerBackup.createJson(
            d.allDays(), d.allTasks(), d.allHabits(), d.allHabitLogs(), d.allActivities(),
            d.allGoals(), d.allProjects(), d.allJournalEntries(), d.allStudySubjects(),
            d.allStudyTopics(), d.allStudySessions(), d.allAssessments(), d.allKnowledgeNotes(),
            d.allWeeklyReviews()
        )
    }

    @Test
    fun exportContainsVersionAndAllCollections() {
        val root = JSONObject(TrackerBackup.createJson(
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
        ))
        assertEquals(TrackerBackup.FORMAT_VERSION, root.getInt("formatVersion"))
        assertEquals(TrackerBackup.DATABASE_VERSION, root.getInt("databaseVersion"))
        assertTrue(root.getLong("exportedAt") > 0)
        listOf("days","tasks","habits","habitLogs","activities","goals","projects","journalEntries",
            "studySubjects","studyTopics","studySessions","assessments","knowledgeNotes","weeklyReviews")
            .forEach { assertEquals(1, root.getJSONArray(it).length()) }
    }

    @Test
    fun parseAndValidateRejectsMissingCollection() {
        val json = JSONObject()
            .put("formatVersion", TrackerBackup.FORMAT_VERSION)
            .put("databaseVersion", TrackerBackup.DATABASE_VERSION)
            .put("exportedAt", 1L).toString()
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
            .put("formatVersion", TrackerBackup.FORMAT_VERSION).put("databaseVersion", TrackerBackup.DATABASE_VERSION)
            .put("exportedAt", 1L)
            .put("days", JSONArray().put(JSONObject().put("id","d1").put("date","2026-09-20").put("note","").put("createdAt",1L).put("updatedAt",1L)))
            .put("tasks", JSONArray().put(JSONObject().put("id","t1").put("dayId","missing").put("title","Task").put("description","").put("priority",2).put("completed",false).put("dueAt",JSONObject.NULL).put("projectId",JSONObject.NULL).put("createdAt",1L).put("updatedAt",1L)))
            .put("habits", JSONArray()).put("habitLogs", JSONArray()).put("activities", JSONArray()).put("goals", JSONArray())
            .put("projects", JSONArray()).put("journalEntries", JSONArray()).put("studySubjects", JSONArray())
            .put("studyTopics", JSONArray()).put("studySessions", JSONArray()).put("assessments", JSONArray())
            .put("knowledgeNotes", JSONArray()).put("weeklyReviews", JSONArray()).toString()
        try {
            TrackerBackup.parseAndValidate(json)
            throw AssertionError("Expected validation failure")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("Task references"))
        }
    }

    @Test
    fun parseAndValidateRejectsMalformedScalarType() {
        val json = JSONObject(TrackerBackup.createJson(
            days = listOf(DayEntity("d1", "2026-09-20")),
            tasks = emptyList(), habits = emptyList(), habitLogs = emptyList(), activities = emptyList(),
            goals = emptyList(), projects = emptyList(), journalEntries = emptyList(), studySubjects = emptyList(),
            studyTopics = emptyList(), studySessions = emptyList(), assessments = emptyList(),
            knowledgeNotes = emptyList(), weeklyReviews = emptyList()
        ))
        json.getJSONArray("days").getJSONObject(0).put("createdAt", "not-a-number")
        try {
            TrackerBackup.parseAndValidate(json.toString())
            throw AssertionError("Expected validation failure")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("createdAt"))
        }
    }

    @Test
    fun restoreRoundTripReplacesDatabaseWithBackup() {
        val original = sampleData()
        insert(original)
        val backup = TrackerBackup.parseAndValidate(exportCurrent())

        database.trackerDao().clearTasks()
        database.trackerDao().insertTask(TaskEntity("replacement", "d1", "Replacement"))
        TrackerBackup.restore(database, backup)

        val d = database.trackerDao()
        assertEquals(listOf("d1"), d.allDays().map { it.id })
        assertEquals(listOf("t1"), d.allTasks().map { it.id })
        assertEquals(listOf("h1"), d.allHabits().map { it.id })
        assertEquals(listOf("h1"), d.allHabitLogs().map { it.habitId })
        assertEquals(listOf("a1"), d.allActivities().map { it.id })
        assertEquals(listOf("g1"), d.allGoals().map { it.id })
        assertEquals(listOf("p1"), d.allProjects().map { it.id })
        assertEquals(listOf("j1"), d.allJournalEntries().map { it.id })
        assertEquals(listOf("s1"), d.allStudySubjects().map { it.id })
        assertEquals(listOf("st1"), d.allStudyTopics().map { it.id })
        assertEquals(listOf("ss1"), d.allStudySessions().map { it.id })
        assertEquals(listOf("as1"), d.allAssessments().map { it.id })
        assertEquals(listOf("n1"), d.allKnowledgeNotes().map { it.id })
        assertEquals(listOf("w1"), d.allWeeklyReviews().map { it.id })
    }

    @Test
    fun restoreRollsBackWhenTransactionFails() {
        val existing = DayEntity("existing", "2026-09-20", "Keep", 1L, 1L)
        database.trackerDao().insertDays(listOf(existing))
        val invalid = sampleData().copy(
            days = listOf(DayEntity("d1", "2026-09-21")),
            weeklyReviews = listOf(
                WeeklyReviewEntity("w1", "2026-09-14"),
                WeeklyReviewEntity("w2", "2026-09-14")
            )
        )

        try {
            TrackerBackup.restore(database, invalid)
            throw AssertionError("Expected transaction failure")
        } catch (_: Exception) {
            // Expected: UNIQUE constraint failure must roll the transaction back.
        }

        assertEquals(listOf(existing), database.trackerDao().allDays())
    }
}
