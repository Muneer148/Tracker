package com.muneer.tracker.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Date rules shared by Tracker's review and reporting features.
 *
 * Weekly reviews use Monday as the first day of the week.
 */
internal object TrackerDateUtils {
    fun weekStart(date: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
            this.timeZone = timeZone
        }
        val parsedDate = requireNotNull(formatter.parse(date)) {
            "Date must use yyyy-MM-dd format."
        }

        val calendar = Calendar.getInstance(timeZone, Locale.US).apply {
            firstDayOfWeek = Calendar.MONDAY
            minimalDaysInFirstWeek = 4
            time = parsedDate
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        return formatter.format(calendar.time)
    }
}
