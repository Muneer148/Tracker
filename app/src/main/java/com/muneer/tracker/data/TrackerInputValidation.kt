package com.muneer.tracker.data

/**
 * Journal entries require meaningful text in both fields.
 * Whitespace-only values are rejected before touching the database.
 */
internal fun isValidJournalEntry(title: String, content: String): Boolean =
    title.isNotBlank() && content.isNotBlank()
