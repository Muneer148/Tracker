package com.muneer.tracker
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.muneer.tracker.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TrackerViewModel(app:Application):AndroidViewModel(app){
 private val dao=TrackerDatabase.get(app).trackerDao()
 private val fmt=SimpleDateFormat("yyyy-MM-dd",Locale.US)
 val today=fmt.format(Date())
 val todayDayId=UUID.nameUUIDFromBytes(today.toByteArray()).toString()
 val tasks=dao.tasksForDay(todayDayId).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val habits=dao.activeHabits().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val habitLogs=dao.habitLogsForDate(today).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val activities=dao.activitiesForDate(today).stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val goals=dao.goals().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val projects=dao.projects().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val journal=dao.journal().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val subjects=dao.studySubjects().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val topics=dao.studyTopics().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val sessions=dao.studySessions().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val assessments=dao.assessments().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val notes=dao.knowledgeNotes().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 init{viewModelScope.launch{dao.upsertDay(DayEntity(id=todayDayId,date=today));dao.insertHabit(HabitEntity(name="Study / learning"));dao.insertHabit(HabitEntity(name="Exercise"));dao.insertHabit(HabitEntity(name="Sleep on time"))}}
 fun addTask(v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertTask(TaskEntity(dayId=todayDayId,title=v.trim()))}
 fun toggleTask(v:TaskEntity)=viewModelScope.launch{dao.updateTask(v.copy(completed=!v.completed,updatedAt=System.currentTimeMillis()))}
 fun toggleHabit(v:HabitEntity)=viewModelScope.launch{val old=habitLogs.value.firstOrNull{it.habitId==v.id};dao.upsertHabitLog(HabitLogEntity(v.id,today,if((old?.completedCount?:0)>0)0 else 1))}
 fun addActivity(v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertActivity(ActivityEntity(date=today,type="GENERAL",title=v.trim()))}
 fun addGoal(v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertGoal(GoalEntity(title=v.trim()))}
 fun updateGoal(v:GoalEntity,p:Int)=viewModelScope.launch{dao.updateGoal(v.copy(progress=p.coerceIn(0,100),updatedAt=System.currentTimeMillis()))}
 fun addProject(v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertProject(ProjectEntity(name=v.trim()))}
 fun addJournal(t:String,c:String)=viewModelScope.launch{if(t.isNotBlank()||c.isNotBlank())dao.insertJournal(JournalEntryEntity(date=today,title=t.trim(),content=c.trim()))}
 fun addSubject(v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertSubject(StudySubjectEntity(name=v.trim()))}
 fun addTopic(subjectId:String,v:String)=viewModelScope.launch{if(v.isNotBlank())dao.insertTopic(StudyTopicEntity(subjectId=subjectId,name=v.trim()))}
 fun addSession(v:String,m:Int)=viewModelScope.launch{if(v.isNotBlank()&&m>0)dao.insertStudySession(StudySessionEntity(date=today,subjectId=null,topicId=null,title=v.trim(),durationMinutes=m))}
 fun addAssessment(v:String,s:Double,t:Double)=viewModelScope.launch{if(v.isNotBlank()&&t>0)dao.insertAssessment(AssessmentEntity(date=today,subjectId=null,title=v.trim(),score=s,total=t))}
 fun addNote(t:String,c:String)=viewModelScope.launch{if(t.isNotBlank()&&c.isNotBlank())dao.insertKnowledgeNote(KnowledgeNoteEntity(topicId=null,title=t.trim(),content=c.trim()))}
}