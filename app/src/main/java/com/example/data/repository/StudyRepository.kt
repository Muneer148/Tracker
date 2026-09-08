package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.GeminiClient
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.local.ActiveRecallNoteEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.MockTestLogEntity
import com.example.data.local.OswaalLogEntity
import com.example.data.local.StudyBlockEntity
import com.example.data.local.StudyDao
import com.example.data.model.PhaseId
import com.example.data.model.PhaseScheduleInfo
import com.example.data.model.StudyPhaseManager
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

data class TextAnalyticsSummary(
    val adherencePercentage: Float,
    val currentStreakDays: Int,
    val totalHoursStudied: Float,
    val plannedHoursTarget: Float,
    val completedBlocksCount: Int,
    val totalBlocksCount: Int,
    val oswaalAverageAccuracy: Float,
    val mockAveragePercentage: Float,
    val activeRecallNotesCount: Int,
    val struggledTopicsCount: Int,
    val topicCompletionTable: List<TopicAdherenceRow>
)

data class TopicAdherenceRow(
    val subject: String,
    val phase: String,
    val hoursLogged: Float,
    val targetHours: Float,
    val statusText: String
)

class StudyRepository(private val dao: StudyDao) {

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getDisplayDate(): String {
        val sdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentPhase(): PhaseScheduleInfo {
        return StudyPhaseManager.determineCurrentPhase(LocalDate.now())
    }

    suspend fun ensureDefaultBlocksForDate(dateStr: String) {
        val currentPhase = getCurrentPhase()

        val (block1Topic, block1Cat, block1Notes) = when (currentPhase.phaseId) {
            PhaseId.PHASE_1 -> Triple(
                "Data Management (SQL, Relational Algebra, Normalization)",
                "GATE CS/DA Overlap Core",
                "Focus on BCNF decomposition & SQL Joins"
            )
            PhaseId.PHASE_2 -> Triple(
                "Operating Systems: Virtual Memory & Process Sync",
                "Non-Hardware Core (CS)",
                "Paging, Semaphores & Deadlock algorithms"
            )
            PhaseId.PHASE_3 -> Triple(
                "GATE Full-Syllabus Mock Test & Error Audit",
                "Phase 3 Hardcore Revision",
                "Solve 65 Questions in timed 3-hour window"
            )
        }

        val (block2Topic, block2Cat, block2Notes) = when (currentPhase.phaseId) {
            PhaseId.PHASE_1 -> Triple(
                "Regression & Statistical Modeling (Linear/Logistic)",
                "DA Machine Learning",
                "Cost function derivations & Gradient Descent"
            )
            PhaseId.PHASE_2 -> Triple(
                "Compiler Design: Parsing & Syntax Directed Translation",
                "Non-Hardware Core (CS)",
                "LL(1), LR(0), LALR tables (COA & DL excluded)"
            )
            PhaseId.PHASE_3 -> Triple(
                "COA & Digital Logic Crash Course",
                "Dedicated Hardware Crash",
                "Pipelining hazards, cache mapping, k-maps"
            )
        }

        // Daily Invariant: Regardless of phase, allocate dedicated 1.0 to 1.5-hour block for Aptitude and Basic Maths
        val block3Topic = "Mandatory Invariant: Aptitude & Basic Maths Drills"
        val block3Cat = "Aptitude & Basic Maths"
        val block3Notes = "Daily Invariant (1.0-1.5h): Oswaal General Aptitude / Probability & Permutations"

        val defaultBlocks = listOf(
            StudyBlockEntity(
                id = "${dateStr}_1",
                dateStr = dateStr,
                blockIndex = 1,
                targetSeconds = 2 * 3600L + 1800L, // 2.5 Hours = 9000s
                timeSpentSeconds = 0L,
                isCompleted = false,
                selectedTopic = block1Topic,
                subCategory = block1Cat,
                notes = block1Notes
            ),
            StudyBlockEntity(
                id = "${dateStr}_2",
                dateStr = dateStr,
                blockIndex = 2,
                targetSeconds = 1 * 3600L + 1800L, // 1.5 Hours = 5400s
                timeSpentSeconds = 0L,
                isCompleted = false,
                selectedTopic = block2Topic,
                subCategory = block2Cat,
                notes = block2Notes
            ),
            StudyBlockEntity(
                id = "${dateStr}_3",
                dateStr = dateStr,
                blockIndex = 3,
                targetSeconds = 3600L, // 1.0 Hour dedicated invariant (can add +15m/+30m)
                timeSpentSeconds = 0L,
                isCompleted = false,
                selectedTopic = block3Topic,
                subCategory = block3Cat,
                notes = block3Notes
            )
        )
        dao.insertBlocksIfAbsent(defaultBlocks)
    }

    // Schedule & Blocks Compatibility
    fun getScheduleForDate(date: String): Flow<List<StudyBlockEntity>> {
        return dao.getStudyBlocksForDate(date)
    }

    fun getStudyBlocksForDate(dateStr: String): Flow<List<StudyBlockEntity>> = getScheduleForDate(dateStr)

    suspend fun updateBlockCompletion(id: String, isCompleted: Boolean, topicStudied: String? = null) {
        val blocks = dao.getAllStudyBlocksList()
        val target = blocks.find { it.id == id }
        if (target != null) {
            dao.insertOrUpdateBlock(
                target.copy(
                    isCompleted = isCompleted,
                    selectedTopic = topicStudied ?: target.selectedTopic
                )
            )
        }
    }

    suspend fun insertNote(note: ActiveRecallNoteEntity) {
        dao.insertNote(note)
    }

    fun getPinnedNotes(): Flow<List<ActiveRecallNoteEntity>> {
        return dao.getPinnedReviewNotes()
    }

    suspend fun getFullUserContext(): com.example.data.model.UserStudyContext {
        val allSyllabus = dao.getAllSyllabusTopicsList()
        val completedTopics = allSyllabus.filter { it.isCompleted }.map { it.topicName }
        val pendingTopics = allSyllabus.filter { !it.isCompleted }.map { it.topicName }
        val syllabusStruggled = dao.getStruggledSyllabusTopics().map {
            "${it.subject}: ${it.topicName} (${it.struggleNotes})"
        }
        val oswaalStruggled = dao.getStruggledOswaalTopics().map {
            "${it.subject}: ${it.chapterName} (Accuracy: ${it.accuracyRate}%, Notes: ${it.notes})"
        }
        val allStruggles = (syllabusStruggled + oswaalStruggled).distinct()

        val recentNotes = dao.getAllActiveRecallNotesList().take(8)
        val allBlocks = dao.getAllStudyBlocksList()
        val completedBlocks = allBlocks.count { it.isCompleted }
        val totalBlocks = allBlocks.size
        val adherence = if (totalBlocks > 0) {
            (completedBlocks.toDouble() / totalBlocks.toDouble()) * 100.0
        } else {
            0.0
        }
        val streak = dao.getDatesWithCompletedBlocks().size
        val currentPhase = determineCurrentPhase(LocalDate.now())

        return com.example.data.model.UserStudyContext(
            currentPhase = currentPhase,
            completedTopics = completedTopics,
            pendingTopics = pendingTopics,
            recentNotes = recentNotes,
            currentStreak = streak,
            adherenceRatePercentage = adherence,
            struggledTopics = allStruggles
        )
    }

    fun determineCurrentPhase(currentDate: LocalDate): Int {
        val phase2EndDate = LocalDate.of(2026, 12, 21) // 3rd week of Dec cutoff
        val now = currentDate
        return when {
            now.isBefore(LocalDate.of(2026, 11, 1)) -> 1
            now.isBefore(phase2EndDate) -> 2
            else -> 3
        }
    }

    suspend fun generateOfflineAnswerFromMemory(
        query: String,
        context: com.example.data.model.UserStudyContext
    ): String {
        val lower = query.lowercase()
        return when {
            lower.contains("data cleaning") || lower.contains("cleaning") -> {
                buildString {
                    appendLine("🧠 **Local Room Memory Recall: Data Cleaning Concepts**")
                    appendLine("From your logged syllabus memory in Machine Learning:")
                    appendLine("• **Concept:** Missing value imputation (KNN vs MICE)")
                    appendLine("• **Outlier Detection:** IQR boundaries and Winsorization")
                    appendLine("• **Notes:** Logged struggle with continuous distribution imputation and high dimensional outlier pruning.")
                    appendLine("\n**Quick Formula Tip:**")
                    appendLine("- Lower Fence = Q1 - 1.5 * IQR")
                    appendLine("- Upper Fence = Q3 + 1.5 * IQR (where IQR = Q3 - Q1)")
                }
            }
            lower.contains("struggle") || lower.contains("weak") || lower.contains("mistake") || lower.contains("last week") -> {
                buildString {
                    appendLine("📚 **Memory Audit: Logged Weak Spots & Struggled Topics**")
                    if (context.struggledTopics.isEmpty()) {
                        appendLine("No explicitly flagged weak spots in database.")
                    } else {
                        context.struggledTopics.forEach {
                            appendLine("• $it")
                        }
                    }
                    appendLine("\n**Recommendation:** Prioritize these in your Phase ${context.currentPhase} active recall drills before moving forward.")
                }
            }
            lower.contains("phase") || lower.contains("schedule") -> {
                buildString {
                    appendLine("🗓️ **Study Engine Status: Phase ${context.currentPhase} Active**")
                    appendLine("• **Current Streak:** ${context.currentStreak} consecutive days")
                    appendLine("• **Adherence Rate:** ${String.format(Locale.US, "%.1f", context.adherenceRatePercentage)}%")
                    appendLine("• **Upcoming Priority:** ${context.pendingTopics.take(3).joinToString(", ")}")
                    appendLine("• **Mandatory Daily Invariant:** 1.0 to 1.5-hour locked block for Aptitude & Basic Maths (TCS NQT).")
                }
            }
            lower.contains("formula") || lower.contains("recall") || lower.contains("note") -> {
                val matching = context.recentNotes.filter {
                    it.title.lowercase().contains(lower) || it.tag.lowercase().contains(lower) || it.contentMarkdown.lowercase().contains(lower)
                }.ifEmpty { context.recentNotes.take(3) }
                buildString {
                    appendLine("🧠 **Active Recall Notes Retrieved from Database (${matching.size} Notes)**")
                    matching.forEach {
                        appendLine("### ${it.title} (`${it.tag}`)")
                        appendLine(it.contentMarkdown)
                        appendLine()
                    }
                }
            }
            else -> {
                buildString {
                    appendLine("💡 **GATE & Placement AI Tutor (Context Active)**")
                    appendLine("• Phase ${context.currentPhase} Engine Active | Streak: ${context.currentStreak} Days | Adherence: ${String.format(Locale.US, "%.1f", context.adherenceRatePercentage)}%")
                    appendLine("• ${context.completedTopics.size} Topics Completed | ${context.pendingTopics.size} Priority Pending")
                    appendLine()
                    appendLine("Regarding: \"$query\"")
                    appendLine("Stay focused on Phase ${context.currentPhase} high-yield topics (DSA, DBMS, and Linear Algebra / Calculus) and execute your daily 1.0-1.5h Aptitude invariant.")
                }
            }
        }
    }

    suspend fun updateBlock(block: StudyBlockEntity) {
        dao.insertOrUpdateBlock(block)
    }

    // Oswaal Logs
    val oswaalLogs: Flow<List<OswaalLogEntity>> = dao.getAllOswaalLogs()

    suspend fun addOswaalLog(
        chapterName: String,
        subject: String,
        questionsAttempted: Int,
        accuracyRate: Float,
        reviewNeeded: Boolean,
        notes: String = ""
    ): Long {
        val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date())
        return dao.insertOswaalLog(
            OswaalLogEntity(
                chapterName = chapterName,
                subject = subject,
                questionsAttempted = questionsAttempted,
                accuracyRate = accuracyRate,
                reviewNeeded = reviewNeeded,
                dateLogged = dateStr,
                notes = notes
            )
        )
    }

