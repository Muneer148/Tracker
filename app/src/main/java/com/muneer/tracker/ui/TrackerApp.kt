package com.muneer.tracker.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.MyApplicationTheme
import com.muneer.tracker.TrackerViewModel
import com.muneer.tracker.data.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun TrackerRoot(vm: TrackerViewModel) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("tracker_preferences", Context.MODE_PRIVATE)
    }
    var darkTheme by remember {
        mutableStateOf(preferences.getBoolean("dark_theme", true))
    }
    val activity = context as? Activity

    SideEffect {
        activity?.window?.let { window ->
            val barColor = if (darkTheme) Color(0xFF0B1120) else Color(0xFFF7F8FC)
            window.statusBarColor = barColor.toArgb()
            window.navigationBarColor = barColor.toArgb()
            WindowCompat.getInsetsController(window, window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MyApplicationTheme(darkTheme = darkTheme) {
        TrackerApp(
            vm = vm,
            isDarkTheme = darkTheme,
            onThemeChange = { enabled ->
                darkTheme = enabled
                preferences.edit().putBoolean("dark_theme", enabled).apply()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerApp(
    vm: TrackerViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showQuickAddTask by rememberSaveable { mutableStateOf(false) }
    var quickTaskTitle by rememberSaveable { mutableStateOf("") }
    val tabs = listOf("Today", "Study", "Goals", "Journal", "Settings")
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Tracker", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text(listOf("⌂", "▣", "◇", "✎", "⚙")[i]) },
                        label = { Text(label) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (tab == 0) {
                FloatingActionButton(
                    onClick = { showQuickAddTask = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text("+", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    ) { padding ->
        when (tab) {
            0 -> TodayScreen(vm, Modifier.padding(padding))
            1 -> StudyScreen(vm, Modifier.padding(padding))
            2 -> GoalsScreen(vm, Modifier.padding(padding))
            3 -> JournalScreen(vm, Modifier.padding(padding))
            else -> SettingsScreen(
                vm = vm,
                modifier = Modifier.padding(padding),
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange
            )
        }
    }

    if (showQuickAddTask) {
        AlertDialog(
            onDismissRequest = {
                showQuickAddTask = false
                quickTaskTitle = ""
            },
            title = { Text("Add a task") },
            text = {
                OutlinedTextField(
                    value = quickTaskTitle,
                    onValueChange = { quickTaskTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("What needs to get done?") },
                    shape = RoundedCornerShape(16.dp)
                )
            },
            confirmButton = {
                TextButton(
                    enabled = quickTaskTitle.isNotBlank(),
                    onClick = {
                        vm.addTask(quickTaskTitle)
                        quickTaskTitle = ""
                        showQuickAddTask = false
                    }
                ) { Text("Add task") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showQuickAddTask = false
                    quickTaskTitle = ""
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ScreenColumn(modifier: Modifier, content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        subtitle?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ProgressCard(title: String, value: Float, supporting: String) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${(value * 100).toInt()}%", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { value.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.22f)
            )
            Text(supporting, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun QuickAddField(label: String, value: String, onValueChange: (String) -> Unit, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(value, onValueChange, Modifier.weight(1f), singleLine = true, label = { Text(label) }, shape = RoundedCornerShape(16.dp))
        Button(onClick = onAction, enabled = value.isNotBlank(), shape = RoundedCornerShape(16.dp)) { Text(action) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayScreen(vm: TrackerViewModel, modifier: Modifier) {
    val today by vm.todayDate.collectAsStateWithLifecycle()
    var task by rememberSaveable { mutableStateOf("") }
    var taskPriority by rememberSaveable { mutableIntStateOf(2) }
    var taskFilter by rememberSaveable { mutableIntStateOf(0) }
    var habit by rememberSaveable { mutableStateOf("") }
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val habits by vm.habits.collectAsStateWithLifecycle()
    val habitLogs by vm.habitLogs.collectAsStateWithLifecycle()
    val studyMinutes by vm.todayStudyMinutes.collectAsStateWithLifecycle()

    val totalTasks = tasks.size
    val completedToday = tasks.count { it.completed }
    val completion = if (totalTasks == 0) 0f else completedToday.toFloat() / totalTasks
    val visibleTasks = when (taskFilter) {
        1 -> tasks.filterNot { it.completed }
        2 -> tasks.filter { it.completed }
        else -> tasks
    }

    ScreenColumn(modifier) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Make today count", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Text(today, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { ProgressCard("Today's momentum", completion, "$completedToday of $totalTasks tasks complete · $studyMinutes min studied") }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Completed", completedToday.toString(), Modifier.weight(1f))
                MetricCard("Study", "${studyMinutes}m", Modifier.weight(1f))
            }
        }
        item { SectionTitle("To-do list", "Small, clear next actions — sorted by priority") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("High" to 1, "Normal" to 2, "Low" to 3).forEach { (label, value) ->
                    FilterChip(
                        selected = taskPriority == value,
                        onClick = { taskPriority = value },
                        label = { Text(label) }
                    )
                }
            }
        }
        item { QuickAddField("Add a task", task, { task = it }, "Add") { vm.addTask(task, taskPriority); task = ""; taskPriority = 2 } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All" to 0, "Open" to 1, "Done" to 2).forEach { (label, value) ->
                    FilterChip(
                        selected = taskFilter == value,
                        onClick = { taskFilter = value },
                        label = {
                            Text(when (value) {
                                0 -> "$label (${tasks.size})"
                                1 -> "$label (${tasks.count { !it.completed }})"
                                else -> "$label (${tasks.count { it.completed }})"
                            })
                        }
                    )
                }
            }
        }
        if (visibleTasks.isEmpty()) {
            item {
                Text(
                    text = when {
                        tasks.isEmpty() -> "Your list is clear. Add a task when something needs doing."
                        taskFilter == 1 -> "No open tasks. Nice work."
                        else -> "No completed tasks yet."
                    },
                    modifier = Modifier.fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(visibleTasks, key = { it.id }) { item ->
                val priorityLabel = when (item.priority) {
                    1 -> "High priority"
                    3 -> "Low priority"
                    else -> "Normal priority"
                }
                TaskRow(
                    title = item.title,
                    done = item.completed,
                    onToggle = { vm.toggleTask(item) },
                    onDelete = { vm.deleteTask(item) },
                    detail = priorityLabel
                )
            }
        }
        item { SectionTitle("Habits", "Small actions, repeated consistently") }
        item { QuickAddField("Add a habit", habit, { habit = it }, "Add") { vm.addHabit(habit); habit = "" } }
        items(habits, key = { it.id }) { item ->
            val done = habitLogs.any { it.habitId == item.id && it.completedCount > 0 }
            TaskRow(item.name, done, { vm.toggleHabit(item) }, { vm.deleteHabit(item) })
        }
    }
}

@Composable
private fun TaskRow(
    title: String,
    done: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    detail: String? = null
) {
    ElevatedCard(shape = RoundedCornerShape(18.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(if (done) "✓" else "•", color = if (done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, color = if (done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                detail?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            TextButton(onClick = onToggle) { Text(if (done) "Undo" else "Done") }
            TextButton(onClick = onDelete) { Text("×") }
        }
    }
}

@Composable
private fun TopicRow(
    topic: com.muneer.tracker.data.StudyTopicEntity,
    linkedExamNames: List<String>,
    onLink: (com.muneer.tracker.data.ExaminationEntity) -> Unit,
    onUnlink: (com.muneer.tracker.data.TopicExaminationLinkEntity) -> Unit,
    examinations: List<com.muneer.tracker.data.ExaminationEntity>,
    topicExamLinks: List<com.muneer.tracker.data.TopicExaminationLinkEntity>
) {
    val availableExams = examinations.filter { exam ->
        !linkedExamNames.contains(exam.title)
    }
    var showLinkMenu by remember { mutableStateOf(false) }

    ElevatedCard(shape = RoundedCornerShape(16.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(topic.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Status: ${topic.status}  ·  Difficulty: ${topic.difficulty}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { showLinkMenu = true }) { Text("Link Exam") }
            }
            if (linkedExamNames.isNotEmpty()) {
                Text("Linked: ${linkedExamNames.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                linkedExamNames.forEach { examName ->
                    val linkEntity = topicExamLinks.firstOrNull { link ->
                        link.topicId == topic.id && examinations.find { exam -> exam.id == link.examinationId }?.title == examName
                    }
                    linkEntity?.let { link ->
                        TextButton(onClick = { onUnlink(link) }) { Text("Unlink $examName") }
                    }
                }
            }
            if (availableExams.isNotEmpty()) {
                DropdownMenu(
                    expanded = showLinkMenu,
                    onDismissRequest = { showLinkMenu = false }
                ) {
                    availableExams.forEach { exam ->
                        DropdownMenuItem(
                            text = { Text("Link to ${exam.title} (${exam.date})") },
                            onClick = {
                                onLink(exam)
                                showLinkMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyScreen(vm: TrackerViewModel, modifier: Modifier) {
    var subject by rememberSaveable { mutableStateOf("") }
    var session by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("30") }
    var assessment by rememberSaveable { mutableStateOf("") }
    var score by rememberSaveable { mutableStateOf("") }
    var total by rememberSaveable { mutableStateOf("") }
    var noteTitle by rememberSaveable { mutableStateOf("") }
    var noteBody by rememberSaveable { mutableStateOf("") }
    var examTitle by rememberSaveable { mutableStateOf("") }
    var examDate by rememberSaveable { mutableStateOf("") }
    var coverageOverride by rememberSaveable { mutableStateOf("") }
    val subjects by vm.subjects.collectAsStateWithLifecycle()
    val topics by vm.topics.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val assessments by vm.assessments.collectAsStateWithLifecycle()
    val examinations by vm.examinations.collectAsStateWithLifecycle()
    val topicExamLinks by vm.topicExaminationLinks.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()

    ScreenColumn(modifier) {
        item { Text("Study", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) }
        item { Text("Learn → connect → test → validate → correct → remember", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Learning loop", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Record not only what you studied, but what you understood and where you made mistakes.", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        item { SectionTitle("Subjects", "Your active learning map") }
        item { QuickAddField("New subject", subject, { subject = it }, "Add") { vm.addSubject(subject); subject = "" } }
        items(subjects, key = { it.id }) { Text("• ${it.name}", Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)).padding(14.dp), color = MaterialTheme.colorScheme.onSurface) }
        item { SectionTitle("Topics", "Link topics to examinations for syllabus tracking") }
        items(subjects, key = { it.id }) { sub ->
            val subjectTopics = topics.filter { it.subjectId == sub.id }
            if (subjectTopics.isNotEmpty()) {
                subjectTopics.forEach { topic ->
                    val linkedExams = topicExamLinks.filter { it.topicId == topic.id }
                    val linkedExamNames = linkedExams.mapNotNull { link ->
                        examinations.find { it.id == link.examinationId }?.title
                    }
                    val topicStatus = topic.status
                    TopicRow(
                        topic = topic,
                        linkedExamNames = linkedExamNames,
                        onLink = { exam -> vm.linkTopicToExamination(topic.id, exam.id) },
                        onUnlink = { link -> vm.unlinkTopicFromExamination(link.topicId, link.examinationId) },
                        examinations = examinations.filter { it.subjectId == sub.id },
                        topicExamLinks = topicExamLinks
                    )
                }
            } else {
                Text("• No topics for ${sub.name}. Add one below.",
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)).padding(14.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { QuickAddField("New topic for ${subjects.firstOrNull()?.name ?: "subject"}", session, { session = it }, "Add") {
            subjects.firstOrNull()?.let { vm.addTopic(it.id, session); session = "" }
        } }
        item { SectionTitle("Focus session") }
        item {
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(session, { session = it }, Modifier.fillMaxWidth(), label = { Text("What are you working on?") }, shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(minutes, { minutes = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text("Minutes") }, shape = RoundedCornerShape(16.dp))
                    // Exam-aware session logging
                    val subjectTopics = topics.filter { it.subjectId == subjects.firstOrNull()?.id }
                    val linkedTopics = subjectTopics.filter { topic ->
                        topicExamLinks.any { it.topicId == topic.id }
                    }
                    var selectedExamId by rememberSaveable { mutableStateOf<String?>(null) }
                    var showExamDropdown by remember { mutableStateOf(false) }
                    val examOptions = linkedTopics.flatMap { topic ->
                        topicExamLinks.filter { it.topicId == topic.id }.mapNotNull { link ->
                            examinations.find { it.id == link.examinationId }
                        }
                    }.distinctBy { it.id }
                    if (examOptions.isNotEmpty()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                selectedExamId?.let { id -> examinations.find { it.id == id }?.title }
                                    ?: "Link to examination (optional)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = { showExamDropdown = true }) { Text("Select") }
                        }
                        DropdownMenu(
                            expanded = showExamDropdown,
                            onDismissRequest = { showExamDropdown = false }
                        ) {
                            examOptions.forEach { exam ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedExamId = exam.id
                                        showExamDropdown = false
                                    },
                                    text = { Text("${exam.title} (${exam.date})") }
                                )
                            }
                        }
                    }
                    Button(onClick = {
                        vm.addSession(
                            v = session,
                            m = minutes.toIntOrNull() ?: 0,
                            subjectId = subjects.firstOrNull()?.id,
                            examinationId = selectedExamId
                        )
                        session = ""
                    }, enabled = session.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Log session") }
                }
            }
        }
        items(sessions, key = { it.id }) { sess ->
            val examName = sess.examinationId?.let { examId -> examinations.find { it.id == examId }?.title }
            Text("${sess.title}  ·  ${sess.durationMinutes} min${examName?.let { "  ·  Exam: $it" } ?: ""}",
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)).padding(14.dp),
                color = MaterialTheme.colorScheme.onSurface)
        }
        item { SectionTitle("Assessments") }
        item {
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(assessment, { assessment = it }, Modifier.fillMaxWidth(), label = { Text("Assessment") }, shape = RoundedCornerShape(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(score, { score = it }, Modifier.weight(1f), label = { Text("Score") }, shape = RoundedCornerShape(16.dp))
                        OutlinedTextField(total, { total = it }, Modifier.weight(1f), label = { Text("Total") }, shape = RoundedCornerShape(16.dp))
                    }
                    Button(onClick = { vm.addAssessment(assessment, score.toDoubleOrNull() ?: 0.0, total.toDoubleOrNull() ?: 0.0); assessment = ""; score = ""; total = "" }, enabled = assessment.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Save result") }
                }
            }
        }
        items(assessments, key = { it.id }) { Text("${it.title}  ·  ${it.score}/${it.total}", Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)).padding(14.dp), color = MaterialTheme.colorScheme.onSurface) }
        item { SectionTitle("Examinations", "Track upcoming exams and syllabus coverage") }
        item {
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(examTitle, { examTitle = it }, Modifier.fillMaxWidth(), label = { Text("Exam title") }, shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(examDate, { examDate = it.filter { it.isDigit() || it == '-' } }, Modifier.fillMaxWidth(), label = { Text("Date (yyyy-MM-dd)") }, shape = RoundedCornerShape(16.dp))
                    Button(onClick = { vm.addExamination(subjects.firstOrNull()?.id ?: "", examTitle, examDate); examTitle = ""; examDate = "" }, enabled = examTitle.isNotBlank() && examDate.isNotBlank() && subjects.isNotEmpty(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Add exam") }
                }
            }
        }
        items(examinations, key = { it.id }) { exam ->
            // Compute topic-based coverage
            val linkedTopics = topicExamLinks.filter { it.examinationId == exam.id }.mapNotNull { link ->
                topics.find { it.id == link.topicId }
            }
            val completedLinkedTopics = linkedTopics.count { it.status == "COMPLETED" }
            val totalLinkedTopics = linkedTopics.size
            val computedCoverage = if (totalLinkedTopics > 0) {
                (completedLinkedTopics * 100 / totalLinkedTopics)
            } else {
                -1 // unavailable
            }
            val effectiveCoverage = exam.manualCoverageOverride ?: if (computedCoverage >= 0) computedCoverage else exam.syllabusCoverage
            val showComputed = computedCoverage >= 0

            ElevatedCard(shape = RoundedCornerShape(18.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text(exam.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Text(exam.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text(exam.status, fontWeight = FontWeight.Bold, color = when (exam.status) { "COMPLETED" -> MaterialTheme.colorScheme.primary; "IN_PROGRESS" -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.onSurfaceVariant })
                    }
                    // Effective coverage progress
                    LinearProgressIndicator(progress = { (effectiveCoverage / 100f).coerceIn(0f, 1f) }, Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                    // Coverage breakdown
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Effective: ${effectiveCoverage}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(when {
                                exam.manualCoverageOverride != null -> "Manual override active"
                                showComputed -> "From ${completedLinkedTopics}/$totalLinkedTopics topics"
                                else -> "Manual entry (no linked topics)"
                            }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (showComputed && exam.manualCoverageOverride != null) {
                            Text("Computed: ${computedCoverage}%  ·  Manual baseline: ${exam.syllabusCoverage}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else if (showComputed) {
                            Text("Computed from linked topics: ${computedCoverage}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Text("Manual entry: ${exam.syllabusCoverage}%  ·  Link topics to enable computed coverage", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    // Manual coverage override
                    var coverageText by remember { mutableStateOf(exam.manualCoverageOverride?.toString() ?: "") }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = coverageText,
                            onValueChange = { coverageText = it.filter { it.isDigit() } },
                            label = { Text("Manual override (0-100, empty=auto)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TextButton(onClick = {
                            val value = coverageText.toIntOrNull()
                            if (value != null && value in 0..100) {
                                vm.setExaminationManualCoverage(exam, value)
                                coverageText = ""
                            } else if (coverageText.isBlank()) {
                                vm.setExaminationManualCoverage(exam, null)
                            }
                        }) {
                            Text(if (coverageText.isBlank()) "Clear" else "Set")
                        }
                        TextButton(onClick = { vm.setExaminationManualCoverage(exam, null); coverageText = "" }) { Text("Auto") }
                    }
                    // Manual syllabus coverage (baseline)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { vm.updateExamination(exam.copy(status = "IN_PROGRESS", syllabusCoverage = (exam.syllabusCoverage + 10).coerceAtMost(100))) }) { Text("Baseline +10%") }
                        TextButton(onClick = { vm.updateExamination(exam.copy(status = if (exam.syllabusCoverage >= 100) "COMPLETED" else "IN_PROGRESS", syllabusCoverage = (exam.syllabusCoverage - 10).coerceAtLeast(0))) }) { Text("Baseline -10%") }
                        TextButton(onClick = { vm.deleteExamination(exam) }) { Text("Delete") }
                    }
                }
            }
        }
        item { SectionTitle("Knowledge notes", "Capture mistakes and insights while they are fresh") }
        item {
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(noteTitle, { noteTitle = it }, Modifier.fillMaxWidth(), label = { Text("Note title") }, shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(noteBody, { noteBody = it }, Modifier.fillMaxWidth(), minLines = 4, label = { Text("What did I learn / get wrong?") }, shape = RoundedCornerShape(16.dp))
                    Button(onClick = { vm.addNote(noteTitle, noteBody); noteTitle = ""; noteBody = "" }, enabled = noteTitle.isNotBlank() && noteBody.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Save note") }
                }
            }
        }
        items(notes, key = { it.id }) { Text("${it.title}: ${it.content}", Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)).padding(14.dp), color = MaterialTheme.colorScheme.onSurface) }
    }
}

@Composable
private fun GoalsScreen(vm: TrackerViewModel, modifier: Modifier) {
    var goal by rememberSaveable { mutableStateOf("") }
    var project by rememberSaveable { mutableStateOf("") }
    val goals by vm.goals.collectAsStateWithLifecycle()
    val projects by vm.projects.collectAsStateWithLifecycle()
    val completedTasks by vm.completedTasks.collectAsStateWithLifecycle()
    val studyMinutes by vm.totalStudyMinutes.collectAsStateWithLifecycle()
    val journalCount by vm.journalCount.collectAsStateWithLifecycle()
    val reviews by vm.weeklyReviews.collectAsStateWithLifecycle()

    ScreenColumn(modifier) {
        item { Text("Goals & Projects", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) }
        item { Text("Turn long-term plans into visible progress.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Tasks", completedTasks.toString(), Modifier.weight(1f))
                MetricCard("Study", "${studyMinutes}m", Modifier.weight(1f))
                MetricCard("Journal", journalCount.toString(), Modifier.weight(1f))
            }
        }
        item { SectionTitle("Goals") }
        item { QuickAddField("New goal", goal, { goal = it }, "Add") { vm.addGoal(goal); goal = "" } }
        items(goals, key = { it.id }) { g ->
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(g.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Text("${g.progress}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                    LinearProgressIndicator(progress = { (g.progress / 100f).coerceIn(0f, 1f) }, Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { TextButton({ vm.updateGoal(g, (g.progress + 10).coerceAtMost(100)) }) { Text("+10") }; TextButton({ vm.updateGoal(g, (g.progress - 10).coerceAtLeast(0)) }) { Text("-10") }; TextButton({ vm.deleteGoal(g) }) { Text("Delete") } }
                }
            }
        }
        item { SectionTitle("Projects") }
        item { QuickAddField("New project", project, { project = it }, "Add") { vm.addProject(project); project = "" } }
        items(projects, key = { it.id }) { p ->
            ElevatedCard(shape = RoundedCornerShape(18.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(p.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface); Text(p.status, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
                    TextButton({ vm.deleteProject(p) }) { Text("Delete") }
                }
            }
        }
        item { SectionTitle("Weekly review", "Close the loop every week") }
        item { WeeklyReviewCard(vm) }
        items(reviews.take(4), key = { it.id }) { r ->
            ElevatedCard(shape = RoundedCornerShape(18.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) { Text(r.weekStart, fontWeight = FontWeight.Bold); Text("Win: ${r.wins}"); Text("Blocker: ${r.blockers}"); Text("Next: ${r.nextFocus}") }
            }
        }
    }
}

@Composable
private fun WeeklyReviewCard(vm: TrackerViewModel) {
    val today by vm.todayDate.collectAsStateWithLifecycle()
    var wins by rememberSaveable { mutableStateOf("") }
    var blockers by rememberSaveable { mutableStateOf("") }
    var next by rememberSaveable { mutableStateOf("") }
    ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            OutlinedTextField(wins, { wins = it }, Modifier.fillMaxWidth(), label = { Text("What went well?") }, shape = RoundedCornerShape(16.dp))
            OutlinedTextField(blockers, { blockers = it }, Modifier.fillMaxWidth(), label = { Text("What blocked me?") }, shape = RoundedCornerShape(16.dp))
            OutlinedTextField(next, { next = it }, Modifier.fillMaxWidth(), label = { Text("Next focus") }, shape = RoundedCornerShape(16.dp))
            Button(onClick = { vm.addWeeklyReview(today, wins, blockers, next); wins = ""; blockers = ""; next = "" }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Save review") }
        }
    }
}

@Composable
private fun JournalScreen(vm: TrackerViewModel, modifier: Modifier) {
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    val journal by vm.journal.collectAsStateWithLifecycle()
    ScreenColumn(modifier) {
        item { Text("Journal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) }
        item { Text("Think clearly. Record what matters. Learn from the week.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item {
            ElevatedCard(shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Title") }, shape = RoundedCornerShape(16.dp))
                    OutlinedTextField(body, { body = it }, Modifier.fillMaxWidth(), minLines = 7, label = { Text("What happened today?") }, shape = RoundedCornerShape(16.dp))
                    Text(
                        "A title and entry text are both required.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            vm.addJournal(title.trim(), body.trim())
                            title = ""
                            body = ""
                        },
                        enabled = title.isNotBlank() && body.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Save entry") }
                }
            }
        }
        items(journal, key = { it.id }) { entry ->
            ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(if (entry.title.isBlank()) entry.date else entry.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(entry.content, color = MaterialTheme.colorScheme.onSurface)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text(entry.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); TextButton({ vm.deleteJournal(entry) }) { Text("Delete") } }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    vm: TrackerViewModel,
    modifier: Modifier,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImport by remember { mutableStateOf<String?>(null) }
    var showRestoreConfirm by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching { context.contentResolver.openInputStream(uri)?.use { it.reader(Charsets.UTF_8).readText() } ?: error("Could not open backup") }
            .onSuccess { pendingImport = it; showRestoreConfirm = true }
            .onFailure { error = it.message ?: "Could not read backup" }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri -> pendingUri = uri }
    LaunchedEffect(pendingUri) {
        val uri = pendingUri ?: return@LaunchedEffect
        busy = true
        runCatching { context.contentResolver.openOutputStream(uri)?.use { out -> out.writer(Charsets.UTF_8).use { it.write(vm.createBackupJson()) } } ?: error("Could not write backup") }
            .onSuccess { Toast.makeText(context, "Backup exported", Toast.LENGTH_SHORT).show() }
            .onFailure { error = it.message ?: "Export failed" }
        busy = false; pendingUri = null
    }
    LaunchedEffect(showRestoreConfirm, pendingImport) {
        if (!showRestoreConfirm) return@LaunchedEffect
    }

    ScreenColumn(modifier) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface) }
        item { Text("Keep your data local, portable and under your control.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        item {
            ElevatedCard(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Appearance", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            if (isDarkTheme) "Dark mode · Midnight slate" else "Light mode · Soft contrast",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isDarkTheme, onCheckedChange = onThemeChange)
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(22.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Data & backup", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Export a portable JSON backup. Importing replaces the current local data after confirmation.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) }, enabled = !busy, shape = RoundedCornerShape(16.dp)) { Text("Import") }
                        Button(onClick = { exportLauncher.launch("tracker-backup-${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}.json") }, enabled = !busy, shape = RoundedCornerShape(16.dp)) { Text(if (busy) "Preparing…" else "Export") }
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
        item {
            ElevatedCard(shape = RoundedCornerShape(22.dp), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Tracker v3", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Offline-first personal operating system · Room database · Jetpack Compose", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false; pendingImport = null },
            title = { Text("Restore backup?") },
            text = { Text("This replaces the current local Tracker data. Export a fresh backup before continuing if you need to preserve it.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val json = pendingImport
                        if (json != null && !busy) {
                            scope.launch {
                                busy = true
                                try {
                                    vm.restoreBackupJson(json)
                                    Toast.makeText(context, "Backup restored", Toast.LENGTH_SHORT).show()
                                    error = null
                                    pendingImport = null
                                    showRestoreConfirm = false
                                } catch (e: Exception) {
                                    error = e.message ?: "Restore failed"
                                } finally {
                                    busy = false
                                }
                            }
                        }
                    },
                    enabled = !busy
                ) { Text(if (busy) "Restoring…" else "Restore") }
            },
            dismissButton = { TextButton(onClick = { pendingImport = null; showRestoreConfirm = false }) { Text("Cancel") } }
        )
    }
}
