package com.muneer.tracker.planner

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ScheduleItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val notes: String,
    val triggerAt: Long,
    val durationMinutes: Int,
    val taskId: String? = null,
    val goal: String = ""
)

class ScheduleStore(context: Context) {
    private val prefs = context.getSharedPreferences("planner_v3", Context.MODE_PRIVATE)

    fun all(): List<ScheduleItem> {
        val raw = prefs.getString("items", "[]") ?: "[]"
        val json = JSONArray(raw)
        return buildList {
            for (i in 0 until json.length()) {
                val o = json.getJSONObject(i)
                add(ScheduleItem(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    notes = o.optString("notes"),
                    triggerAt = o.getLong("triggerAt"),
                    durationMinutes = o.optInt("durationMinutes", 25),
                    taskId = o.optString("taskId").ifBlank { null },
                    goal = o.optString("goal")
                ))
            }
        }.sortedBy { it.triggerAt }
    }

    fun upsert(item: ScheduleItem) {
        val items = all().filterNot { it.id == item.id } + item
        save(items)
    }

    fun delete(id: String) = save(all().filterNot { it.id == id })

    private fun save(items: List<ScheduleItem>) {
        val array = JSONArray()
        items.sortedBy { it.triggerAt }.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("notes", item.notes)
                put("triggerAt", item.triggerAt)
                put("durationMinutes", item.durationMinutes)
                put("taskId", item.taskId ?: "")
                put("goal", item.goal)
            })
        }
        prefs.edit().putString("items", array.toString()).apply()
    }
}
