package com.muneer.tracker.planner

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.muneer.tracker.R

class ScheduleNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Scheduled session"
        val notes = intent.getStringExtra("notes") ?: "It's time to start your planned session."
        val notificationId = intent.getIntExtra("notificationId", title.hashCode())
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Planner reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Scheduled Tracker tasks and study sessions"
            }
        )
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(notes.ifBlank { "Open Tracker and start the session." })
            .setStyle(NotificationCompat.BigTextStyle().bigText(notes.ifBlank { "Open Tracker and start the session." }))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    companion object { const val CHANNEL_ID = "planner_reminders" }
}
