package com.muneer.tracker.data

import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackerDateUtilsTest {
    private val utc = TimeZone.getTimeZone("UTC")

    @Test
    fun sundayUsesPreviousMondayAsWeekStart() {
        assertEquals("2026-10-05", TrackerDateUtils.weekStart("2026-10-11", utc))
    }

    @Test
    fun mondayRemainsTheWeekStart() {
        assertEquals("2026-10-12", TrackerDateUtils.weekStart("2026-10-12", utc))
    }

    @Test
    fun weekStartCanFallInPreviousCalendarYear() {
        assertEquals("2026-12-28", TrackerDateUtils.weekStart("2027-01-03", utc))
    }
}
