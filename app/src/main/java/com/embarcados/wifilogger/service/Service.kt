package com.embarcados.wifilogger.service

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.embarcados.domain.utils.constants.INIT
import com.embarcados.domain.utils.constants.wStartRX
import com.embarcados.domain.utils.constants.wStartTX
import com.embarcados.wifilogger.data.collect.DataFile
import com.embarcados.wifilogger.utils.notification.NotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class Service : Service() {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    override fun onCreate() {
        super.onCreate()
        scope.launch {
            while (true) {
                showFiles()
                initMobileRxAndTx()
                withContext(Dispatchers.Main) {
                    delay(1000)
                }
            }
        }
        startAlarm()
        startNotification()
    }
    private fun startAlarm() {
        val update = Calendar.getInstance()
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            update.timeInMillis,
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )
    }
    private fun showFiles() {
        val data = DataFile()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            data.saveData(this)
        }
    }
    private fun initMobileRxAndTx() {
        if (wStartRX == INIT) {
            wStartRX = TrafficStats.getTotalRxBytes()
        }
        if (wStartTX == INIT) {
            wStartTX = TrafficStats.getTotalTxBytes()
        }
    }
    private fun startNotification() {
        val foregroundWithNotification = NotificationReceiver()
        foregroundWithNotification.startForegroundWithNotification(this)
    }
}