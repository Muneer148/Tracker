package com.muneer.tracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.lifecycle.viewModelScope
import com.muneer.tracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = TrackerDatabase.get(app).trackerDao()
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val _todayDate = MutableStateFlow(fmt.format(Date()))
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()
    val today: String get() = _todayDate.value
    private fun dayId(date: String): String = UUID.nameUUIDFromBytes(date.toByteArray()).toString()
    val todayDayId: String get() = dayId(today)

    val tasks = _todayDate.flatMapLatest { date ->
        dao.tasksForDay(dayId(date))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habits = dao.activeHabits().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habitLogs = _todayDate.flatMapLatest { date ->
        dao.habitLogsForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activities = _todayDate.flatMapLatest { date ->
        dao.activitiesForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals = dao.goals().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val projects = dao.projects().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val journal = dao.journal().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val subjects = dao.studySubjects().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val topics = dao.studyTopics().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sessions = dao.studySessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val assessments = dao.assessments().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val weeklyReviews = dao.weeklyReviews().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedTasks = dao.completedTaskCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalStudyMinutes = dao.totalStudyMinutes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val todayStudyMinutes = _todayDate.flatMapLatest { date ->
        dao.studyMinutesForDate(date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val journalCount = dao.journalCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val notes = dao.knowledgeNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            dao.upsertDay(DayEntity(id = todayDayId, date = today))
            dao.insertHabit(HabitEntity(name = "Study / learning"))
            dao.insertHabit(HabitEntity(name = "Exercise"))
            dao.insertHabit(HabitEntity(name = "Sleep on time"))
        }
    }

    suspend fun createBackupJson(): String = TrackerBackup.createJson(
        days = dao.allDays(),
        tasks = dao.allTasks(),
        habits = dao.allHabits(),
        habitLogs = dao.allHabitLogs(),
        activities = dao.allActivities(),
        goals = dao.allGoals(),
        projects = dao.allProjects(),
        journalEntries = dao.allJournalEntries(),
        studySubjects = dao.allStudySubjects(),
        studyTopics = dao.allStudyTopics(),
        studySessions = dao.allStudySessions(),
        assessments = dao.allAssessments(),
        knowledgeNotes = dao.allKnowledgeNotes(),
        weeklyReviews = dao.allWeeklyReviews()
    )

    suspend fun restoreBackupJson(json: String) {
        val data = TrackerBackup.parseAndValidate(json)
        TrackerBackup.restore(TrackerDatabase.get(getApplication()), data)
    }

    fun refreshToday() {
        val currentDate = fmt.format(Date())
        if (_todayDate.value != currentDate) {
            _todayDate.value = currentDate
            viewModelScope.launch {
                dao.upsertDay(DayEntity(id = dayId(currentDate), date = currentDate))
            }
        }
    }

    fun addTask(v: String, priority: Int = 2) = viewModelScope.launch {
        val title = v.trim()
        if (title.isNotEmpty()) {
            val date = today
            val id = dayId(date)
            dao.upsertDay(DayEntity(id = id, date = date))
            dao.insertTask(TaskEntity(dayId = id, title = title, priority = priority.coerceIn(1, 3)))
        }
    }
    fun deleteTask(v: TaskEntity) = viewModelScope.launch { dao.deleteTask(v) }
    fun toggleTask(v: TaskEntity) = viewModelScope.launch { dao.updateTask(v.copy(completed = !v.completed, updatedAt = System.currentTimeMillis())) }
    fun toggleHabit(v: HabitEntity) = viewModelScope.launch {
        val date = today
        val old = dao.habitLogForDateAndHabit(v.id, date)
        dao.upsertHabitLog(HabitLogEntity(v.id, date, if ((old?.completedCount ?: 0) > 0) 0 else 1))
    }
    fun addHabit(v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertHabit(HabitEntity(name = v.trim())) }
    fun deleteHabit(v: HabitEntity) = viewModelScope.launch { dao.deleteHabit(v) }
    fun addActivity(v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertActivity(ActivityEntity(date = today, type = "GENERAL", title = v.trim())) }
    fun addGoal(v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertGoal(GoalEntity(title = v.trim())) }
    fun deleteGoal(v: GoalEntity) = viewModelScope.launch { dao.deleteGoal(v) }
    fun deleteProject(v: ProjectEntity) = viewModelScope.launch { dao.deleteProject(v) }
    fun deleteJournal(v: JournalEntryEntity) = viewModelScope.launch { dao.deleteJournal(v) }
    fun updateGoal(v: GoalEntity, p: Int) = viewModelScope.launch { dao.updateGoal(v.copy(progress = p.coerceIn(0, 100), updatedAt = System.currentTimeMillis())) }
    fun addProject(v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertProject(ProjectEntity(name = v.trim())) }
    fun addJournal(t: String, c: String) = viewModelScope.launch {
        val title = t.trim()
        val content = c.trim()
        if (isValidJournalEntry(title, content)) {
            dao.insertJournal(JournalEntryEntity(date = today, title = title, content = content))
        }
    }
    fun addSubject(v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertSubject(StudySubjectEntity(name = v.trim())) }
    fun addTopic(subjectId: String, v: String) = viewModelScope.launch { if (v.isNotBlank()) dao.insertTopic(StudyTopicEntity(subjectId = subjectId, name = v.trim())) }
    fun addSession(v: String, m: Int) = viewModelScope.launch { if (v.isNotBlank() && m > 0) dao.insertStudySession(StudySessionEntity(date = today, subjectId = null, topicId = null, title = v.trim(), durationMinutes = m)) }
    fun addAssessment(v: String, s: Double, t: Double) = viewModelScope.launch { if (v.isNotBlank() && t > 0) dao.insertAssessment(AssessmentEntity(date = today, subjectId = null, title = v.trim(), score = s, total = t)) }
    fun addWeeklyReview(week: String, wins: String, blockers: String, next: String) = viewModelScope.launch {
        val start = TrackerDateUtils.weekStart(week)
        dao.insertWeeklyReview(
            WeeklyReviewEntity(
                weekStart = start,
                wins = wins.trim(),
                blockers = blockers.trim(),
                nextFocus = next.trim()
            )
        )
    }
    fun addNote(t: String, c: String) = viewModelScope.launch { if (t.isNotBlank() && c.isNotBlank()) dao.insertKnowledgeNote(KnowledgeNoteEntity(topicId = null, title = t.trim(), content = c.trim())) }
}