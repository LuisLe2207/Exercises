package com.example.countdownapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is channel 1"
            channel1.enableVibration(true)
            channel1.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            val manager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
        }
    }

    companion object {
        val NOTIFICATION_CHANNEL_ID = "NotificationChannel1"
    }
}