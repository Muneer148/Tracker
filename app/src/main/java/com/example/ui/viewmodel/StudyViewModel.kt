package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ActiveRecallNoteEntity
import com.example.data.local.AppDatabase
import com.example.data.local.MockTestLogEntity
import com.example.data.local.OswaalLogEntity
import com.example.data.local.StudyBlockEntity
import com.example.data.local.toScheduleEntity
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveTimerState(
    val blockIndex: Int? = null,
    val remainingSeconds: Long = 0L,
    val totalSeconds: Long = 0L,
    val isRunning: Boolean = false,
    val blockTitle: String = ""
)

enum class DashboardTab(val label: String) {
    PHASE_ENGINE("3-Phase Engine"),
    BLOCKS("5H Blocks"),
    AI_TUTOR("AI Tutor (Memory)"),
    TEXT_ANALYTICS("Text Analytics"),
    ACTIVE_RECALL("Active Recall"),
    PRACTICE_LOGS("Practice Logs")
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository = StudyRepository(AppDatabase.getDatabase(application).studyDao())
    val todayDateStr: String = repository.getTodayDateString()
    val displayDateStr: String = repository.getDisplayDate()

    private val _currentTab = MutableStateFlow(DashboardTab.PHASE_ENGINE)
    val currentTab: StateFlow<DashboardTab> = _currentTab.asStateFlow()

    // 3-Phase Engine Info
    val currentPhaseInfo = repository.getCurrentPhase()

    // AI Tutor Chat State
    val chatMessages: StateFlow<List<com.example.data.local.ChatMessageEntity>> = repository.chatMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _aiInputText = MutableStateFlow("")
    val aiInputText: StateFlow<String> = _aiInputText.asStateFlow()

    // Text Analytics State
    private val _textAnalytics = MutableStateFlow<com.example.data.repository.TextAnalyticsSummary?>(null)
    val textAnalytics: StateFlow<com.example.data.repository.TextAnalyticsSummary?> = _textAnalytics.asStateFlow()

    private val _activeTimer = MutableStateFlow(ActiveTimerState())
    val activeTimer: StateFlow<ActiveTimerState> = _activeTimer.asStateFlow()

    private var timerJob: Job? = null

    // Filter states
    private val _notesSearchQuery = MutableStateFlow("")
    val notesSearchQuery: StateFlow<String> = _notesSearchQuery.asStateFlow()

    private val _selectedNoteTag = MutableStateFlow("All")
    val selectedNoteTag: StateFlow<String> = _selectedNoteTag.asStateFlow()

    private val _oswaalFilterReviewOnly = MutableStateFlow(false)
    val oswaalFilterReviewOnly: StateFlow<Boolean> = _oswaalFilterReviewOnly.asStateFlow()

    private val _mockTestCategoryFilter = MutableStateFlow("All")
    val mockTestCategoryFilter: StateFlow<String> = _mockTestCategoryFilter.asStateFlow()

