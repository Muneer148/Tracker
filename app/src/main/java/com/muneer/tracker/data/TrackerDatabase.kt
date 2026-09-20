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
@Entity(tableName="weekly_reviews", indices=[Index(value=["weekStart"], unique=true)])
data class WeeklyReviewEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val weekStart:String,val wins:String="",val blockers:String="",val nextFocus:String="",val createdAt:Long=System.currentTimeMillis())

@Entity(tableName="knowledge_notes",indices=[Index("topicId"),Index("updatedAt")])
data class KnowledgeNoteEntity(@PrimaryKey val id:String=UUID.randomUUID().toString(),val topicId:String?,val title:String,val content:String,val tags:String="",val reviewDate:String?=null,val createdAt:Long=System.currentTimeMillis(),val updatedAt:Long=System.currentTimeMillis())

@Dao interface TrackerDao{
 @Query("DELETE FROM days") suspend fun clearDays()
 @Query("DELETE FROM tasks") suspend fun clearTasks()
 @Query("DELETE FROM habits") suspend fun clearHabits()
 @Query("DELETE FROM habit_logs") suspend fun clearHabitLogs()
 @Query("DELETE FROM activities") suspend fun clearActivities()
 @Query("DELETE FROM goals") suspend fun clearGoals()
 @Query("DELETE FROM projects") suspend fun clearProjects()
 @Query("DELETE FROM journal_entries") suspend fun clearJournalEntries()
 @Query("DELETE FROM study_subjects") suspend fun clearStudySubjects()
 @Query("DELETE FROM study_topics") suspend fun clearStudyTopics()
 @Query("DELETE FROM study_sessions") suspend fun clearStudySessions()
 @Query("DELETE FROM assessments") suspend fun clearAssessments()
 @Query("DELETE FROM knowledge_notes") suspend fun clearKnowledgeNotes()
 @Query("DELETE FROM weekly_reviews") suspend fun clearWeeklyReviews()

 @Insert suspend fun insertDays(items:List<DayEntity>)
 @Insert suspend fun insertTasks(items:List<TaskEntity>)
 @Insert suspend fun insertHabits(items:List<HabitEntity>)
 @Insert suspend fun insertHabitLogs(items:List<HabitLogEntity>)
 @Insert suspend fun insertActivities(items:List<ActivityEntity>)
 @Insert suspend fun insertGoals(items:List<GoalEntity>)
 @Insert suspend fun insertProjects(items:List<ProjectEntity>)
 @Insert suspend fun insertJournalEntries(items:List<JournalEntryEntity>)
 @Insert suspend fun insertStudySubjects(items:List<StudySubjectEntity>)
 @Insert suspend fun insertStudyTopics(items:List<StudyTopicEntity>)
 @Insert suspend fun insertStudySessions(items:List<StudySessionEntity>)
 @Insert suspend fun insertAssessments(items:List<AssessmentEntity>)
 @Insert suspend fun insertKnowledgeNotes(items:List<KnowledgeNoteEntity>)
 @Insert suspend fun insertWeeklyReviews(items:List<WeeklyReviewEntity>)

