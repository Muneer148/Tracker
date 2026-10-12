package com.muneer.tracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import com.muneer.tracker.data.isValidJournalEntry
import org.junit.Test

class TrackerModelTest {
    @Test fun goalProgressIsClamped() {
        assertEquals(0,  (-10).coerceIn(0, 100))
        assertEquals(100, 140.coerceIn(0, 100))
        assertEquals(65, 65.coerceIn(0, 100))
    }

    @Test fun emptyTaskIsNotAccepted() {
        assertTrue("   ".isBlank())
    }

    @Test fun journalRequiresNonblankTitleAndContent() {
        assertTrue(isValidJournalEntry("GATE revision", "Reviewed conditional probability."))
        assertTrue(!isValidJournalEntry("", "Some content"))
        assertTrue(!isValidJournalEntry("A title", ""))
        assertTrue(!isValidJournalEntry("   ", "   "))
    }
}
