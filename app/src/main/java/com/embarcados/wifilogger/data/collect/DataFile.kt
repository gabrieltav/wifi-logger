package com.embarcados.wifilogger.data.collect

import android.content.Context
import android.net.wifi.WifiManager
import com.embarcados.domain.collect.device.DeviceCollect
import com.embarcados.domain.collect.wifi.WifiCollect
import com.embarcados.wifilogger.utils.UtilsStaticService
import java.io.File
import java.io.IOException
import com.opencsv.CSVWriter
import java.io.FileWriter

class DataFile {
    fun saveData(context: Context) {
        val dc = DeviceCollect()
        val wc = WifiCollect()
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val directoryPath = UtilsStaticService().directoryPath()

        val bfile = File(directoryPath)
        if (!bfile.exists()) {
            bfile.mkdirs()
        }
        val bfile2 = File(directoryPath, UtilsStaticService().getNameCsv(context))

        val deviceId = dc.getDeviceId(context)
        val model = dc.getModelAndroid()
        val androidVersion = dc.getVersionAndroid()
        val wifiStatus = wc.getStatusWifi(context)
        val ssid = wc.wifiSSID(context)
        val ipDevice = wc.getWifiIpDevice(wifiManager)

        if (!bfile2.exists()) {
            val writer: CSVWriter?
            try {
                writer = CSVWriter(FileWriter(bfile2, true), ',', CSVWriter.DEFAULT_QUOTE_CHARACTER)
                val data: MutableList<Array<String>> = ArrayList()
                data.add(
                    arrayOf(
                        "deviceId",
                        "model",
                        "androidVersion",
                        "wifiStatus",
                        "ssid",
                        "ipDevice"
                    )
                )
                writer.writeAll(data)
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            val writer: CSVWriter?
            try {
                writer = CSVWriter(FileWriter(bfile2, true), ',', CSVWriter.DEFAULT_QUOTE_CHARACTER)

                val data: MutableList<Array<String?>> = ArrayList()
                data.add(
                    arrayOf(
                        deviceId,
                        model,
                        androidVersion,
                        wifiStatus,
                        ssid,
                        ipDevice,
                    )
                )
                writer.writeAll(data)
                writer.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}