 @Query("SELECT * FROM days ORDER BY date ASC") suspend fun allDays():List<DayEntity>
 @Query("SELECT * FROM tasks ORDER BY createdAt ASC") suspend fun allTasks():List<TaskEntity>
 @Query("SELECT * FROM habits ORDER BY createdAt ASC") suspend fun allHabits():List<HabitEntity>
 @Query("SELECT * FROM habit_logs ORDER BY date ASC") suspend fun allHabitLogs():List<HabitLogEntity>
 @Query("SELECT * FROM activities ORDER BY createdAt ASC") suspend fun allActivities():List<ActivityEntity>
 @Query("SELECT * FROM goals ORDER BY createdAt ASC") suspend fun allGoals():List<GoalEntity>
 @Query("SELECT * FROM projects ORDER BY createdAt ASC") suspend fun allProjects():List<ProjectEntity>
 @Query("SELECT * FROM journal_entries ORDER BY createdAt ASC") suspend fun allJournalEntries():List<JournalEntryEntity>
 @Query("SELECT * FROM study_subjects ORDER BY name ASC") suspend fun allStudySubjects():List<StudySubjectEntity>
 @Query("SELECT * FROM study_topics ORDER BY name ASC") suspend fun allStudyTopics():List<StudyTopicEntity>
 @Query("SELECT * FROM study_sessions ORDER BY createdAt ASC") suspend fun allStudySessions():List<StudySessionEntity>
 @Query("SELECT * FROM assessments ORDER BY createdAt ASC") suspend fun allAssessments():List<AssessmentEntity>
 @Query("SELECT * FROM knowledge_notes ORDER BY createdAt ASC") suspend fun allKnowledgeNotes():List<KnowledgeNoteEntity>
 @Query("SELECT * FROM weekly_reviews ORDER BY weekStart ASC") suspend fun allWeeklyReviews():List<WeeklyReviewEntity>

 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsertDay(day:DayEntity)
 @Query("SELECT * FROM tasks WHERE dayId=:dayId ORDER BY completed ASC,priority ASC,createdAt ASC") fun tasksForDay(dayId:String):Flow<List<TaskEntity>>
 @Insert suspend fun insertTask(task:TaskEntity)
 @Update suspend fun updateTask(task:TaskEntity)
 @Delete suspend fun deleteTask(task:TaskEntity)
 @Delete suspend fun deleteHabit(habit:HabitEntity)
 @Query("SELECT * FROM habits WHERE active=1 ORDER BY name") fun activeHabits():Flow<List<HabitEntity>>
 @Insert(onConflict=OnConflictStrategy.IGNORE) suspend fun insertHabit(habit:HabitEntity)
 @Query("SELECT * FROM habit_logs WHERE date=:date") fun habitLogsForDate(date:String):Flow<List<HabitLogEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsertHabitLog(log:HabitLogEntity)
 @Insert suspend fun insertActivity(activity:ActivityEntity)
 @Query("SELECT * FROM activities WHERE date=:date ORDER BY createdAt DESC") fun activitiesForDate(date:String):Flow<List<ActivityEntity>>
 @Query("SELECT * FROM goals ORDER BY status,targetDate") fun goals():Flow<List<GoalEntity>>
 @Insert suspend fun insertGoal(goal:GoalEntity)
 @Update suspend fun updateGoal(goal:GoalEntity)
 @Delete suspend fun deleteGoal(goal:GoalEntity)
 @Delete suspend fun deleteProject(project:ProjectEntity)
 @Query("SELECT * FROM projects ORDER BY status,name") fun projects():Flow<List<ProjectEntity>>
 @Insert suspend fun insertProject(project:ProjectEntity)
 @Query("SELECT * FROM journal_entries ORDER BY date DESC,updatedAt DESC") fun journal():Flow<List<JournalEntryEntity>>
 @Insert suspend fun insertJournal(entry:JournalEntryEntity)
 @Delete suspend fun deleteJournal(entry:JournalEntryEntity)
 @Query("SELECT * FROM study_subjects WHERE active=1 ORDER BY name") fun studySubjects():Flow<List<StudySubjectEntity>>
 @Insert suspend fun insertSubject(subject:StudySubjectEntity)
 @Query("SELECT * FROM study_topics ORDER BY status,name") fun studyTopics():Flow<List<StudyTopicEntity>>
 @Insert suspend fun insertTopic(topic:StudyTopicEntity)
 @Insert suspend fun insertStudySession(session:StudySessionEntity)
 @Query("SELECT * FROM study_sessions ORDER BY date DESC,createdAt DESC") fun studySessions():Flow<List<StudySessionEntity>>
 @Insert suspend fun insertAssessment(assessment:AssessmentEntity)
 @Query("SELECT * FROM assessments ORDER BY date DESC,createdAt DESC") fun assessments():Flow<List<AssessmentEntity>>
 @Insert suspend fun insertWeeklyReview(review:WeeklyReviewEntity)
 @Query("SELECT * FROM weekly_reviews ORDER BY weekStart DESC") fun weeklyReviews():Flow<List<WeeklyReviewEntity>>
 @Query("SELECT COUNT(*) FROM tasks WHERE completed=1") fun completedTaskCount():Flow<Int>
 @Query("SELECT COALESCE(SUM(durationMinutes),0) FROM study_sessions") fun totalStudyMinutes():Flow<Int>
 @Query("SELECT COUNT(*) FROM journal_entries") fun journalCount():Flow<Int>
 @Insert suspend fun insertKnowledgeNote(note:KnowledgeNoteEntity)
 @Query("SELECT * FROM knowledge_notes ORDER BY updatedAt DESC") fun knowledgeNotes():Flow<List<KnowledgeNoteEntity>>
}
@Database(entities=[DayEntity::class,TaskEntity::class,HabitEntity::class,HabitLogEntity::class,ActivityEntity::class,GoalEntity::class,ProjectEntity::class,JournalEntryEntity::class,StudySubjectEntity::class,StudyTopicEntity::class,StudySessionEntity::class,AssessmentEntity::class,KnowledgeNoteEntity::class,WeeklyReviewEntity::class],version=1,exportSchema=true)
abstract class TrackerDatabase:RoomDatabase(){
 abstract fun trackerDao():TrackerDao
 companion object{
  @Volatile private var INSTANCE:TrackerDatabase?=null
  fun get(context:Context):TrackerDatabase=INSTANCE?:synchronized(this){INSTANCE?:Room.databaseBuilder(context.applicationContext,TrackerDatabase::class.java,"tracker_v2.db").build().also{INSTANCE=it}}
 }
}