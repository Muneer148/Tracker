package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ActiveRecallNoteEntity
import com.example.data.local.AppDatabase
import com.example.data.local.MockTestLogEntity
import com.example.data.local.OswaalLogEntity
import com.example.data.local.StudyBlockEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: AppDatabase

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GATE 5H Prep", appName)
  }

  @Test
  fun `verify study blocks 5-hour constraint`() = runBlocking {
    val dao = db.studyDao()
    val dateStr = "2026-09-07"
    val blocks = listOf(
      StudyBlockEntity(
        id = "${dateStr}_1",
        dateStr = dateStr,
        blockIndex = 1,
        targetSeconds = (2.5 * 3600).toLong(),
        timeSpentSeconds = 3600,
        selectedTopic = "Discrete Math",
        subCategory = "Math & DSA"
      ),
      StudyBlockEntity(
        id = "${dateStr}_2",
        dateStr = dateStr,
        blockIndex = 2,
        targetSeconds = (1.5 * 3600).toLong(),
        timeSpentSeconds = 1800,
        selectedTopic = "ML Basics",
        subCategory = "DA Machine Learning"
      ),
      StudyBlockEntity(
        id = "${dateStr}_3",
        dateStr = dateStr,
        blockIndex = 3,
        targetSeconds = (1.0 * 3600).toLong(),
        timeSpentSeconds = 1800,
        selectedTopic = "TCS Numerical",
        subCategory = "TCS Aptitude"
      )
    )
    dao.insertBlocksIfAbsent(blocks)

    val loaded = dao.getStudyBlocksForDate(dateStr).first()
    assertEquals(3, loaded.size)
    val totalSeconds = loaded.sumOf { it.targetSeconds }
    assertEquals(18000L, totalSeconds) // 5.0 hours = 18,000 seconds
  }

  @Test
  fun `verify oswaal log and review flag`() = runBlocking {
    val dao = db.studyDao()
    val log = OswaalLogEntity(
      chapterName = "Calculus & Limits",
      subject = "Engineering Mathematics",
      questionsAttempted = 30,
      accuracyRate = 70f,
      reviewNeeded = true,
      dateLogged = "2026-09-07"
    )
    val id = dao.insertOswaalLog(log)
    val allLogs = dao.getAllOswaalLogs().first()
    assertEquals(1, allLogs.size)
    assertTrue(allLogs.first().reviewNeeded)

    // Toggle review flag
    val updated = allLogs.first().copy(reviewNeeded = false)
    dao.updateOswaalLog(updated)
    val reloaded = dao.getAllOswaalLogs().first()
    assertEquals(false, reloaded.first().reviewNeeded)
  }

  @Test
  fun `verify active recall note tag and markdown`() = runBlocking {
    val dao = db.studyDao()
    val note = ActiveRecallNoteEntity(
      title = "Bayes Theorem",
      contentMarkdown = "P(A|B) = [P(B|A)*P(A)] / P(B)",
      tag = "#MathOverlap",
      reviewTomorrow = true
    )
    dao.insertNote(note)

    val allNotes = dao.getAllActiveRecallNotes().first()
    assertEquals(1, allNotes.size)
    assertEquals("Bayes Theorem", allNotes.first().title)
    assertTrue(allNotes.first().reviewTomorrow)

    val pinned = dao.getPinnedReviewNotes().first()
    assertEquals(1, pinned.size)
    assertEquals("#MathOverlap", pinned.first().tag)
  }

  @Test
  fun `verify 3-phase engine schedule logic`() {
    val phase1 = com.example.data.model.StudyPhaseManager.PHASES[0]
    assertEquals(com.example.data.model.PhaseId.PHASE_1, phase1.phaseId)
    assertTrue(phase1.focusSubjects.any { it.contains("Data Management") })
    assertTrue(phase1.excludedSubjects.contains("Computer Organization & Architecture (COA)"))
    assertTrue(phase1.excludedSubjects.contains("Digital Logic"))

    val phase3 = com.example.data.model.StudyPhaseManager.PHASES[2]
    assertEquals(com.example.data.model.PhaseId.PHASE_3, phase3.phaseId)
    assertTrue(phase3.focusSubjects.any { it.contains("COA Crash Course") })

    // Verify Daily Invariant invariant across all phases
    com.example.data.model.StudyPhaseManager.PHASES.forEach { phase ->
      assertTrue(phase.dailyBlocksSummary.any { it.contains("1.0h") || it.contains("Aptitude") })
    }
  }

  @Test
  fun `verify ai chat memory persistence`() = runBlocking {
    val dao = db.studyDao()
    val userMsg = com.example.data.local.ChatMessageEntity(
      role = "user",
      text = "What did I struggle with last week in Oswaal?"
    )
    val assistantMsg = com.example.data.local.ChatMessageEntity(
      role = "assistant",
      text = "According to your Oswaal practice log, you flagged 'Calculus & Limits' for review with 70% accuracy.",
      contextSummary = "1 Struggle Chapter"
    )

    dao.insertChatMessage(userMsg)
    dao.insertChatMessage(assistantMsg)

    val history = dao.getAllChatMessages().first()
    assertEquals(2, history.size)
    assertEquals("user", history[0].role)
    assertEquals("assistant", history[1].role)
    assertTrue(history[1].text.contains("Calculus & Limits"))
  }

  @Test
  fun `verify text analytics calculation`() = runBlocking {
    val dao = db.studyDao()
    val repo = com.example.data.repository.StudyRepository(dao)

    val dateStr = "2026-09-07"
    val blocks = listOf(
      StudyBlockEntity(
        id = "${dateStr}_1",
        dateStr = dateStr,
        blockIndex = 1,
        targetSeconds = 9000,
        timeSpentSeconds = 9000,
        isCompleted = true,
        selectedTopic = "Relational Algebra",
        subCategory = "GATE Core Overlap"
      ),
      StudyBlockEntity(
        id = "${dateStr}_2",
        dateStr = dateStr,
        blockIndex = 2,
        targetSeconds = 5400,
        timeSpentSeconds = 5400,
        isCompleted = true,
        selectedTopic = "Regression Analysis",
        subCategory = "DA Machine Learning"
      ),
      StudyBlockEntity(
        id = "${dateStr}_3",
        dateStr = dateStr,
        blockIndex = 3,
        targetSeconds = 3600,
        timeSpentSeconds = 3600,
        isCompleted = true,
        selectedTopic = "TCS Numerical",
        subCategory = "Aptitude & Maths Invariant"
      )
    )
    dao.insertBlocksIfAbsent(blocks)

    val analytics = repo.calculateTextAnalytics()
    assertEquals(100f, analytics.adherencePercentage, 0.1f)
    assertEquals(3, analytics.completedBlocksCount)
    assertEquals(5.0f, analytics.totalHoursStudied, 0.1f)
    assertTrue(analytics.topicCompletionTable.isNotEmpty())
  }
}

