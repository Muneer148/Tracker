package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.LightSlate
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMuted
import com.example.data.local.StudyBlockEntity
import com.example.ui.components.ActiveRecallNotesSection
import com.example.ui.components.AiTutorScreen
import com.example.ui.components.MockTestSection
import com.example.ui.components.OswaalPracticeSection
import com.example.ui.components.PhaseEngineSection
import com.example.ui.components.StudyBlockCard
import com.example.ui.components.TextAnalyticsSection
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BrightPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.SnowflakeBlue
import com.example.ui.theme.TcsOrange
import com.example.ui.viewmodel.ActiveTimerState
import com.example.ui.viewmodel.DashboardTab
import com.example.ui.viewmodel.StudyViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MainDashboardScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val activeTimer by viewModel.activeTimer.collectAsStateWithLifecycle()
    val studyBlocks by viewModel.studyBlocks.collectAsStateWithLifecycle()
    val oswaalLogs by viewModel.oswaalLogs.collectAsStateWithLifecycle()
    val mockTestLogs by viewModel.mockTestLogs.collectAsStateWithLifecycle()
    val notes by viewModel.allNotes.collectAsStateWithLifecycle()
    val pinnedNotes by viewModel.pinnedReviewNotes.collectAsStateWithLifecycle()

    val notesQuery by viewModel.notesSearchQuery.collectAsStateWithLifecycle()
    val notesTag by viewModel.selectedNoteTag.collectAsStateWithLifecycle()
    val oswaalReviewOnly by viewModel.oswaalFilterReviewOnly.collectAsStateWithLifecycle()
    val mockCategory by viewModel.mockTestCategoryFilter.collectAsStateWithLifecycle()

    // AI Tutor and Text Analytics States
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
    val aiInputText by viewModel.aiInputText.collectAsStateWithLifecycle()
    val textAnalytics by viewModel.textAnalytics.collectAsStateWithLifecycle()
    val studyContext by viewModel.studyContext.collectAsStateWithLifecycle()

    var quickNoteTitle by remember { mutableStateOf("") }
    var quickNoteContent by remember { mutableStateOf("") }
    var quickNoteTags by remember { mutableStateOf("") }

    // Daily 5-Hour calculations
    val totalTargetSeconds = 5 * 3600L // 18,000s = 5 Hours
    val totalTimeSpentSeconds = studyBlocks.sumOf { it.timeSpentSeconds }
    val totalProgressFraction = (totalTimeSpentSeconds.toFloat() / totalTargetSeconds.toFloat()).coerceIn(0f, 1f)
    val completedBlocksCount = studyBlocks.count { it.isCompleted }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate950,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.linearGradient(listOf(ElectricCyan, NeonIndigo)),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.School,
                                contentDescription = null,
                                tint = Slate950,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GATE & Placement 5H",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate50
                                )
                            )
                            Text(
                                text = "B.Tech CSE (AI&ML) • ${viewModel.displayDateStr}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "OFFLINE • FREE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950,
                    titleContentColor = Slate50
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Active Live Timer Sticky Strip (if running)
            AnimatedVisibility(visible = activeTimer.isRunning) {
                Surface(
                    color = Slate900,
                    border = BorderStroke(1.dp, ElectricCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 840.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(EmeraldSuccess, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeTimer.blockTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Slate50,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatSecondsToClock(activeTimer.remainingSeconds),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            IconButton(
                                onClick = { viewModel.pauseTimer() },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Filled.Pause, contentDescription = "Pause", tint = AmberWarning, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = Slate950,
                contentColor = ElectricCyan,
                edgePadding = 16.dp,
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Slate800)
                    )
                },
                indicator = { tabPositions ->
                    if (currentTab.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                            color = ElectricCyan,
                            height = 3.dp
                        )
                    }
                }
            ) {
                DashboardTab.values().forEach { tab ->
                    Tab(
                        selected = currentTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        text = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    color = if (currentTab == tab) ElectricCyan else Slate400
                                )
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Tab Content in a centered responsive container (max 840dp)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        // Daily 5-Hour Overview Banner
                        DailyOverviewBanner(
                            totalTimeSpentSeconds = totalTimeSpentSeconds,
                            totalTargetSeconds = totalTargetSeconds,
                            progressFraction = totalProgressFraction,
                            completedBlocksCount = completedBlocksCount,
                            studyBlocks = studyBlocks
                        )
                    }

                    when (currentTab) {
                        DashboardTab.PHASE_ENGINE -> {
                            item {
                                PhaseEngineSection(
                                    currentPhase = viewModel.currentPhaseInfo,
                                    onNavigateToBlocks = { viewModel.setTab(DashboardTab.BLOCKS) },
                                    onNavigateToAiTutor = { viewModel.setTab(DashboardTab.AI_TUTOR) }
                                )
                            }
                        }

                        DashboardTab.BLOCKS -> {
                            item {
                                Text(
                                    text = "3 STRUCTURED DAILY INTERVALS (STRICT 5-HOUR LIMIT)",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Slate400,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            items(studyBlocks, key = { it.id }) { block ->
                                StudyBlockCard(
                                    block = block,
                                    activeTimer = activeTimer,
                                    onToggleComplete = { viewModel.toggleBlockCompletion(block) },
                                    onStartTimer = { viewModel.startTimerForBlock(block) },
                                    onPauseTimer = { viewModel.pauseTimer() },
                                    onResetTimer = { viewModel.resetTimer(block) },
                                    onAddQuickTime = { added -> viewModel.addTimeToBlock(block, added) },
                                    onUpdateTopic = { topic, cat -> viewModel.updateBlockTopic(block, topic, cat) }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = LightSlate)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Active Recall Notes (Quick Jot)",
                                    color = TextHighContrast,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Log formulas, edge cases, and algorithmic logic with tags",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                                )

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SlateGray),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        OutlinedTextField(
                                            value = quickNoteTags,
                                            onValueChange = { quickNoteTags = it },
                                            placeholder = { Text("#Algorithms, #TCS-NQT, #DBMS", color = TextMuted) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextHighContrast,
                                                unfocusedTextColor = TextHighContrast,
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = LightSlate
                                            ),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = quickNoteContent,
                                            onValueChange = { quickNoteContent = it },
                                            placeholder = { Text("Markdown supported logic/formulas...", color = TextMuted) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(110.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextHighContrast,
                                                unfocusedTextColor = TextHighContrast,
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = LightSlate
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                if (quickNoteContent.isNotBlank()) {
                                                    val finalTitle = if (quickNoteTitle.isNotBlank()) quickNoteTitle else "Quick Note"
                                                    val finalTags = if (quickNoteTags.isNotBlank()) quickNoteTags else "#General"
                                                    viewModel.saveActiveRecallNote(finalTitle, quickNoteContent, finalTags)
                                                    quickNoteContent = ""
                                                    quickNoteTags = ""
                                                    quickNoteTitle = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Text("Save Note", color = DeepBlue, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        DashboardTab.AI_TUTOR -> {
                            item {
                                AiTutorScreen(
                                    currentPhase = viewModel.currentPhaseInfo,
                                    chatMessages = chatMessages,
                                    inputText = aiInputText,
                                    isGenerating = isAiGenerating,
                                    onInputTextChanged = { viewModel.setAiInputText(it) },
                                    onSendMessage = { viewModel.sendAiMessage(it) },
                                    onClearChat = { viewModel.clearAiChat() }
                                )
                            }
                        }

                        DashboardTab.TEXT_ANALYTICS -> {
                            studyContext?.let { context ->
                                item {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            text = "Performance Metrics",
                                            color = TextHighContrast,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            MetricBox(
                                                title = "Current Phase",
                                                value = "Phase ${context.currentPhase}",
                                                modifier = Modifier.weight(1f)
                                            )
                                            MetricBox(
                                                title = "Daily Streak",
                                                value = "${context.currentStreak} Days",
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        MetricBox(
                                            title = "Schedule Adherence",
                                            value = "${String.format(java.util.Locale.US, "%.1f", context.adherenceRatePercentage)}%",
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Syllabus Audit (Text Table)",
                                            color = TextHighContrast,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = SlateGray),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text("COMPLETED TOPICS", color = SuccessGreen, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = if (context.completedTopics.isEmpty()) "None yet." else context.completedTopics.joinToString(", "),
                                                    color = TextHighContrast,
                                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                                )

                                                HorizontalDivider(color = LightSlate)
                                                Spacer(modifier = Modifier.height(12.dp))

                                                Text("PENDING CORE TOPICS", color = AccentBlue, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = if (context.pendingTopics.isEmpty()) "All caught up." else context.pendingTopics.joinToString(", "),
                                                    color = TextHighContrast,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextAnalyticsSection(
                                    summary = textAnalytics,
                                    onRefresh = {
                                        viewModel.refreshTextAnalytics()
                                        viewModel.loadAnalyticsContext()
                                    }
                                )
                            }
                        }

                        DashboardTab.PRACTICE_LOGS -> {
                            item {
                                OswaalPracticeSection(
                                    oswaalLogs = oswaalLogs,
                                    isFilterReviewOnly = oswaalReviewOnly,
                                    onToggleFilterReviewOnly = { viewModel.toggleOswaalFilterReviewOnly() },
                                    onAddLog = { ch, subj, q, acc, rev, notesStr ->
                                        viewModel.addOswaalLog(ch, subj, q, acc, rev, notesStr)
                                    },
                                    onToggleReview = { viewModel.toggleOswaalReview(it) },
                                    onDeleteLog = { viewModel.deleteOswaalLog(it) }
                                )
                            }

                            item {
                                MockTestSection(
                                    mockTests = mockTestLogs,
                                    selectedCategory = mockCategory,
                                    onCategoryFilterSelected = { viewModel.setMockTestCategoryFilter(it) },
                                    onAddMockTest = { name, cat, sc, tot, mist ->
                                        viewModel.addMockTestLog(name, cat, sc, tot, mist)
                                    },
                                    onDeleteMockTest = { viewModel.deleteMockTestLog(it) }
                                )
                            }
                        }

                        DashboardTab.ACTIVE_RECALL -> {
                            item {
                                ActiveRecallNotesSection(
                                    notes = notes,
                                    searchQuery = notesQuery,
                                    selectedTag = notesTag,
                                    onSearchQueryChange = { viewModel.setNotesSearchQuery(it) },
                                    onTagSelected = { viewModel.setSelectedNoteTag(it) },
                                    onAddNote = { t, c, tag, rev -> viewModel.addNote(t, c, tag, rev) },
                                    onToggleReviewTomorrow = { viewModel.toggleNoteReview(it) },
                                    onDeleteNote = { viewModel.deleteNote(it) }
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DailyOverviewBanner(
    totalTimeSpentSeconds: Long,
    totalTargetSeconds: Long,
    progressFraction: Float,
    completedBlocksCount: Int,
    studyBlocks: List<StudyBlockEntity>
) {
    val totalHours = totalTimeSpentSeconds.toDouble() / 3600.0
    val targetHours = totalTargetSeconds.toDouble() / 3600.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, if (completedBlocksCount == 3) EmeraldSuccess else ElectricCyan.copy(alpha = 0.4f)),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Slate900
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Target exam badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TargetBadge(title = "GATE CS", color = ElectricCyan)
                TargetBadge(title = "GATE DA", color = BrightPurple)
                TargetBadge(title = "TCS NQT", color = TcsOrange)
                TargetBadge(title = "SNOWFLAKE", color = SnowflakeBlue)
                TargetBadge(title = "STRICT 5H LIMIT", color = EmeraldSuccess)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DAILY STUDY PROGRESS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", totalHours),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (totalHours >= 5.0) EmeraldSuccess else Slate50,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = " / ${targetHours.toInt()}.0 Hours",
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Slate400,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                Surface(
                    color = if (completedBlocksCount == 3) EmeraldSuccess.copy(alpha = 0.2f) else Slate800,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (completedBlocksCount == 3) EmeraldSuccess else Slate700)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BLOCKS DONE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = Slate400)
                        )
                        Text(
                            text = "$completedBlocksCount / 3",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (completedBlocksCount == 3) EmeraldSuccess else ElectricCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Three-Segment Visual Progress Bar corresponding to the 3 blocks:
            // Block 1 (2.5h), Block 2 (1.5h), Block 3 (1.0h)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Segment 1 (weight 25 for 2.5h)
                val b1 = studyBlocks.find { it.blockIndex == 1 }
                val b1Frac = if (b1 != null && b1.targetSeconds > 0) {
                    if (b1.isCompleted) 1f else (b1.timeSpentSeconds.toFloat() / b1.targetSeconds.toFloat()).coerceIn(0f, 1f)
                } else 0f
                SegmentBar(
                    label = "Block 1 (2.5h)",
                    progress = b1Frac,
                    activeColor = ElectricCyan,
                    modifier = Modifier.weight(2.5f)
                )

                // Segment 2 (weight 15 for 1.5h)
                val b2 = studyBlocks.find { it.blockIndex == 2 }
                val b2Frac = if (b2 != null && b2.targetSeconds > 0) {
                    if (b2.isCompleted) 1f else (b2.timeSpentSeconds.toFloat() / b2.targetSeconds.toFloat()).coerceIn(0f, 1f)
                } else 0f
                SegmentBar(
                    label = "Block 2 (1.5h)",
                    progress = b2Frac,
                    activeColor = BrightPurple,
                    modifier = Modifier.weight(1.5f)
                )

                // Segment 3 (weight 10 for 1.0h)
                val b3 = studyBlocks.find { it.blockIndex == 3 }
                val b3Frac = if (b3 != null && b3.targetSeconds > 0) {
                    if (b3.isCompleted) 1f else (b3.timeSpentSeconds.toFloat() / b3.targetSeconds.toFloat()).coerceIn(0f, 1f)
                } else 0f
                SegmentBar(
                    label = "Block 3 (1.0h)",
                    progress = b3Frac,
                    activeColor = SnowflakeBlue,
                    modifier = Modifier.weight(1.0f)
                )
            }
        }
    }
}

@Composable
private fun SegmentBar(
    label: String,
    progress: Float,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (progress >= 1f) EmeraldSuccess else activeColor,
            trackColor = Slate800
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = Slate400,
                fontWeight = FontWeight.Medium
            ),
            maxLines = 1
        )
    }
}

@Composable
private fun TargetBadge(title: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 9.sp
            )
        )
    }
}

private fun formatSecondsToClock(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
}
