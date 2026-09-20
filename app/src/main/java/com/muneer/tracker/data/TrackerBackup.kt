package com.muneer.tracker.data

import org.json.JSONArray
import org.json.JSONObject
import androidx.room.withTransaction

object TrackerBackup {
    const val FORMAT_VERSION = 1
    const val DATABASE_VERSION = 1

    fun createJson(
        days: List<DayEntity>,
        tasks: List<TaskEntity>,
        habits: List<HabitEntity>,
        habitLogs: List<HabitLogEntity>,
        activities: List<ActivityEntity>,
        goals: List<GoalEntity>,
        projects: List<ProjectEntity>,
        journalEntries: List<JournalEntryEntity>,
        studySubjects: List<StudySubjectEntity>,
        studyTopics: List<StudyTopicEntity>,
        studySessions: List<StudySessionEntity>,
        assessments: List<AssessmentEntity>,
        knowledgeNotes: List<KnowledgeNoteEntity>,
        weeklyReviews: List<WeeklyReviewEntity>
    ): String {
        val root = JSONObject()
            .put("formatVersion", FORMAT_VERSION)
            .put("databaseVersion", DATABASE_VERSION)
            .put("exportedAt", System.currentTimeMillis())

        root.put("days", JSONArray().also { a -> days.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("note", it.note)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("tasks", JSONArray().also { a -> tasks.forEach { a.put(JSONObject()
            .put("id", it.id).put("dayId", it.dayId).put("title", it.title)
            .put("description", it.description).put("priority", it.priority)
            .put("completed", it.completed).putNullableLong("dueAt", it.dueAt)
            .putNullableString("projectId", it.projectId).put("createdAt", it.createdAt)
            .put("updatedAt", it.updatedAt)) } })

        root.put("habits", JSONArray().also { a -> habits.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("description", it.description)
            .put("targetPerDay", it.targetPerDay).put("active", it.active)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("habitLogs", JSONArray().also { a -> habitLogs.forEach { a.put(JSONObject()
            .put("habitId", it.habitId).put("date", it.date)
            .put("completedCount", it.completedCount).put("updatedAt", it.updatedAt)) } })

        root.put("activities", JSONArray().also { a -> activities.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("type", it.type).put("title", it.title)
            .put("durationMinutes", it.durationMinutes).put("metadata", it.metadata)
            .put("createdAt", it.createdAt)) } })

        root.put("goals", JSONArray().also { a -> goals.forEach { a.put(JSONObject()
            .put("id", it.id).put("title", it.title).put("description", it.description)
            .putNullableString("targetDate", it.targetDate).put("progress", it.progress)
            .put("status", it.status).put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("projects", JSONArray().also { a -> projects.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("description", it.description)
            .put("status", it.status).putNullableString("goalId", it.goalId)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("journalEntries", JSONArray().also { a -> journalEntries.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).put("title", it.title).put("content", it.content)
            .putNullableInt("mood", it.mood).put("createdAt", it.createdAt)
            .put("updatedAt", it.updatedAt)) } })

        root.put("studySubjects", JSONArray().also { a -> studySubjects.forEach { a.put(JSONObject()
            .put("id", it.id).put("name", it.name).put("category", it.category)
            .put("targetMinutes", it.targetMinutes).put("active", it.active)) } })

        root.put("studyTopics", JSONArray().also { a -> studyTopics.forEach { a.put(JSONObject()
            .put("id", it.id).put("subjectId", it.subjectId).put("name", it.name)
            .put("status", it.status).put("difficulty", it.difficulty).put("notes", it.notes)
            .put("updatedAt", it.updatedAt)) } })

        root.put("studySessions", JSONArray().also { a -> studySessions.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).putNullableString("subjectId", it.subjectId)
            .putNullableString("topicId", it.topicId).put("title", it.title)
            .put("durationMinutes", it.durationMinutes).put("quality", it.quality)
            .put("notes", it.notes).put("createdAt", it.createdAt)) } })

        root.put("assessments", JSONArray().also { a -> assessments.forEach { a.put(JSONObject()
            .put("id", it.id).put("date", it.date).putNullableString("subjectId", it.subjectId)
            .put("title", it.title).put("score", it.score).put("total", it.total)
            .put("mistakes", it.mistakes).put("createdAt", it.createdAt)) } })

        root.put("knowledgeNotes", JSONArray().also { a -> knowledgeNotes.forEach { a.put(JSONObject()
            .put("id", it.id).putNullableString("topicId", it.topicId).put("title", it.title)
            .put("content", it.content).put("tags", it.tags).putNullableString("reviewDate", it.reviewDate)
            .put("createdAt", it.createdAt).put("updatedAt", it.updatedAt)) } })

        root.put("weeklyReviews", JSONArray().also { a -> weeklyReviews.forEach { a.put(JSONObject()
            .put("id", it.id).put("weekStart", it.weekStart).put("wins", it.wins)
            .put("blockers", it.blockers).put("nextFocus", it.nextFocus).put("createdAt", it.createdAt)) } })

        return root.toString(2)
    }

    data class RestoreData(
        val days: List<DayEntity>,
        val tasks: List<TaskEntity>,
        val habits: List<HabitEntity>,
        val habitLogs: List<HabitLogEntity>,
        val activities: List<ActivityEntity>,
        val goals: List<GoalEntity>,
        val projects: List<ProjectEntity>,
        val journalEntries: List<JournalEntryEntity>,
        val studySubjects: List<StudySubjectEntity>,
        val studyTopics: List<StudyTopicEntity>,
        val studySessions: List<StudySessionEntity>,
        val assessments: List<AssessmentEntity>,
        val knowledgeNotes: List<KnowledgeNoteEntity>,
        val weeklyReviews: List<WeeklyReviewEntity>
    )

    fun parseAndValidate(json: String): RestoreData {
        val root = JSONObject(json)
        require(root.optInt("formatVersion", -1) == FORMAT_VERSION) { "Unsupported backup format version." }
        require(root.optInt("databaseVersion", -1) == DATABASE_VERSION) { "Unsupported database version." }
        require(root.has("exportedAt")) { "Backup is missing exportedAt." }

        fun array(name: String) = root.optJSONArray(name) ?: error("Backup is missing $name.")
        fun requiredString(o: JSONObject, key: String) = o.optString(key, "").also {
            require(it.isNotBlank()) { "Invalid $key in backup." }
        }
        fun requiredLong(o: JSONObject, key: String) = o.optLong(key, Long.MIN_VALUE).also {
            require(it != Long.MIN_VALUE) { "Invalid $key in backup." }
        }
        fun nullableString(o: JSONObject, key: String): String? =
            if (o.isNull(key)) null else o.optString(key, "").also { require(it.isNotBlank()) { "Invalid $key in backup." } }
        fun nullableLong(o: JSONObject, key: String): Long? =
            if (o.isNull(key)) null else o.optLong(key, Long.MIN_VALUE).also { require(it != Long.MIN_VALUE) { "Invalid $key in backup." } }
        fun nullableInt(o: JSONObject, key: String): Int? =
            if (o.isNull(key)) null else o.optInt(key, Int.MIN_VALUE).also { require(it != Int.MIN_VALUE) { "Invalid $key in backup." } }
        fun <T> uniqueIds(items: List<T>, id: (T) -> String) {
            require(items.map(id).toSet().size == items.size) { "Duplicate IDs in backup." }
        }

        val days = buildList {
            val a=array("days"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(DayEntity(requiredString(o,"id"),requiredString(o,"date"),o.optString("note",""),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val tasks = buildList {
            val a=array("tasks"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(TaskEntity(requiredString(o,"id"),requiredString(o,"dayId"),requiredString(o,"title"),o.optString("description",""),o.optInt("priority",2),o.optBoolean("completed",false),nullableLong(o,"dueAt"),nullableString(o,"projectId"),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val habits = buildList {
            val a=array("habits"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(HabitEntity(requiredString(o,"id"),requiredString(o,"name"),o.optString("description",""),o.optInt("targetPerDay",1),o.optBoolean("active",true),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val habitLogs = buildList {
            val a=array("habitLogs"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(HabitLogEntity(requiredString(o,"habitId"),requiredString(o,"date"),o.optInt("completedCount",0),requiredLong(o,"updatedAt")))}
        }
        val activities = buildList {
            val a=array("activities"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(ActivityEntity(requiredString(o,"id"),requiredString(o,"date"),requiredString(o,"type"),requiredString(o,"title"),o.optInt("durationMinutes",0),o.optString("metadata",""),requiredLong(o,"createdAt")))}
        }
        val goals = buildList {
            val a=array("goals"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(GoalEntity(requiredString(o,"id"),requiredString(o,"title"),o.optString("description",""),nullableString(o,"targetDate"),o.optInt("progress",0),o.optString("status","ACTIVE"),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val projects = buildList {
            val a=array("projects"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(ProjectEntity(requiredString(o,"id"),requiredString(o,"name"),o.optString("description",""),o.optString("status","ACTIVE"),nullableString(o,"goalId"),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val journalEntries = buildList {
            val a=array("journalEntries"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(JournalEntryEntity(requiredString(o,"id"),requiredString(o,"date"),requiredString(o,"title"),requiredString(o,"content"),nullableInt(o,"mood"),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val studySubjects = buildList {
            val a=array("studySubjects"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(StudySubjectEntity(requiredString(o,"id"),requiredString(o,"name"),o.optString("category","GENERAL"),o.optInt("targetMinutes",0),o.optBoolean("active",true)))}
        }
        val studyTopics = buildList {
            val a=array("studyTopics"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(StudyTopicEntity(requiredString(o,"id"),requiredString(o,"subjectId"),requiredString(o,"name"),o.optString("status","TODO"),o.optInt("difficulty",2),o.optString("notes",""),requiredLong(o,"updatedAt")))}
        }
        val studySessions = buildList {
            val a=array("studySessions"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(StudySessionEntity(requiredString(o,"id"),requiredString(o,"date"),nullableString(o,"subjectId"),nullableString(o,"topicId"),requiredString(o,"title"),o.optInt("durationMinutes",0),o.optInt("quality",3),o.optString("notes",""),requiredLong(o,"createdAt")))}
        }
        val assessments = buildList {
            val a=array("assessments"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(AssessmentEntity(requiredString(o,"id"),requiredString(o,"date"),nullableString(o,"subjectId"),requiredString(o,"title"),o.optDouble("score",Double.NaN),o.optDouble("total",Double.NaN),o.optString("mistakes",""),requiredLong(o,"createdAt")));require(last().score.isFinite()&&last().total.isFinite()){"Invalid assessment score."}}
        }
        val knowledgeNotes = buildList {
            val a=array("knowledgeNotes"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(KnowledgeNoteEntity(requiredString(o,"id"),nullableString(o,"topicId"),requiredString(o,"title"),requiredString(o,"content"),o.optString("tags",""),nullableString(o,"reviewDate"),requiredLong(o,"createdAt"),requiredLong(o,"updatedAt")))}
        }
        val weeklyReviews = buildList {
            val a=array("weeklyReviews"); for(i in 0 until a.length()){val o=a.getJSONObject(i);add(WeeklyReviewEntity(requiredString(o,"id"),requiredString(o,"weekStart"),o.optString("wins",""),o.optString("blockers",""),o.optString("nextFocus",""),requiredLong(o,"createdAt")))}
        }

        uniqueIds(days){it.id}; uniqueIds(tasks){it.id}; uniqueIds(habits){it.id}; uniqueIds(activities){it.id}; uniqueIds(goals){it.id}; uniqueIds(projects){it.id}; uniqueIds(journalEntries){it.id}; uniqueIds(studySubjects){it.id}; uniqueIds(studyTopics){it.id}; uniqueIds(studySessions){it.id}; uniqueIds(assessments){it.id}; uniqueIds(knowledgeNotes){it.id}; uniqueIds(weeklyReviews){it.id}
        val dayIds=days.map{it.id}.toSet(); val habitIds=habits.map{it.id}.toSet(); val projectIds=projects.map{it.id}.toSet(); val goalIds=goals.map{it.id}.toSet(); val subjectIds=studySubjects.map{it.id}.toSet(); val topicIds=studyTopics.map{it.id}.toSet()
        require(tasks.all{it.dayId in dayIds && (it.projectId==null || it.projectId in projectIds)}){"Task references missing data."}
        require(habitLogs.all{it.habitId in habitIds}){"Habit log references missing habit."}
        require(projects.all{it.goalId==null || it.goalId in goalIds}){"Project references missing goal."}
        require(studyTopics.all{it.subjectId in subjectIds}){"Study topic references missing subject."}
        require(studySessions.all{(it.subjectId==null||it.subjectId in subjectIds)&&(it.topicId==null||it.topicId in topicIds)}){"Study session references missing data."}
        require(assessments.all{it.subjectId==null||it.subjectId in subjectIds}){"Assessment references missing subject."}
        require(knowledgeNotes.all{it.topicId==null||it.topicId in topicIds}){"Knowledge note references missing topic."}
        require(days.map{it.date}.toSet().size==days.size){"Duplicate day dates in backup."}
        require(habits.map{it.name}.toSet().size==habits.size){"Duplicate habit names in backup."}
        require(studySubjects.map{it.name}.toSet().size==studySubjects.size){"Duplicate subject names in backup."}
        require(weeklyReviews.map{it.weekStart}.toSet().size==weeklyReviews.size){"Duplicate weekly review dates in backup."}

        return RestoreData(days,tasks,habits,habitLogs,activities,goals,projects,journalEntries,studySubjects,studyTopics,studySessions,assessments,knowledgeNotes,weeklyReviews)
    }

    suspend fun restore(db: TrackerDatabase, data: RestoreData) {
        db.withTransaction {
            val d=db.trackerDao()
            d.clearHabitLogs(); d.clearTasks(); d.clearStudySessions(); d.clearAssessments(); d.clearKnowledgeNotes(); d.clearStudyTopics(); d.clearJournalEntries(); d.clearActivities(); d.clearProjects(); d.clearGoals(); d.clearWeeklyReviews(); d.clearHabits(); d.clearDays()
            d.insertDays(data.days); d.insertGoals(data.goals); d.insertProjects(data.projects); d.insertHabits(data.habits); d.insertTasks(data.tasks); d.insertHabitLogs(data.habitLogs); d.insertActivities(data.activities); d.insertJournalEntries(data.journalEntries); d.insertStudySubjects(data.studySubjects); d.insertStudyTopics(data.studyTopics); d.insertStudySessions(data.studySessions); d.insertAssessments(data.assessments); d.insertKnowledgeNotes(data.knowledgeNotes); d.insertWeeklyReviews(data.weeklyReviews)
        }
    }

    private fun JSONObject.putNullableString(key: String, value: String?): JSONObject =
        put(key, value ?: JSONObject.NULL)

    private fun JSONObject.putNullableLong(key: String, value: Long?): JSONObject =
        put(key, value ?: JSONObject.NULL)

    private fun JSONObject.putNullableInt(key: String, value: Int?): JSONObject =
        put(key, value ?: JSONObject.NULL)
}
