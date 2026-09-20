package com.muneer.tracker.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muneer.tracker.TrackerViewModel
import com.muneer.tracker.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun TrackerApp(vm:TrackerViewModel){
 var tab by remember{mutableStateOf(0)}
 val tabs=listOf("Today","Study","Goals","Journal","Settings")
 Scaffold(topBar={TopAppBar(title={Text("Tracker")})},bottomBar={NavigationBar{tabs.forEachIndexed{i,l->NavigationBarItem(selected=tab==i,onClick={tab=i},icon={},label={Text(l)})}}}){p->when(tab){0->Today(vm,Modifier.padding(p));1->Study(vm,Modifier.padding(p));2->Goals(vm,Modifier.padding(p));3->Journal(vm,Modifier.padding(p));else->Settings(vm,Modifier.padding(p))}}}

@Composable private fun Today(vm:TrackerViewModel,m:Modifier){
 var task by remember{mutableStateOf("")};var habit by remember{mutableStateOf("")};var activity by remember{mutableStateOf("")}
 val tasks by vm.tasks.collectAsStateWithLifecycle()
 val habits by vm.habits.collectAsStateWithLifecycle()
 val habitLogs by vm.habitLogs.collectAsStateWithLifecycle()
 val activities by vm.activities.collectAsStateWithLifecycle()
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text("Today",style=MaterialTheme.typography.headlineSmall);Text(vm.today,style=MaterialTheme.typography.labelMedium)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(task,{task=it},Modifier.weight(1f),label={Text("New task")});Button({vm.addTask(task);task=""}){Text("Add")}}}
  item{Text("Tasks",style=MaterialTheme.typography.titleLarge)}
  items(tasks){x->ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(if(x.completed)"✓ "+x.title else x.title);Row{TextButton({vm.toggleTask(x)}){Text(if(x.completed)"Undo" else "Done")};TextButton({vm.deleteTask(x)}){Text("Delete")}}}}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(habit,{habit=it},Modifier.weight(1f),label={Text("New habit")});Button({vm.addHabit(habit);habit=""}){Text("Add")}}}
  item{Text("Habits",style=MaterialTheme.typography.titleLarge)}
  items(habits){x->val done=habitLogs.any{it.habitId==x.id&&it.completedCount>0};ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(if(done)"✓ "+x.name else x.name);Row{TextButton({vm.toggleHabit(x)}){Text(if(done)"Undo" else "Done")};TextButton({vm.deleteHabit(x)}){Text("Delete")}}}}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(activity,{activity=it},Modifier.weight(1f),label={Text("Log activity")});Button({vm.addActivity(activity);activity=""}){Text("Log")}}}
  item{Text("Activity",style=MaterialTheme.typography.titleLarge)}
  items(activities){x->Text("• "+x.title)}
 }}
@Composable private fun Study(vm:TrackerViewModel,m:Modifier){
 var subject by remember{mutableStateOf("")};var session by remember{mutableStateOf("")};var minutes by remember{mutableStateOf("30")};var nt by remember{mutableStateOf("")};var nb by remember{mutableStateOf("")};var assessment by remember{mutableStateOf("")};var score by remember{mutableStateOf("")};var total by remember{mutableStateOf("")}
 val subjects by vm.subjects.collectAsStateWithLifecycle()
 val sessions by vm.sessions.collectAsStateWithLifecycle()
 val assessments by vm.assessments.collectAsStateWithLifecycle()
 val notes by vm.notes.collectAsStateWithLifecycle()
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Study",style=MaterialTheme.typography.headlineSmall)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(subject,{subject=it},Modifier.weight(1f),label={Text("New subject")});Button({vm.addSubject(subject);subject=""}){Text("Add")}}}
  items(subjects){x->Text("• "+x.name)}
  item{Text("Log session",style=MaterialTheme.typography.titleLarge)}
  item{OutlinedTextField(session,{session=it},Modifier.fillMaxWidth(),label={Text("Session title")});OutlinedTextField(minutes,{minutes=it.filter(Char::isDigit)},Modifier.fillMaxWidth(),label={Text("Minutes")});Button({vm.addSession(session,minutes.toIntOrNull()?:0);session=""}){Text("Save session")}}
  items(sessions.take(20)){x->Text("• "+x.title+" — "+x.durationMinutes+" min")}
  item{Text("Assessments",style=MaterialTheme.typography.titleLarge)}
  item{OutlinedTextField(assessment,{assessment=it},Modifier.fillMaxWidth(),label={Text("Assessment name")});Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(score,{score=it},Modifier.weight(1f),label={Text("Score")});OutlinedTextField(total,{total=it},Modifier.weight(1f),label={Text("Total")})};Button({vm.addAssessment(assessment,score.toDoubleOrNull()?:0.0,total.toDoubleOrNull()?:0.0);assessment="";score="";total=""}){Text("Save assessment")}}
  items(assessments.take(10)){x->Text("• "+x.title+" — "+x.score+"/"+x.total)}
  item{Text("Knowledge notes",style=MaterialTheme.typography.titleLarge)}
  item{OutlinedTextField(nt,{nt=it},Modifier.fillMaxWidth(),label={Text("Note title")});OutlinedTextField(nb,{nb=it},Modifier.fillMaxWidth(),minLines=3,label={Text("Active recall / knowledge")});Button({vm.addNote(nt,nb);nt="";nb=""}){Text("Save note")}}
  items(notes.take(10)){x->Text("• "+x.title+": "+x.content.take(120))}
 }}
