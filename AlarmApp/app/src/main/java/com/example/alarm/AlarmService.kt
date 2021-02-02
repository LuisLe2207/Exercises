package com.example.alarm

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmService: JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        val builder = NotificationCompat.Builder(this, MyApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(resources.getString(R.string.alarm_fired_notification_title))
            .setContentText(resources.getString(R.string.alarm_fired_notification_msg))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()

        NotificationManagerCompat.from(this).apply {
            this.notify(111, builder)
        }
    }

    companion object {
        val JOB_ID = 1
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, AlarmService::class.java, JOB_ID, intent)
        }
    }
}