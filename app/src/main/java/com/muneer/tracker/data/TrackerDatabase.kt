package com.muneer.tracker.data
import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity(tableName="days", indices=[Index(value=["date"], unique=true)])
data class DayEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val date:String,val note:String="",val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="tasks",indices=[Index("dayId"),Index("projectId")])
data class TaskEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val dayId:String,val title:String,val description:String="",val priority:Int=2,val completed:Boolean=false,val dueAt:Long?=null,val projectId:String?=null,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="habits",indices=[Index(value=["name"],unique=true)])
data class HabitEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val name:String,val description:String="",val targetPerDay:Int=1,val active:Boolean=true,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="habit_logs",primaryKeys=["habitId","date"],indices=[Index("date")])
data class HabitLogEntity(val habitId:String,val date:String,val completedCount:Int=0,val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="activities",indices=[Index("date"),Index("type")])
data class ActivityEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val date:String,val type:String,val title:String,val durationMinutes:Int=0,val metadata:String="",val createdAt:Long=System.currentTimeMillis())
@Entity(tableName="goals",indices=[Index("status")])
data class GoalEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val title:String,val description:String="",val targetDate:String?=null,val progress:Int=0,val status:String="ACTIVE",val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="projects",indices=[Index("status")])
data class ProjectEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val name:String,val description:String="",val status:String="ACTIVE",val goalId:String?=null,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="journal_entries",indices=[Index("date")])
data class JournalEntryEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val date:String,val title:String,val content:String,val mood:Int?=null,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="study_subjects",indices=[Index(value=["name"],unique=true)])
data class StudySubjectEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val name:String,val category:String="GENERAL",val targetMinutes:Int=0,val active:Boolean=true)
@Entity(tableName="study_topics",indices=[Index("subjectId"),Index("status")])
data class StudyTopicEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val subjectId:String,val name:String,val status:String="TODO",val difficulty:Int=2,val notes:String="",val updatedAt:Long=System.currentTimeMillis())
@Entity(tableName="study_sessions",indices=[Index("date"),Index("subjectId"),Index("topicId")])
data class StudySessionEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val date:String,val subjectId:String?,val topicId:String?,val title:String,val durationMinutes:Int,val quality:Int=3,val notes:String="",val createdAt:Long=System.currentTimeMillis())
@Entity(tableName="assessments",indices=[Index("date"),Index("subjectId")])
data class AssessmentEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val date:String,val subjectId:String?,val title:String,val score:Double,val total:Double,val mistakes:String="",val createdAt:Long=System.currentTimeMillis())
@Entity(tableName="knowledge_notes",indices=[Index("topicId"),Index("updatedAt")])
data class KnowledgeNoteEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val topicId:String?,val title:String,val content:String,val tags:String="",val reviewDate:String?=null,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())

@Dao interface TrackerDao{
 @Query("SELECT * FROM tasks WHERE dayId=:dayId ORDER BY completed ASC,priority ASC,createdAt ASC") fun tasksForDay(dayId:String):Flow<List<TaskEntity>>
 @Insert fun insertTask(task:TaskEntity)
 @Update fun updateTask(task:TaskEntity)
 @Query("SELECT * FROM habits WHERE active=1 ORDER BY name") fun activeHabits():Flow<List<HabitEntity>>
 @Insert fun insertHabit(habit:HabitEntity)
 @Query("SELECT * FROM habit_logs WHERE date=:date") fun habitLogsForDate(date:String):Flow<List<HabitLogEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) fun upsertHabitLog(log:HabitLogEntity)
 @Insert fun insertActivity(activity:ActivityEntity)
 @Query("SELECT * FROM activities WHERE date=:date ORDER BY createdAt DESC") fun activitiesForDate(date:String):Flow<List<ActivityEntity>>
 @Query("SELECT * FROM goals ORDER BY status,targetDate") fun goals():Flow<List<GoalEntity>>
 @Insert fun insertGoal(goal:GoalEntity)
 @Update fun updateGoal(goal:GoalEntity)
 @Query("SELECT * FROM projects ORDER BY status,name") fun projects():Flow<List<ProjectEntity>>
 @Insert fun insertProject(project:ProjectEntity)
 @Query("SELECT * FROM journal_entries ORDER BY date DESC,updatedAt DESC") fun journal():Flow<List<JournalEntryEntity>>
 @Insert fun insertJournal(entry:JournalEntryEntity)
 @Query("SELECT * FROM study_subjects WHERE active=1 ORDER BY name") fun studySubjects():Flow<List<StudySubjectEntity>>
 @Insert fun insertSubject(subject:StudySubjectEntity)
 @Query("SELECT * FROM study_topics ORDER BY status,name") fun studyTopics():Flow<List<StudyTopicEntity>>
 @Insert fun insertTopic(topic:StudyTopicEntity)
 @Insert fun insertStudySession(session:StudySessionEntity)
 @Query("SELECT * FROM study_sessions ORDER BY date DESC,createdAt DESC") fun studySessions():Flow<List<StudySessionEntity>>
 @Insert fun insertAssessment(assessment:AssessmentEntity)
 @Query("SELECT * FROM assessments ORDER BY date DESC,createdAt DESC") fun assessments():Flow<List<AssessmentEntity>>
 @Insert fun insertKnowledgeNote(note:KnowledgeNoteEntity)
 @Query("SELECT * FROM knowledge_notes ORDER BY updatedAt DESC") fun knowledgeNotes():Flow<List<KnowledgeNoteEntity>>
}
@Database(entities=[DayEntity::class,TaskEntity::class,HabitEntity::class,HabitLogEntity::class,ActivityEntity::class,GoalEntity::class,ProjectEntity::class,JournalEntryEntity::class,StudySubjectEntity::class,StudyTopicEntity::class,StudySessionEntity::class,AssessmentEntity::class,KnowledgeNoteEntity::class],version=1,exportSchema=true)
abstract class TrackerDatabase:RoomDatabase(){
 abstract fun trackerDao():TrackerDao
 companion object{
  @Volatile private var INSTANCE:TrackerDatabase?=null
  fun get(context:Context):TrackerDatabase=INSTANCE?:synchronized(this){INSTANCE?:Room.databaseBuilder(context.applicationContext,TrackerDatabase::class.java,"tracker_v2.db").build().also{INSTANCE=it}}
 }
}