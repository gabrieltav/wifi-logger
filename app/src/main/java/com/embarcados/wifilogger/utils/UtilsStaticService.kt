package com.embarcados.wifilogger.utils

import android.content.Context
import android.os.Environment
import android.text.format.DateFormat
import com.embarcados.domain.collect.device.DeviceCollect
import com.embarcados.domain.utils.constants.DIRECTORY_PATH
import java.util.Date

class UtilsStaticService {
    private val date = Date()

    fun directoryPath(): String {
        return Environment.getExternalStorageDirectory().path + DIRECTORY_PATH
    }

    fun getNameCsv(context: Context): String {
        val month = DateFormat.format("MM", date) as String // 06
        val day = DateFormat.format("dd", date) as String // 20
        val year = DateFormat.format("yyyy", date) as String // 2022

        val dc = DeviceCollect()
        val deviceId = dc.getDeviceId(context)

        return "$month-$day-$year-$deviceId.csv"
    }
}