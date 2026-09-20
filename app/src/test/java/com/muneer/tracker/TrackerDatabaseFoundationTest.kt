package com.muneer.tracker

import android.content.Context
import androidx.room.Room
import com.muneer.tracker.data.TrackerDatabase
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

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
    }
}
