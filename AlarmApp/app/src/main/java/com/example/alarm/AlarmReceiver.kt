package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.JobIntentService.enqueueWork
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
         context?.run {
             val alarmServiceIntent = Intent(context, AlarmService::class.java)
             AlarmService.enqueueWork(context, alarmServiceIntent)
         }
    }
}