@Composable private fun Goals(vm:TrackerViewModel,m:Modifier){
 var goal by remember{mutableStateOf("")};var project by remember{mutableStateOf("")};var wins by remember{mutableStateOf("")};var blockers by remember{mutableStateOf("")};var next by remember{mutableStateOf("")}
 val goals by vm.goals.collectAsStateWithLifecycle()
 val projects by vm.projects.collectAsStateWithLifecycle()
 val completedTasks by vm.completedTasks.collectAsStateWithLifecycle()
 val totalStudyMinutes by vm.totalStudyMinutes.collectAsStateWithLifecycle()
 val journalCount by vm.journalCount.collectAsStateWithLifecycle()
 val weeklyReviews by vm.weeklyReviews.collectAsStateWithLifecycle()
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Goals & Projects",style=MaterialTheme.typography.headlineSmall)}
  item{Text("Personal analytics",style=MaterialTheme.typography.titleLarge);Text("Completed tasks: "+completedTasks);Text("Study time: "+totalStudyMinutes+" min");Text("Journal entries: "+journalCount)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(goal,{goal=it},Modifier.weight(1f),label={Text("Goal")});Button({vm.addGoal(goal);goal=""}){Text("Add")}}}
  items(goals){x->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(x.title,style=MaterialTheme.typography.titleMedium);LinearProgressIndicator(progress={x.progress/100f},Modifier.fillMaxWidth());Row{Text(x.progress.toString()+"%");TextButton({vm.updateGoal(x,x.progress+10)}){Text("+10")};TextButton({vm.updateGoal(x,x.progress-10)}){Text("-10")};TextButton({vm.deleteGoal(x)}){Text("Delete")}}}}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(project,{project=it},Modifier.weight(1f),label={Text("Project")});Button({vm.addProject(project);project=""}){Text("Add")}}}
  items(projects){x->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("• "+x.name+" — "+x.status);TextButton({vm.deleteProject(x)}){Text("Delete")}}}
  item{Text("Weekly review",style=MaterialTheme.typography.titleLarge);OutlinedTextField(wins,{wins=it},Modifier.fillMaxWidth(),label={Text("Wins")});OutlinedTextField(blockers,{blockers=it},Modifier.fillMaxWidth(),label={Text("Blockers")});OutlinedTextField(next,{next=it},Modifier.fillMaxWidth(),label={Text("Next focus")});Button({vm.addWeeklyReview(vm.today,wins,blockers,next);wins="";blockers="";next=""}){Text("Save review")}}
  items(weeklyReviews.take(4)){x->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(x.weekStart,style=MaterialTheme.typography.titleMedium);Text("Wins: "+x.wins);Text("Blockers: "+x.blockers);Text("Next: "+x.nextFocus)}}}
 }}
@Composable private fun Journal(vm:TrackerViewModel,m:Modifier){
 var title by remember{mutableStateOf("")};var body by remember{mutableStateOf("")}
 val journal by vm.journal.collectAsStateWithLifecycle()
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Journal",style=MaterialTheme.typography.headlineSmall)}
  item{OutlinedTextField(title,{title=it},Modifier.fillMaxWidth(),label={Text("Title")})}
  item{OutlinedTextField(body,{body=it},Modifier.fillMaxWidth(),minLines=5,label={Text("What happened today?")})}
  item{Button({vm.addJournal(title,body);title="";body=""}){Text("Save entry")}}
  items(journal){x->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(if(x.title.isBlank())x.date else x.title,style=MaterialTheme.typography.titleMedium);Text(x.content);Text(x.date,style=MaterialTheme.typography.labelSmall);TextButton({vm.deleteJournal(x)}){Text("Delete")}}}}
 }}

@Composable
private fun Settings(vm: TrackerViewModel, m: Modifier) {
    val context = LocalContext.current
    var exporting by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) {
            exporting = false
            return@rememberLauncherForActivityResult
        }
        LaunchedEffectKey.export(context, vm, uri) { exporting = false }
    }

    LazyColumn(
        m.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineSmall)
            Text("Data", style = MaterialTheme.typography.titleLarge)
            Text(
                "Export a portable JSON backup of all Tracker data. Keep the file somewhere secure because it may contain journal and study content.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            Button(
                enabled = !exporting,
                onClick = {
                    exporting = true
                    val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                        .format(java.util.Date())
                    launcher.launch("tracker-backup-$date.json")
                }
            ) {
                Text(if (exporting) "Preparing…" else "Export Backup")
            }
        }
        item {
            Text(
                "Restore/import will be added after export validation and recovery tests.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private object LaunchedEffectKey {
    @Composable
    fun export(
        context: android.content.Context,
        vm: TrackerViewModel,
        uri: android.net.Uri,
        onDone: () -> Unit
    ) {
        LaunchedEffect(uri) {
            runCatching {
                val json = vm.createBackupJson()
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.writer(Charsets.UTF_8).use { it.write(json) }
                } ?: error("Could not open the selected file.")
            }.onSuccess {
                Toast.makeText(context, "Backup exported successfully.", Toast.LENGTH_LONG).show()
            }.onFailure {
                Toast.makeText(context, "Backup export failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
            onDone()
        }
    }
}
