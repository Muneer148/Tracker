package com.muneer.tracker.planner

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muneer.tracker.TrackerViewModel
import com.muneer.tracker.data.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

private val PlannerInk = Color(0xFF171927)
private val PlannerPrimary = Color(0xFF5B5BD6)
private val PlannerMint = Color(0xFF159A83)
private val PlannerBg = Color(0xFFF6F7FB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(vm: TrackerViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { ScheduleStore(context.applicationContext) }
    var schedules by remember { mutableStateOf(store.all()) }
    var title by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var duration by rememberSaveable { mutableStateOf("45") }
    var goal by rememberSaveable { mutableStateOf("") }
    var selectedTaskId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAt by remember { mutableLongStateOf(System.currentTimeMillis() + 30 * 60_000L) }
    var showTaskMenu by remember { mutableStateOf(false) }
    var activeTimer by remember { mutableStateOf<ScheduleItem?>(null) }
    var remainingSeconds by remember(activeTimer?.id) { mutableLongStateOf((activeTimer?.durationMinutes ?: 0) * 60L) }
    var timerRunning by remember(activeTimer?.id) { mutableStateOf(false) }
    val tasks by vm.tasks.collectAsStateWithLifecycle()

    LaunchedEffect(activeTimer?.id, timerRunning) {
        while (timerRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (timerRunning && remainingSeconds <= 0) {
            timerRunning = false
            activeTimer?.taskId?.let { id -> tasks.firstOrNull { it.id == id }?.let { task -> if (!task.completed) vm.toggleTask(task) } }
        }
    }

    fun requestNotifications() {
        if (android.os.Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            (context as? Activity)?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 7001) }
        }
    }

    fun scheduleReminder(item: ScheduleItem) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleNotificationReceiver::class.java).apply {
            putExtra("title", item.title)
            putExtra("notes", item.notes.ifBlank { "Scheduled for ${formatDateTime(item.triggerAt)}" })
            putExtra("notificationId", item.id.hashCode())
        }
        val pending = PendingIntent.getBroadcast(context, item.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (item.triggerAt > System.currentTimeMillis()) alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, item.triggerAt, pending)
    }

    Scaffold(
        modifier = modifier,
        containerColor = PlannerBg,
        floatingActionButton = {
            FloatingActionButton(onClick = { requestNotifications() }, containerColor = PlannerPrimary, contentColor = Color.White) { Text("🔔") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Planner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PlannerInk)
                    Text("Decide when the work happens — then let Tracker remind you.", color = Color(0xFF6C7180))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(18.dp), color = Color.White, modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Today", style = MaterialTheme.typography.labelMedium, color = Color(0xFF6C7180))
                            Text(formatDay(System.currentTimeMillis()), fontWeight = FontWeight.Bold, color = PlannerInk)
                        }
                    }
                    Surface(shape = RoundedCornerShape(18.dp), color = PlannerPrimary, modifier = Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Scheduled", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = .75f))
                            Text(schedules.count { it.triggerAt >= startOfToday() }.toString(), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
            activeTimer?.let { item ->
                item {
                    ElevatedCard(shape = RoundedCornerShape(26.dp), colors = CardDefaults.elevatedCardColors(containerColor = PlannerInk)) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("FOCUS TIMER", style = MaterialTheme.typography.labelMedium, color = Color(0xFFA9ABBA))
                            Text(item.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(formatTimer(remainingSeconds), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { timerRunning = !timerRunning }, colors = ButtonDefaults.buttonColors(containerColor = PlannerMint)) { Text(if (timerRunning) "Pause" else "Start") }
                                OutlinedButton(onClick = { timerRunning = false; activeTimer = null }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Close") }
                            }
                        }
                    }
                }
            }
            item {
                ElevatedCard(shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Schedule a session", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlannerInk)
                        OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("What are you going to do?") }, singleLine = true, shape = RoundedCornerShape(16.dp))
                        OutlinedTextField(goal, { goal = it }, Modifier.fillMaxWidth(), label = { Text("Goal / context (optional)") }, singleLine = true, shape = RoundedCornerShape(16.dp))
                        OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text("What should the reminder tell you?") }, minLines = 2, shape = RoundedCornerShape(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                val c = Calendar.getInstance().apply { timeInMillis = selectedAt }
                                DatePickerDialog(context, { _, y, m, d -> c.set(y, m, d); selectedAt = c.timeInMillis }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                            }, modifier = Modifier.weight(1f)) { Text(SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date(selectedAt))) }
                            OutlinedButton(onClick = {
                                val c = Calendar.getInstance().apply { timeInMillis = selectedAt }
                                TimePickerDialog(context, { _, h, m -> c.set(Calendar.HOUR_OF_DAY, h); c.set(Calendar.MINUTE, m); c.set(Calendar.SECOND, 0); selectedAt = c.timeInMillis }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show()
                            }, modifier = Modifier.weight(1f)) { Text(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(selectedAt))) }
                        }
                        OutlinedTextField(duration, { duration = it.filter(Char::isDigit) }, Modifier.fillMaxWidth(), label = { Text("Focus duration (minutes)") }, singleLine = true, shape = RoundedCornerShape(16.dp))
                        Box {
                            OutlinedButton(onClick = { showTaskMenu = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(tasks.firstOrNull { it.id == selectedTaskId }?.title ?: "Link an existing task (optional)")
                            }
                            DropdownMenu(expanded = showTaskMenu, onDismissRequest = { showTaskMenu = false }) {
                                tasks.take(20).forEach { task ->
                                    DropdownMenuItem(text = { Text(task.title) }, onClick = { selectedTaskId = task.id; showTaskMenu = false })
                                }
                                DropdownMenuItem(text = { Text("No linked task") }, onClick = { selectedTaskId = null; showTaskMenu = false })
                            }
                        }
                        Button(
                            onClick = {
                                val item = ScheduleItem(title = title.trim(), notes = notes.trim(), triggerAt = selectedAt, durationMinutes = duration.toIntOrNull()?.coerceIn(1, 240) ?: 45, taskId = selectedTaskId, goal = goal.trim())
                                store.upsert(item); scheduleReminder(item); schedules = store.all(); requestNotifications(); title = ""; notes = ""; goal = ""; selectedTaskId = null
                            },
                            enabled = title.isNotBlank() && selectedAt > System.currentTimeMillis(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Schedule + reminder") }
                    }
                }
            }
            item { Text("Timeline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PlannerInk) }
            items(schedules, key = { it.id }) { item ->
                val overdue = item.triggerAt < System.currentTimeMillis()
                ElevatedCard(shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                Text(formatDateTime(item.triggerAt), style = MaterialTheme.typography.labelMedium, color = if (overdue) Color(0xFFB42318) else PlannerPrimary)
                                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PlannerInk)
                            }
                            Text("${item.durationMinutes}m", fontWeight = FontWeight.Bold, color = PlannerMint)
                        }
                        if (item.goal.isNotBlank()) Text("Goal · ${item.goal}", color = Color(0xFF6C7180))
                        if (item.notes.isNotBlank()) Text(item.notes, color = Color(0xFF4F5563))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { activeTimer = item; remainingSeconds = item.durationMinutes * 60L; timerRunning = false }, shape = RoundedCornerShape(14.dp)) { Text("Start timer") }
                            TextButton(onClick = { store.delete(item.id); schedules = store.all() }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDay(time: Long) = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date(time))
private fun formatDateTime(time: Long) = SimpleDateFormat("EEE, dd MMM · hh:mm a", Locale.getDefault()).format(Date(time))
private fun formatTimer(seconds: Long) = "%02d:%02d".format(seconds / 60, seconds % 60)
private fun startOfToday(): Long = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
