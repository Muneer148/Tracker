package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // 1. Study Blocks
    @Query("SELECT * FROM study_blocks WHERE dateStr = :dateStr ORDER BY blockIndex ASC")
    fun getStudyBlocksForDate(dateStr: String): Flow<List<StudyBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBlock(block: StudyBlockEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBlocksIfAbsent(blocks: List<StudyBlockEntity>)

    @Query("SELECT COUNT(*) FROM study_blocks WHERE isCompleted = 1")
    fun getTotalCompletedBlocksCount(): Flow<Int>

    @Query("SELECT * FROM study_blocks ORDER BY dateStr DESC")
    suspend fun getAllStudyBlocksList(): List<StudyBlockEntity>

    @Query("SELECT DISTINCT dateStr FROM study_blocks WHERE isCompleted = 1 ORDER BY dateStr DESC")
    suspend fun getDatesWithCompletedBlocks(): List<String>

    // 2. Oswaal Practice Logs
    @Query("SELECT * FROM oswaal_logs ORDER BY id DESC")
    fun getAllOswaalLogs(): Flow<List<OswaalLogEntity>>

    @Query("SELECT * FROM oswaal_logs ORDER BY id DESC")
    suspend fun getAllOswaalLogsList(): List<OswaalLogEntity>

    @Query("SELECT * FROM oswaal_logs WHERE reviewNeeded = 1 OR accuracyRate < 70 ORDER BY id DESC")
    suspend fun getStruggledOswaalTopics(): List<OswaalLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOswaalLog(log: OswaalLogEntity): Long

    @Update
    suspend fun updateOswaalLog(log: OswaalLogEntity)

    @Query("DELETE FROM oswaal_logs WHERE id = :id")
    suspend fun deleteOswaalLog(id: Long)

    // 3. Mock Test Logs
    @Query("SELECT * FROM mock_test_logs ORDER BY id DESC")
    fun getAllMockTestLogs(): Flow<List<MockTestLogEntity>>

    @Query("SELECT * FROM mock_test_logs ORDER BY id DESC")
    suspend fun getAllMockTestLogsList(): List<MockTestLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockTestLog(log: MockTestLogEntity): Long

    @Query("DELETE FROM mock_test_logs WHERE id = :id")
    suspend fun deleteMockTestLog(id: Long)

    // 4. Active Recall Notes
    @Query("SELECT * FROM active_recall_notes ORDER BY reviewTomorrow DESC, createdTimestamp DESC")
    fun getAllActiveRecallNotes(): Flow<List<ActiveRecallNoteEntity>>

    @Query("SELECT * FROM active_recall_notes ORDER BY reviewTomorrow DESC, createdTimestamp DESC")
    suspend fun getAllActiveRecallNotesList(): List<ActiveRecallNoteEntity>

    @Query("SELECT * FROM active_recall_notes WHERE reviewTomorrow = 1 ORDER BY createdTimestamp DESC")
    fun getPinnedReviewNotes(): Flow<List<ActiveRecallNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ActiveRecallNoteEntity): Long

    @Update
    suspend fun updateNote(note: ActiveRecallNoteEntity)

    @Query("DELETE FROM active_recall_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    // 5. Syllabus Topics (Phase Allocation & AI Memory)
    @Query("SELECT * FROM syllabus_topics ORDER BY phaseNumber ASC, id ASC")
    fun getAllSyllabusTopics(): Flow<List<SyllabusTopicEntity>>

    @Query("SELECT * FROM syllabus_topics ORDER BY phaseNumber ASC, id ASC")
    suspend fun getAllSyllabusTopicsList(): List<SyllabusTopicEntity>

    @Query("SELECT * FROM syllabus_topics WHERE phaseNumber = :phaseNumber ORDER BY id ASC")
    fun getTopicsForPhase(phaseNumber: Int): Flow<List<SyllabusTopicEntity>>

    @Query("SELECT * FROM syllabus_topics WHERE struggled = 1 ORDER BY id DESC")
    suspend fun getStruggledSyllabusTopics(): List<SyllabusTopicEntity>

    @Query("SELECT * FROM syllabus_topics WHERE topicName LIKE '%' || :query || '%' OR struggleNotes LIKE '%' || :query || '%'")
    suspend fun searchTopics(query: String): List<SyllabusTopicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyllabusTopic(topic: SyllabusTopicEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSyllabusTopicsIfAbsent(topics: List<SyllabusTopicEntity>)

    @Update
    suspend fun updateSyllabusTopic(topic: SyllabusTopicEntity)

    @Query("UPDATE syllabus_topics SET isCompleted = :completed WHERE id = :id")
    suspend fun setTopicCompletion(id: Long, completed: Boolean)

    // 6. AI Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY id ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
