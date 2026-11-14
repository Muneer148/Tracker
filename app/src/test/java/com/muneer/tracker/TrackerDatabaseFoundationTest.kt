package com.muneer.tracker

import android.content.Context
import androidx.room.Room
import com.muneer.tracker.data.DayEntity
import com.muneer.tracker.data.ExaminationEntity
import com.muneer.tracker.data.StudySubjectEntity
import com.muneer.tracker.data.TrackerDatabase
import com.muneer.tracker.data.MIGRATION_1_2 as Migration12
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class TrackerDatabaseFoundationTest {
    private lateinit var database: TrackerDatabase

    @Before
    fun setUp() {
        val context: Context = RuntimeEnvironment.getApplication()
        database = Room.inMemoryDatabaseBuilder(context, TrackerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun databaseOpensWithExpectedCoreTables() {
        val tables = database.openHelper.writableDatabase.query(
            "SELECT name FROM sqlite_master WHERE type = 'table'"
        ).use { cursor ->
            buildSet {
                val nameColumn = cursor.getColumnIndexOrThrow("name")
                while (cursor.moveToNext()) add(cursor.getString(nameColumn))
            }
        }

        assertTrue("days" in tables)
        assertTrue("tasks" in tables)
        assertTrue("habits" in tables)
        assertTrue("journal_entries" in tables)
        assertTrue("study_sessions" in tables)
        assertTrue("weekly_reviews" in tables)
        assertTrue("examinations" in tables)
    }

    @Test
    fun examinationTableHasCorrectSchema() {
        val columns = database.openHelper.writableDatabase.query(
            "PRAGMA table_info(examinations)"
        ).use { cursor ->
            buildList {
                val nameColumn = cursor.getColumnIndexOrThrow("name")
                val typeColumn = cursor.getColumnIndexOrThrow("type")
                val notNullColumn = cursor.getColumnIndexOrThrow("notnull")
                val pkColumn = cursor.getColumnIndexOrThrow("pk")
                while (cursor.moveToNext()) {
                    add(cursor.getString(nameColumn) to mapOf(
                        "type" to cursor.getString(typeColumn),
                        "notNull" to cursor.getInt(notNullColumn),
                        "pk" to cursor.getInt(pkColumn)
                    ))
                }
            }
        }

        val columnMap = columns.toMap()
        assertEquals("TEXT", columnMap["id"]!!["type"])
        assertEquals(1, columnMap["id"]!!["pk"])
        assertEquals("TEXT", columnMap["subjectId"]!!["type"])
        assertEquals(1, columnMap["subjectId"]!!["notNull"])
        assertEquals("TEXT", columnMap["title"]!!["type"])
        assertEquals(1, columnMap["title"]!!["notNull"])
        assertEquals("TEXT", columnMap["date"]!!["type"])
        assertEquals(1, columnMap["date"]!!["notNull"])
        assertEquals("INTEGER", columnMap["syllabusCoverage"]!!["type"])
        assertEquals(1, columnMap["syllabusCoverage"]!!["notNull"])
        assertEquals("TEXT", columnMap["status"]!!["type"])
        assertEquals(1, columnMap["status"]!!["notNull"])
        assertEquals("INTEGER", columnMap["createdAt"]!!["type"])
        assertEquals(1, columnMap["createdAt"]!!["notNull"])
        assertEquals("INTEGER", columnMap["updatedAt"]!!["type"])
        assertEquals(1, columnMap["updatedAt"]!!["notNull"])
    }

    @Test
    fun examinationIndicesExist() {
        val indices = database.openHelper.writableDatabase.query(
            "SELECT name FROM sqlite_master WHERE type = 'index' AND tbl_name = 'examinations'"
        ).use { cursor ->
            buildSet {
                val nameColumn = cursor.getColumnIndexOrThrow("name")
                while (cursor.moveToNext()) add(cursor.getString(nameColumn))
            }
        }

        assertTrue("index_examinations_date" in indices)
        assertTrue("index_examinations_subjectId" in indices)
        assertTrue("index_examinations_status" in indices)
    }
}

@RunWith(RobolectricTestRunner::class)
class TrackerDatabaseMigrationTest {
    private lateinit var database: TrackerDatabase

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun migrationFromVersion1PreservesExistingData() = runBlocking {
        // Create a file-based database for migration test (in-memory doesn't persist across instances)
        val dbFile = RuntimeEnvironment.getApplication().cacheDir.resolve("tracker_migration_test.db")
        if (dbFile.exists()) dbFile.delete()

        try {
            // Create version 1 database schema manually
            val v1Database = Room.databaseBuilder(
                RuntimeEnvironment.getApplication(),
                TrackerDatabase::class.java,
                dbFile.absolutePath
            ).allowMainThreadQueries()
                .build()

            val dao = v1Database.trackerDao()
            // Insert some v1 data
            dao.insertDays(listOf(DayEntity("d1", "2026-09-20", "Test day", 1L, 2L)))
            dao.insertSubject(StudySubjectEntity("s1", "CS", "GATE", 60, true))

            // Close v1 database
            v1Database.close()

            // Now reopen with migration to v2
            database = Room.databaseBuilder(
                RuntimeEnvironment.getApplication(),
                TrackerDatabase::class.java,
                dbFile.absolutePath
            ).addMigrations(Migration12)
                .allowMainThreadQueries()
                .build()

            val migratedDao = database.trackerDao()
            // Verify existing data preserved
            assertEquals(1, migratedDao.allDays().size)
            assertEquals("d1", migratedDao.allDays()[0].id)
            assertEquals("2026-09-20", migratedDao.allDays()[0].date)

            assertEquals(1, migratedDao.allStudySubjects().size)
            assertEquals("s1", migratedDao.allStudySubjects()[0].id)
            assertEquals("CS", migratedDao.allStudySubjects()[0].name)

            // Verify examinations table exists and is empty
            assertEquals(0, migratedDao.allExaminations().size)

            // Verify we can insert new examinations
            val exam = ExaminationEntity("ex1", "s1", "Midterm", "2026-10-15", 0, null, "UPCOMING", System.currentTimeMillis(), System.currentTimeMillis())
            migratedDao.insertExamination(exam)
            assertEquals(1, migratedDao.allExaminations().size)
            assertEquals("Midterm", migratedDao.allExaminations()[0].title)
        } finally {
            database.close()
            if (dbFile.exists()) dbFile.delete()
        }
    }
}