    private val todayString = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)

    private val _dailyBlocks = MutableStateFlow<List<com.example.data.local.ScheduleEntity>>(emptyList())
    val dailyBlocks: StateFlow<List<com.example.data.local.ScheduleEntity>> = _dailyBlocks.asStateFlow()

    private val _studyContext = MutableStateFlow<com.example.data.model.UserStudyContext?>(null)
    val studyContext: StateFlow<com.example.data.model.UserStudyContext?> = _studyContext.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultBlocksForDate(todayDateStr)
            refreshTextAnalytics()
            loadDailySchedule()
            loadAnalyticsContext()
        }
    }

    private fun loadDailySchedule() {
        viewModelScope.launch {
            repository.getScheduleForDate(todayDateStr).collect { blocks ->
                _dailyBlocks.value = blocks.map { it.toScheduleEntity() }
            }
        }
    }

    fun loadAnalyticsContext() {
        viewModelScope.launch {
            val context = repository.getFullUserContext()
            _studyContext.value = context
            refreshTextAnalytics()
        }
    }

    fun toggleBlockCompletion(blockId: Long, isCompleted: Boolean, topicStudied: String) {
        toggleBlockCompletion(blockId.toString(), isCompleted, topicStudied)
    }

    fun toggleBlockCompletion(blockId: String, isCompleted: Boolean, topicStudied: String) {
        viewModelScope.launch {
            repository.updateBlockCompletion(blockId, isCompleted, topicStudied)
            loadAnalyticsContext()
        }
    }

    fun saveActiveRecallNote(title: String, content: String, tags: String) {
        viewModelScope.launch {
            val note = ActiveRecallNoteEntity(
                title = title,
                contentMarkdown = content,
                tag = tags,
                reviewTomorrow = true
            )
            repository.insertNote(note)
            loadAnalyticsContext()
        }
    }

    val studyBlocks: StateFlow<List<StudyBlockEntity>> = repository.getStudyBlocksForDate(todayDateStr)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val oswaalLogs: StateFlow<List<OswaalLogEntity>> = combine(
        repository.oswaalLogs,
        _oswaalFilterReviewOnly
    ) { logs, reviewOnly ->
        if (reviewOnly) logs.filter { it.reviewNeeded } else logs
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mockTestLogs: StateFlow<List<MockTestLogEntity>> = combine(
        repository.mockTestLogs,
        _mockTestCategoryFilter
    ) { tests, category ->
        if (category == "All") tests else tests.filter { it.paperCategory == category }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allNotes: StateFlow<List<ActiveRecallNoteEntity>> = combine(
        repository.activeRecallNotes,
        _notesSearchQuery,
        _selectedNoteTag
    ) { notes, query, tag ->
        notes.filter { note ->
            val matchesQuery = query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.contentMarkdown.contains(query, ignoreCase = true)
            val matchesTag = tag == "All" || note.tag.equals(tag, ignoreCase = true)
            matchesQuery && matchesTag
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pinnedReviewNotes: StateFlow<List<ActiveRecallNoteEntity>> = repository.pinnedReviewNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setTab(tab: DashboardTab) {
        _currentTab.value = tab
    }

    fun setNotesSearchQuery(query: String) {
        _notesSearchQuery.value = query
    }

    fun setSelectedNoteTag(tag: String) {
        _selectedNoteTag.value = tag
    }

    fun toggleOswaalFilterReviewOnly() {
        _oswaalFilterReviewOnly.value = !_oswaalFilterReviewOnly.value
    }

    fun setMockTestCategoryFilter(category: String) {
        _mockTestCategoryFilter.value = category
    }

    // --- Study Block Controls & Timer ---
    fun toggleBlockCompletion(block: StudyBlockEntity) {
        val newCompleted = !block.isCompleted
        val updatedSpent = if (newCompleted && block.timeSpentSeconds < block.targetSeconds) {
            block.targetSeconds
        } else {
            block.timeSpentSeconds
        }
        viewModelScope.launch {
            repository.updateBlock(
                block.copy(
                    isCompleted = newCompleted,
                    timeSpentSeconds = updatedSpent
                )
            )
        }
        // Stop timer if this block was active
        if (_activeTimer.value.blockIndex == block.blockIndex) {
            pauseTimer()
        }
    }

    fun updateBlockTopic(block: StudyBlockEntity, newTopic: String, subCategory: String = block.subCategory) {
        viewModelScope.launch {
            repository.updateBlock(
                block.copy(
                    selectedTopic = newTopic,
                    subCategory = subCategory
                )
            )
        }
    }

    fun addTimeToBlock(block: StudyBlockEntity, additionalSeconds: Long) {
        val updatedTime = (block.timeSpentSeconds + additionalSeconds).coerceAtMost(block.targetSeconds)
        val isCompleted = updatedTime >= block.targetSeconds
        viewModelScope.launch {
            repository.updateBlock(
                block.copy(
                    timeSpentSeconds = updatedTime,
                    isCompleted = isCompleted
                )
            )
        }
    }

    fun startTimerForBlock(block: StudyBlockEntity) {
        val current = _activeTimer.value
        if (current.blockIndex == block.blockIndex && current.isRunning) {
            pauseTimer()
            return
        }

        val remaining = (block.targetSeconds - block.timeSpentSeconds).coerceAtLeast(0L)
        _activeTimer.value = ActiveTimerState(
            blockIndex = block.blockIndex,
            remainingSeconds = remaining,
            totalSeconds = block.targetSeconds,
            isRunning = true,
            blockTitle = "Block ${block.blockIndex}: ${block.subCategory}"
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_activeTimer.value.isRunning && _activeTimer.value.remainingSeconds > 0) {
                delay(1000L)
                val currentRem = _activeTimer.value.remainingSeconds - 1
                _activeTimer.value = _activeTimer.value.copy(remainingSeconds = currentRem)

                // Persist increment every 15 seconds or on finish
                if (currentRem % 15 == 0L || currentRem <= 0) {
                    val currentBlock = studyBlocks.value.find { it.blockIndex == block.blockIndex }
                    if (currentBlock != null) {
                        val spent = (currentBlock.targetSeconds - currentRem).coerceAtLeast(currentBlock.timeSpentSeconds)
                        repository.updateBlock(
                            currentBlock.copy(
                                timeSpentSeconds = spent,
                                isCompleted = currentRem <= 0
                            )
                        )
                    }
                }

                if (currentRem <= 0) {
                    _activeTimer.value = _activeTimer.value.copy(isRunning = false)
                    break
                }
            }
        }
    }

    fun pauseTimer() {
        val state = _activeTimer.value
        if (!state.isRunning) return
        timerJob?.cancel()
        _activeTimer.value = state.copy(isRunning = false)

        // Persist final spent time
        val block = studyBlocks.value.find { it.blockIndex == state.blockIndex }
        if (block != null) {
            val spent = (block.targetSeconds - state.remainingSeconds).coerceAtLeast(block.timeSpentSeconds)
            viewModelScope.launch {
                repository.updateBlock(
                    block.copy(
                        timeSpentSeconds = spent,
                        isCompleted = state.remainingSeconds <= 0
                    )
                )
            }
        }
    }

    fun resetTimer(block: StudyBlockEntity) {
        if (_activeTimer.value.blockIndex == block.blockIndex) {
            timerJob?.cancel()
            _activeTimer.value = ActiveTimerState()
        }
        viewModelScope.launch {
            repository.updateBlock(
                block.copy(
                    timeSpentSeconds = 0L,
                    isCompleted = false
                )
            )
        }
    }

    // --- Oswaal Log Actions ---
    fun addOswaalLog(
        chapterName: String,
        subject: String,
        questionsAttempted: Int,
        accuracyRate: Float,
        reviewNeeded: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addOswaalLog(
                chapterName = chapterName,
                subject = subject,
                questionsAttempted = questionsAttempted,
                accuracyRate = accuracyRate,
                reviewNeeded = reviewNeeded,
                notes = notes
            )
        }
    }

    fun toggleOswaalReview(log: OswaalLogEntity) {
        viewModelScope.launch {
            repository.toggleOswaalReview(log)
        }
    }

    fun deleteOswaalLog(id: Long) {
        viewModelScope.launch {
            repository.deleteOswaalLog(id)
        }
    }

    // --- Mock Test Actions ---
    fun addMockTestLog(
        testName: String,
        paperCategory: String,
        scoreObtained: Float,
        totalMarks: Float,
        keyMistakes: String
    ) {
        viewModelScope.launch {
            repository.addMockTestLog(
                testName = testName,
                paperCategory = paperCategory,
                scoreObtained = scoreObtained,
                totalMarks = totalMarks,
                keyMistakes = keyMistakes
            )
        }
    }

    fun deleteMockTestLog(id: Long) {
        viewModelScope.launch {
            repository.deleteMockTestLog(id)
        }
    }

    // --- Active Recall Notes Actions ---
    fun addNote(
        title: String,
        contentMarkdown: String,
        tag: String,
        reviewTomorrow: Boolean
    ) {
        viewModelScope.launch {
            repository.addNote(
                title = title,
                contentMarkdown = contentMarkdown,
                tag = tag,
                reviewTomorrow = reviewTomorrow
            )
        }
    }

    fun toggleNoteReview(note: ActiveRecallNoteEntity) {
        viewModelScope.launch {
            repository.toggleNoteReview(note)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteNote(id)
            refreshTextAnalytics()
        }
    }

    // --- AI Tutor Actions ---
    fun setAiInputText(text: String) {
        _aiInputText.value = text
    }

    fun sendAiMessage(prompt: String = _aiInputText.value) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() || _isAiGenerating.value) return
        _aiInputText.value = ""
        _isAiGenerating.value = true

        viewModelScope.launch {
            try {
                repository.askAiTutor(trimmed)
            } finally {
                _isAiGenerating.value = false
            }
        }
    }

    fun clearAiChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun refreshTextAnalytics() {
        viewModelScope.launch {
            _textAnalytics.value = repository.calculateTextAnalytics()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