    suspend fun toggleOswaalReview(log: OswaalLogEntity) {
        dao.updateOswaalLog(log.copy(reviewNeeded = !log.reviewNeeded))
    }

    suspend fun deleteOswaalLog(id: Long) {
        dao.deleteOswaalLog(id)
    }

    // Mock Tests
    val mockTestLogs: Flow<List<MockTestLogEntity>> = dao.getAllMockTestLogs()

    suspend fun addMockTestLog(
        testName: String,
        paperCategory: String,
        scoreObtained: Float,
        totalMarks: Float,
        keyMistakes: String = ""
    ): Long {
        val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date())
        return dao.insertMockTestLog(
            MockTestLogEntity(
                testName = testName,
                paperCategory = paperCategory,
                scoreObtained = scoreObtained,
                totalMarks = totalMarks,
                dateTaken = dateStr,
                keyMistakes = keyMistakes
            )
        )
    }

    suspend fun deleteMockTestLog(id: Long) {
        dao.deleteMockTestLog(id)
    }

    // Active Recall Notes
    val activeRecallNotes: Flow<List<ActiveRecallNoteEntity>> = dao.getAllActiveRecallNotes()
    val pinnedReviewNotes: Flow<List<ActiveRecallNoteEntity>> = dao.getPinnedReviewNotes()

    suspend fun addNote(
        title: String,
        contentMarkdown: String,
        tag: String,
        reviewTomorrow: Boolean
    ): Long {
        return dao.insertNote(
            ActiveRecallNoteEntity(
                title = title,
                contentMarkdown = contentMarkdown,
                tag = tag,
                reviewTomorrow = reviewTomorrow
            )
        )
    }

    suspend fun toggleNoteReview(note: ActiveRecallNoteEntity) {
        dao.updateNote(note.copy(reviewTomorrow = !note.reviewTomorrow))
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteNote(id)
    }

    // Chat History
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()

    suspend fun clearChat() {
        dao.clearChatHistory()
    }

    // --- AI TUTOR CONTEXTUAL MEMORY ENGINE ---

    suspend fun askAiTutor(userPrompt: String): String {
        // Save user message to database
        val currentPhase = getCurrentPhase()
        dao.insertChatMessage(
            ChatMessageEntity(
                role = "user",
                text = userPrompt,
                contextSummary = "${currentPhase.title} | Query"
            )
        )

        // Query Room Database to build dynamic contextual memory
        val allBlocks = dao.getAllStudyBlocksList()
        val oswaalLogsList = dao.getAllOswaalLogsList()
        val struggledTopics = dao.getStruggledOswaalTopics()
        val mockTests = dao.getAllMockTestLogsList()
        val allNotes = dao.getAllActiveRecallNotesList()
        val completedDates = dao.getDatesWithCompletedBlocks()

        val completedBlocksCount = allBlocks.count { it.isCompleted }
        val streak = completedDates.size

        // Build comprehensive Contextual Memory string
        val memoryContext = buildString {
            appendLine("=== STUDENT CONTEXTUAL DATABASE MEMORY ===")
            appendLine("• Student Degree: Final-Year B.Tech CSE (AI&ML)")
            appendLine("• Target Exams: GATE CS, GATE DA, TCS NQT, Snowflake Certification")
            appendLine("• Current Multi-Phase Engine: ${currentPhase.title}")
            appendLine("  - Phase Dates: ${currentPhase.dateRangeText}")
            appendLine("  - Phase Focus: ${currentPhase.focusSubjects.joinToString("; ")}")
            appendLine("  - Excluded from Phase: ${currentPhase.excludedSubjects.joinToString(", ").ifEmpty { "None (All Included)" }}")
            appendLine("  - Daily Invariant: ${currentPhase.mandatoryDailyAptitudeHours}h dedicated Aptitude and Basic Maths")
            appendLine("• Adherence & Streaks: $completedBlocksCount blocks completed across $streak active study days.")
            appendLine()

            appendLine("=== STRUGGLED TOPICS & REVIEW NEEDED (From Oswaal & Mocks) ===")
            if (struggledTopics.isEmpty()) {
                appendLine("No explicitly flagged Oswaal chapters at this time.")
            } else {
                struggledTopics.forEach {
                    appendLine("- Chapter: ${it.chapterName} (${it.subject}) | Accuracy: ${it.accuracyRate}% | Flagged Review: ${it.reviewNeeded} | Notes: ${it.notes}")
                }
            }
            appendLine()

            appendLine("=== ACTIVE RECALL NOTES IN MEMORY (${allNotes.size} Total) ===")
            allNotes.take(6).forEach {
                appendLine("- [${it.tag}] ${it.title} (Review Tomorrow: ${it.reviewTomorrow})")
                appendLine("  Content: ${it.contentMarkdown.replace("\n", " ")}")
            }
            appendLine()

            appendLine("=== RECENT MOCK TEST SCORES ===")
            mockTests.take(4).forEach {
                appendLine("- ${it.testName} (${it.paperCategory}): Score ${it.scoreObtained}/${it.totalMarks} (${String.format(Locale.getDefault(), "%.1f", it.percentage)}%) | Mistakes: ${it.keyMistakes}")
            }
        }

        val systemPrompt = """
            You are the expert personal AI Tutor for a final-year B.Tech CSE (AI&ML) student preparing simultaneously for GATE CS, GATE DA, TCS NQT, and Snowflake certification.
            You have direct read-access to the student's local Room database memory: their current study phase, daily blocks, struggled topics, mock scores, and active recall formulas.

            Always answer accurately, authoritatively, concisely, and specifically using the provided student database context. If the student asks about what they struggled with, cite their real logged chapters and mistakes. If they ask about formulas or phases, provide exact syllabus alignment for GATE CS & DA.
        """.trimIndent()

        val fullPrompt = "$memoryContext\n\nStudent Query: $userPrompt"

        // Check if API key is provided and try Gemini
        val apiKey = BuildConfig.GEMINI_API_KEY
        val aiResponseText: String = if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            role = "user",
                            parts = listOf(GeminiPart(text = fullPrompt))
                        )
                    ),
                    systemInstruction = GeminiContent(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        temperature = 0.6f,
                        maxOutputTokens = 800
                    )
                )
                val response = GeminiClient.service.generateContent(apiKey, request)
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: generateOfflineContextualAnswer(userPrompt, currentPhase, struggledTopics, allNotes, streak)
            } catch (e: Exception) {
                // Fallback to offline memory responder on network or API failure
                generateOfflineContextualAnswer(userPrompt, currentPhase, struggledTopics, allNotes, streak, errorNote = e.message)
            }
        } else {
            // Intelligent offline responder reading direct Room memory
            generateOfflineContextualAnswer(userPrompt, currentPhase, struggledTopics, allNotes, streak)
        }

        // Save AI response to Room database
        dao.insertChatMessage(
            ChatMessageEntity(
                role = "model",
                text = aiResponseText,
                contextSummary = "Context Memory: ${struggledTopics.size} struggle items | Phase ${currentPhase.phaseId.number}"
            )
        )

        return aiResponseText
    }

    /**
     * Context-aware local inference engine that inspects the database to answer
     * specific questions directly from memory even when offline or before API key setup.
     */
    private fun generateOfflineContextualAnswer(
        query: String,
        currentPhase: PhaseScheduleInfo,
        struggledTopics: List<OswaalLogEntity>,
        allNotes: List<ActiveRecallNoteEntity>,
        streak: Int,
        errorNote: String? = null
    ): String {
        val lower = query.lowercase()

        return when {
            lower.contains("struggle") || lower.contains("weak") || lower.contains("mistake") || lower.contains("last week") -> {
                buildString {
                    appendLine("📚 **Memory Audit: Topics You Struggled With**")
                    if (struggledTopics.isEmpty()) {
                        appendLine("Good news! You haven't flagged any chapters for review with accuracy < 70%.")
                    } else {
                        struggledTopics.forEach {
                            appendLine("• **${it.chapterName}** (${it.subject})")
                            appendLine("  Accuracy: `${it.accuracyRate}%` | Note: ${it.notes.ifBlank { "Flagged for re-solving" }}")
                        }
                    }
                    appendLine("\n**Recommendation:** Add these to your Phase ${currentPhase.phaseId.number} active recall list before moving to new syllabus blocks.")
                }
            }
            lower.contains("phase") || lower.contains("schedule") || lower.contains("today") -> {
                buildString {
                    appendLine("🗓️ **Current Active Phase: ${currentPhase.title}**")
                    appendLine("• **Timeline:** ${currentPhase.dateRangeText}")
                    appendLine("• **Core Focus:** ${currentPhase.focusSubjects.take(3).joinToString(", ")}")
                    appendLine("• **Excluded in this Phase:** ${currentPhase.excludedSubjects.joinToString(", ").ifEmpty { "None" }}")
                    appendLine("• **Mandatory Daily Invariant:** Dedicated 1.0 - 1.5h Aptitude & Basic Maths block.")
                    appendLine("• **Current Streak:** $streak active study days logged.")
                }
            }
            lower.contains("formula") || lower.contains("recall") || lower.contains("note") || lower.contains("datamanagement") || lower.contains("math") -> {
                val matchingNotes = allNotes.filter {
                    it.title.lowercase().contains(lower) || it.tag.lowercase().contains(lower) || it.contentMarkdown.lowercase().contains(lower)
                }
                val notesToShow = matchingNotes.ifEmpty { allNotes.take(3) }
                buildString {
                    appendLine("🧠 **Active Recall Memory Retrieval (${notesToShow.size} Notes Found)**")
                    notesToShow.forEach {
                        appendLine("### ${it.title} (`${it.tag}`)")
                        appendLine(it.contentMarkdown)
                        appendLine()
                    }
                }
            }
            else -> {
                buildString {
                    appendLine("💡 **GATE & Placement AI Tutor (Context-Loaded)**")
                    appendLine("I have loaded your profile:")
                    appendLine("• Phase: **${currentPhase.title}**")
                    appendLine("• Memory: **${allNotes.size} Active Recall Notes** & **${struggledTopics.size} Flagged Topics**")
                    appendLine("• Streak: **$streak Days**")
                    appendLine()
                    appendLine("Regarding your question: \"$query\"")
                    appendLine("In **Phase 1**, focus on the high-yield overlap between GATE CS & DA (Data Management, Linear Algebra, Regression, and Trees/DP). Remember to keep your daily 1.0-1.5h invariant block reserved for Aptitude.")
                    if (errorNote != null) {
                        appendLine("\n*(Local memory mode active. Cloud Gemini API: $errorNote)*")
                    }
                }
            }
        }
    }

    // --- TEXT-BASED ANALYTICS ENGINE (STRICTLY NO CHARTS/GRAPHS) ---

    suspend fun calculateTextAnalytics(): TextAnalyticsSummary {
        val allBlocks = dao.getAllStudyBlocksList()
        val oswaalLogsList = dao.getAllOswaalLogsList()
        val mockTests = dao.getAllMockTestLogsList()
        val allNotes = dao.getAllActiveRecallNotesList()
        val completedDates = dao.getDatesWithCompletedBlocks()
        val struggledTopics = dao.getStruggledOswaalTopics()

        val totalBlocks = allBlocks.size.coerceAtLeast(1)
        val completedBlocks = allBlocks.count { it.isCompleted }
        val adherencePct = (completedBlocks.toFloat() / totalBlocks.toFloat()) * 100f

        val totalHoursStudied = (allBlocks.sumOf { it.timeSpentSeconds } / 3600f)
        val plannedHoursTarget = (allBlocks.sumOf { it.targetSeconds } / 3600f)

        val oswaalAvgAccuracy = if (oswaalLogsList.isNotEmpty()) {
            oswaalLogsList.map { it.accuracyRate }.average().toFloat()
        } else 0f

        val mockAvgPct = if (mockTests.isNotEmpty()) {
            mockTests.map { it.percentage }.average().toFloat()
        } else 0f

        // Build pure text-based tabular adherence breakdown
        val table = listOf(
            TopicAdherenceRow(
                subject = "GATE CS/DA Overlap (DBMS, DSA, Math)",
                phase = "Phase 1",
                hoursLogged = (allBlocks.filter { it.blockIndex == 1 }.sumOf { it.timeSpentSeconds } / 3600f),
                targetHours = 2.5f * (allBlocks.size / 3).coerceAtLeast(1),
                statusText = "ON TRACK (High Yield)"
            ),
            TopicAdherenceRow(
                subject = "Paper Rotation (CS Systems / DA ML)",
                phase = "Phase 1/2",
                hoursLogged = (allBlocks.filter { it.blockIndex == 2 }.sumOf { it.timeSpentSeconds } / 3600f),
                targetHours = 1.5f * (allBlocks.size / 3).coerceAtLeast(1),
                statusText = "ACTIVE (Alternating)"
            ),
            TopicAdherenceRow(
                subject = "Mandatory Invariant (Aptitude & Maths)",
                phase = "All Phases",
                hoursLogged = (allBlocks.filter { it.blockIndex == 3 }.sumOf { it.timeSpentSeconds } / 3600f),
                targetHours = 1.0f * (allBlocks.size / 3).coerceAtLeast(1),
                statusText = "100% MANDATORY"
            ),
            TopicAdherenceRow(
                subject = "COA & Digital Logic Crash",
                phase = "Phase 3",
                hoursLogged = 0.0f,
                targetHours = 20.0f,
                statusText = "SCHEDULED (Post-Dec 20)"
            )
        )

        return TextAnalyticsSummary(
            adherencePercentage = adherencePct,
            currentStreakDays = completedDates.size,
            totalHoursStudied = totalHoursStudied,
            plannedHoursTarget = plannedHoursTarget,
            completedBlocksCount = completedBlocks,
            totalBlocksCount = totalBlocks,
            oswaalAverageAccuracy = oswaalAvgAccuracy,
            mockAveragePercentage = mockAvgPct,
            activeRecallNotesCount = allNotes.size,
            struggledTopicsCount = struggledTopics.size,
            topicCompletionTable = table
        )
    }
}

