package com.muneer.tracker.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.muneer.tracker.TrackerViewModel
import com.muneer.tracker.data.*

@Composable fun TrackerApp(vm:TrackerViewModel){
 var tab by remember{mutableStateOf(0)}
 val tabs=listOf("Today","Study","Goals","Journal")
 Scaffold(topBar={TopAppBar(title={Text("Tracker")})},bottomBar={NavigationBar{tabs.forEachIndexed{i,l->NavigationBarItem(selected=tab==i,onClick={tab=i},icon={},label={Text(l)})}}}){p->when(tab){0->Today(vm,Modifier.padding(p));1->Study(vm,Modifier.padding(p));2->Goals(vm,Modifier.padding(p));else->Journal(vm,Modifier.padding(p))}}}

@Composable private fun Today(vm:TrackerViewModel,m:Modifier){
 var task by remember{mutableStateOf("")};var activity by remember{mutableStateOf("")}
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  item{Text("Today",style=MaterialTheme.typography.headlineSmall);Text(vm.today,style=MaterialTheme.typography.labelMedium)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(task,{task=it},Modifier.weight(1f),label={Text("New task")});Button({vm.addTask(task);task=""}){Text("Add")}}}
  item{Text("Tasks",style=MaterialTheme.typography.titleLarge)}
  items(vm.tasks){x->ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(if(x.completed)"✓ "+x.title else x.title);TextButton({vm.toggleTask(x)}){Text(if(x.completed)"Undo" else "Done")}}}}
  item{Text("Habits",style=MaterialTheme.typography.titleLarge)}
  items(vm.habits){x->val done=vm.habitLogs.any{it.habitId==x.id&&it.completedCount>0};ElevatedCard(Modifier.fillMaxWidth()){Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(if(done)"✓ "+x.name else x.name);TextButton({vm.toggleHabit(x)}){Text(if(done)"Undo" else "Done")}}}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(activity,{activity=it},Modifier.weight(1f),label={Text("Log activity")});Button({vm.addActivity(activity);activity=""}){Text("Log")}}}
  item{Text("Activity",style=MaterialTheme.typography.titleLarge)}
  items(vm.activities){x->Text("• "+x.title)}
 }}
@Composable private fun Study(vm:TrackerViewModel,m:Modifier){
 var subject by remember{mutableStateOf("")};var session by remember{mutableStateOf("")};var minutes by remember{mutableStateOf("30")};var nt by remember{mutableStateOf("")};var nb by remember{mutableStateOf("")}
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Study",style=MaterialTheme.typography.headlineSmall)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(subject,{subject=it},Modifier.weight(1f),label={Text("New subject")});Button({vm.addSubject(subject);subject=""}){Text("Add")}}}
  items(vm.subjects){x->Text("• "+x.name)}
  item{Text("Log session",style=MaterialTheme.typography.titleLarge)}
  item{OutlinedTextField(session,{session=it},Modifier.fillMaxWidth(),label={Text("Session title")});OutlinedTextField(minutes,{minutes=it.filter(Char::isDigit)},Modifier.fillMaxWidth(),label={Text("Minutes")});Button({vm.addSession(session,minutes.toIntOrNull()?:0);session=""}){Text("Save session")}}
  items(vm.sessions.take(20)){x->Text("• "+x.title+" — "+x.durationMinutes+" min")}
  item{Text("Knowledge notes",style=MaterialTheme.typography.titleLarge)}
  item{OutlinedTextField(nt,{nt=it},Modifier.fillMaxWidth(),label={Text("Note title")});OutlinedTextField(nb,{nb=it},Modifier.fillMaxWidth(),minLines=3,label={Text("Active recall / knowledge")});Button({vm.addNote(nt,nb);nt="";nb=""}){Text("Save note")}}
  items(vm.notes.take(10)){x->Text("• "+x.title+": "+x.content.take(120))}
 }}
@Composable private fun Goals(vm:TrackerViewModel,m:Modifier){
 var goal by remember{mutableStateOf("")};var project by remember{mutableStateOf("")}
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Goals & Projects",style=MaterialTheme.typography.headlineSmall)}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(goal,{goal=it},Modifier.weight(1f),label={Text("Goal")});Button({vm.addGoal(goal);goal=""}){Text("Add")}}}
  items(vm.goals){x->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(x.title,style=MaterialTheme.typography.titleMedium);LinearProgressIndicator(progress={x.progress/100f},Modifier.fillMaxWidth());Text(x.progress.toString()+"%")}}}
  item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(project,{project=it},Modifier.weight(1f),label={Text("Project")});Button({vm.addProject(project);project=""}){Text("Add")}}}
  items(vm.projects){x->Text("• "+x.name+" — "+x.status)}
 }}
@Composable private fun Journal(vm:TrackerViewModel,m:Modifier){
 var title by remember{mutableStateOf("")};var body by remember{mutableStateOf("")}
 LazyColumn(m.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
  item{Text("Journal",style=MaterialTheme.typography.headlineSmall)}
  item{OutlinedTextField(title,{title=it},Modifier.fillMaxWidth(),label={Text("Title")})}
  item{OutlinedTextField(body,{body=it},Modifier.fillMaxWidth(),minLines=5,label={Text("What happened today?")})}
  item{Button({vm.addJournal(title,body);title="";body=""}){Text("Save entry")}}
  items(vm.journal){x->ElevatedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(if(x.title.isBlank())x.date else x.title,style=MaterialTheme.typography.titleMedium);Text(x.content);Text(x.date,style=MaterialTheme.typography.labelSmall)}}}
 }}
