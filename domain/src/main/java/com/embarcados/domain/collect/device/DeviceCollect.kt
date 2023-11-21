package com.embarcados.domain.collect.device

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.embarcados.domain.utils.constants.EMPTY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DeviceCollect {
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getVersionAndroid(): String {
        try {
            val version = Build.VERSION.RELEASE
            if (version != null) {
                return version
            }
        } catch (e: Exception) {
            EMPTY
        }
        return EMPTY
    }

    fun getModelAndroid(): String {
        try {
            val model = Build.MODEL
            if (model != null) {
                return model
            }
        } catch (e: Exception) {
            EMPTY
        }
        return EMPTY
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }
}