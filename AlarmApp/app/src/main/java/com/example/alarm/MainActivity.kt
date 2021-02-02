package com.example.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import com.example.alarm.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var alarmManager: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private var isRepeated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.checkBoxRepeat.setOnCheckedChangeListener { _, isChecked ->
            isRepeated = isChecked
        }

        binding.btnSetAlarm.setOnClickListener(View.OnClickListener {
            val selectedHour = binding.timePicker.hour
            val selectedMinute = binding.timePicker.minute

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                setTriggerTime(this, selectedHour, selectedMinute)
            }

            Toast.makeText(this@MainActivity, "Alarm set to ${calendar.time}", Toast.LENGTH_SHORT).show()
            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            resetAlarm()

            alarmManager?.run {
                if (isRepeated) {
                    setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
                } else {
                    setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
                }
            }
        })
    }

    private fun resetAlarm() {
        alarmIntent = Intent(applicationContext, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        alarmManager?.run {
            cancel(alarmIntent)
        }
    }

    private fun setTriggerTime(calendar: Calendar, selectedHour: Int, selectedMinute: Int) {
        val currentHours = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinutes = calendar.get(Calendar.MINUTE)

        // selected hour is the same or before - selected minute is before or the same
        if (selectedHour - currentHours <= 0) {
            if (selectedMinute - currentMinutes <= 0) {
                calendar.add(Calendar.DATE, 1)
            }
        }

        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
    }

    companion object {
        val REQUEST_CODE = 0
    }
}