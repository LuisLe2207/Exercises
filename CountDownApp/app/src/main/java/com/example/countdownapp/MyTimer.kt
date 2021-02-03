package com.example.countdownapp

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import java.lang.ref.WeakReference

class MyTimer(context: Context, millisInFuture: Long): CountDownTimer(millisInFuture, COUNT_DOWN_INTERNAL) {
    private var context: WeakReference<Context> = WeakReference(context)

    override fun onTick(millisUntilFinished: Long) {
        context.get()?.let {context ->
            val intent = generateIntent(millisUntilFinished = millisUntilFinished, isCompleted = false)
            context.sendBroadcast(intent)
        }
    }

    override fun onFinish() {
        context.get()?.let {context ->
            val intent = generateIntent(millisUntilFinished = 0, isCompleted = true)
            context.sendBroadcast(intent)
        }
    }

    private fun generateIntent(millisUntilFinished: Long, isCompleted: Boolean): Intent {
        return Intent().also {
            it.action = INTENT_ACTION
            it.putExtra(EXTRA_KEY_DATA, millisUntilFinished)
            it.putExtra(EXTRA_KEY_COMPLETED, millisUntilFinished)
        }
    }

    companion object {
        val COUNT_DOWN_INTERNAL = 1000L
        val INTENT_ACTION = "com.example.countdownapp.customaction"
        val EXTRA_KEY_DATA = "data"
        val EXTRA_KEY_COMPLETED = "completed"
    }
}