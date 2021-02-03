package com.example.countdownapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.opengl.Visibility
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.countdownapp.MyTimer.Companion.EXTRA_KEY_DATA
import com.example.countdownapp.MyTimer.Companion.INTENT_ACTION
import com.example.countdownapp.databinding.ActivityMainBinding
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myReceiver: MyBroadcast
    private lateinit var myTimer: MyTimer
    private var shouldStartNewTimer = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        myTimer = MyTimer(applicationContext, 0)

        binding.buttonStartPause.setOnClickListener(View.OnClickListener {

            val hour = binding.edtHour.text.toString().toSafeInt()
            val minute = binding.edtMinute.text.toString().toSafeInt()
            val second = binding.edtSecond.text.toString().toSafeInt()

            if (validateInput(hour, minute, second)) {
                myTimer.run {

                    if (shouldStartNewTimer) {
                        setElapseTime(getElapseTime(hour, minute, second)).also {
                            shouldStartNewTimer = false
                            showElapseTime(true)
                        }
                    }

                    if (timerIsRunning()) {
                        pauseTimer()
                        binding.buttonStartPause.text = resources.getString(R.string.resume)
                    } else {
                        startTimer()
                        binding.buttonStartPause.text = resources.getString(R.string.pause)
                    }
                }
            }
        })

        binding.buttonStop.setOnClickListener(View.OnClickListener {
            shouldStartNewTimer = true
            myTimer.stopTimer()
            binding.buttonStartPause.text = resources.getString(R.string.start)
            resetUI()
        })

    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter().apply {
            addAction(INTENT_ACTION)
        }

        myTimer.run {
            if (!timerIsRunning() && elapsedTime() == 0L) {
                resetUI()
            }
        }

        myReceiver = MyBroadcast()
        registerReceiver(myReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
    }

    private fun getElapseTime(hour: Int, minute: Int, second: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.HOUR, hour)
            add(Calendar.MINUTE, minute)
            add(Calendar.SECOND, second)
        }

        val elapseTime = calendar.timeInMillis - System.currentTimeMillis()

        binding.timerProgress.apply {
            max = elapseTime.toProgress()
            progress = elapseTime.toProgress()
        }

        return elapseTime
    }

    private fun showElapseTime(shoudShow: Boolean) {
        if (shoudShow) {
            binding.textElapsedTime.visibility = View.VISIBLE
            binding.setTimeLayout.visibility = View.INVISIBLE
        } else {
            binding.textElapsedTime.visibility = View.INVISIBLE
            binding.setTimeLayout.visibility = View.VISIBLE
        }
    }

    private fun resetUI() {
        showElapseTime(false)
        shouldStartNewTimer = true
        binding.edtHour.text.clear()
        binding.edtMinute.text.clear()
        binding.edtSecond.text.clear()
        binding.buttonStartPause.text = resources.getString(R.string.start)
        binding.timerProgress.apply {
            max = 100
            progress = 100
        }
    }

    private fun String.toSafeInt(): Int {
        return takeIf { it.isNotEmpty() }?.toInt() ?: 0
    }

    private fun validateInput(hour: Int, minute: Int, second: Int): Boolean {
        if (hour == 0 && minute == 0 && second == 0 ) {
            Toast.makeText(this@MainActivity, resources.getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
            return false
        }

        if (minute > 59 || second > 59) {
            Toast.makeText(this@MainActivity, resources.getString(R.string.invalid_input), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun Long.toProgress(): Int {
        return (this/1000).toInt()
    }


    inner class MyBroadcast: BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {

            p1?.let {
                val data = it.getLongExtra(EXTRA_KEY_DATA, 0L)

                if (data != 0L) {
                    binding.textElapsedTime.text = hmsTimeFormatter(data)
                    binding.timerProgress.apply {
                        progress = data.toProgress()
                    }
                } else {
                    resetUI()
                }
            }
        }

        /**
         * method to convert millisecond to time format
         *
         * @param milliSeconds
         * @return HH:mm:ss time formatted string
         */
        private fun hmsTimeFormatter(milliSeconds: Long): String? {
            return String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        milliSeconds
                    )
                ),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        milliSeconds
                    )
                )
            )
        }
    }
}