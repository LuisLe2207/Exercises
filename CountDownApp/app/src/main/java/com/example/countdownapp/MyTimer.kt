package com.example.countdownapp

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.lang.ref.WeakReference

class MyTimer(context: Context, millisInFuture: Long) {
    private var context: WeakReference<Context> = WeakReference(context)
    private var millisInFuture = millisInFuture
    private var isRunning = false
    private lateinit var internalTimer : InternalTimer

    inner class InternalTimer(millisInFuture: Long): CountDownTimer(millisInFuture, COUNT_DOWN_INTERNAL) {
        var remainingTime = 0L
        override fun onTick(millisUntilFinished: Long) {
            remainingTime = millisUntilFinished
            context.get()?.let {context ->
                val intent = generateIntent(millisUntilFinished = millisUntilFinished)
                context.sendBroadcast(intent)
            }
        }

        override fun onFinish() {
            context.get()?.let {context ->
                isRunning = false
                millisInFuture = 0
                val intent = generateIntent(millisUntilFinished = 0)
                context.sendBroadcast(intent)
                sendCompleteNotification(context)
            }
        }

        private fun sendCompleteNotification(context: Context) {
            val builder = NotificationCompat.Builder(context, MyApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(context.resources.getString(R.string.timer_finished_notification_title))
                .setContentText(context.resources.getString(R.string.timer_finished_notification_msg))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .build()

            NotificationManagerCompat.from(context).apply {
                this.notify(111, builder)
            }
        }
    }

    fun startTimer() {
        if (millisInFuture == 0L) {
            return
        }

        if (!isRunning && internalTimer.remainingTime > 0) {
            internalTimer = InternalTimer(internalTimer.remainingTime)
        }
        internalTimer.start()
        isRunning = true
    }

    fun pauseTimer() {
        if (isRunning) {
            internalTimer.cancel()
            isRunning = false
        }
    }

    fun stopTimer() {
        isRunning = false
        internalTimer.remainingTime = 0
        internalTimer.cancel()
        internalTimer = InternalTimer(millisInFuture)
    }

    fun timerIsRunning() = isRunning

    fun elapsedTime() = millisInFuture

    fun setElapseTime(time: Long) {
        millisInFuture = time
        internalTimer = InternalTimer(millisInFuture)
    }

    private fun generateIntent(millisUntilFinished: Long): Intent {
        return Intent().also {
            it.action = INTENT_ACTION
            it.putExtra(EXTRA_KEY_DATA, millisUntilFinished)
        }
    }

    companion object {
        val COUNT_DOWN_INTERNAL = 1000L
        val INTENT_ACTION = "com.example.countdownapp.customaction"
        val EXTRA_KEY_DATA = "data"
    }
}