package com.embarcados.domain.collect.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.TrafficStats
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.embarcados.domain.utils.constants.OFF
import com.embarcados.domain.utils.constants.ON
import com.embarcados.domain.utils.constants.ZERO
import com.embarcados.domain.utils.constants.wStartRX
import com.embarcados.domain.utils.constants.wStartTX

@Suppress("DEPRECATION")
class WifiCollect {

    // function to get Bandwidth Wifi
    fun getDownstreamBandwidthWifi(context: Context): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isWifiConn = connectivityManager.activeNetworkInfo

        try {
            if (isWifiConn!!.type == ConnectivityManager.TYPE_WIFI) {
                val bandwidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.activeNetwork?.let { activeNetwork ->
                        connectivityManager.getNetworkCapabilities(activeNetwork)!!.linkDownstreamBandwidthKbps
                    } ?: -1
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
                if (bandwidth != null) {
                    return bandwidth.toString()
                }
            }
        } catch (e: Exception) {
            OFF
        }
        return OFF
    }

    // function to get Bandwidth Wifi
    fun getUpstreamBandwidthWifi(context: Context): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isWifiConn = connectivityManager.activeNetworkInfo

        try {
            if (isWifiConn!!.type == ConnectivityManager.TYPE_WIFI) {
                val bandwidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.activeNetwork?.let { activeNetwork ->
                        connectivityManager.getNetworkCapabilities(activeNetwork)!!.linkUpstreamBandwidthKbps
                    } ?: -1
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
                if (bandwidth != null) {
                    return bandwidth.toString()
                }
            }
        } catch (e: Exception) {
            OFF
        }
        return OFF
    }

    // function to get status wifi
    fun getStatusWifi(context: Context): String? {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isWifiConn = connMgr.activeNetworkInfo
        return try {
            if (isWifiConn!!.type == ConnectivityManager.TYPE_WIFI) {
                ON
            } else {
                OFF
            }
        } catch (e: Exception) {
            OFF
        }
    }

    // function to get SSID wifi
    fun wifiSSID(context: Context): String {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ssid: String = OFF
        val wifiInfo: WifiInfo = wifiManager.connectionInfo

        if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            ssid = wifiInfo.ssid
        }
        return ssid
    }

    // function to get ip wifi
    fun getWifiIpDevice(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        val enteredIp: String
        val ip = info.ipAddress
        enteredIp = String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
        return enteredIp
    }

    // function to get signal level wifi
    fun wifiSignalLevel(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        val signalLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifiManager.calculateSignalLevel(info.rssi)
        } else TODO("VERSION.SDK_INT < R")
        return signalLevel.toString()
    }

    // function to get network id
    fun wifiNetworkId(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return try {
            info.networkId.toString()
        } catch (e: Exception) {
            OFF
        }
        return OFF
    }

    // function to get speed wifi
    fun wifiSpeed(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.linkSpeed != null) {
            info.linkSpeed.toString()
        } else {
            OFF
        }
    }

    // function to get tx link speed wifi
    @RequiresApi(Build.VERSION_CODES.Q)
    fun wifiTxLinkSpeed(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.txLinkSpeedMbps != null) {
            info.txLinkSpeedMbps.toString()
        } else {
            OFF
        }
    }

    // function to get rx link speed wifi
    @RequiresApi(Build.VERSION_CODES.Q)
    fun wifiRxLinkSpeed(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.rxLinkSpeedMbps != null) {
            info.rxLinkSpeedMbps.toString()
        } else {
            OFF
        }
    }

    // function to get max supported tx link speed wifi
    @RequiresApi(Build.VERSION_CODES.R)
    fun wifiMaxSupportedTxLinkSpeed(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.maxSupportedTxLinkSpeedMbps != null) {
            info.maxSupportedTxLinkSpeedMbps.toString()
        } else {
            OFF
        }
    }

    // function to get max supported rx link speed wifi
    @RequiresApi(Build.VERSION_CODES.R)
    fun wifiMaxSupportedRxLinkSpeed(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.maxSupportedRxLinkSpeedMbps != null) {
            info.maxSupportedTxLinkSpeedMbps.toString()
        } else {
            OFF
        }
    }

    // function to get Mobile Tx
    fun getWifiTx(): String {
        val txBytes: Long = TrafficStats.getTotalTxBytes() - wStartTX
        wStartTX = TrafficStats.getTotalTxBytes()
        return if (txBytes >= 1000000) {
            val result = (txBytes / 1000000)
            "$result MB"
        } else if (txBytes >= 1000) {
            val result = (txBytes / 1000)
            "$result KB"
        } else {
            "$txBytes B"
        }
        return ZERO
    }

    // function to get Mobile Rx
    fun getWifiRx(): String {
        val rxBytes: Long = TrafficStats.getTotalRxBytes() - wStartRX
        wStartRX = TrafficStats.getTotalRxBytes()
        return if (rxBytes >= 1000000) {
            val result = (rxBytes / 1000000)
            "$result MB"
        } else if (rxBytes >= 1000) {
            val result = (rxBytes / 1000)
            "$result KB"
        } else {
            "$rxBytes B"
        }
        return ZERO.toString()
    }

    // function to get frequency wifi
    fun wifiFrequency(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.frequency != null) {
            info.frequency.toString()
        } else {
            OFF
        }
    }

    // function to get standard wifi
    @RequiresApi(Build.VERSION_CODES.R)
    fun wifiStandard(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.wifiStandard != null) {
            info.wifiStandard.toString()
        } else {
            OFF
        }
    }

    // function to get rssi wifi
    fun wifiRssi(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return if (info.rssi != null) {
            info.rssi.toString()
        } else {
            OFF
        }
    }

    // function to get wap wifi
    fun wifiWap(wifiManager: WifiManager): String {
        val info = wifiManager.connectionInfo
        return try {
            if (info.bssid != null) {
                info.bssid.toString()
            } else {
                OFF
            }
        } catch (e: Exception) {
            OFF
        }
    }
}