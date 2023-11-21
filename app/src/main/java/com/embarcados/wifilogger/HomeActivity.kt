package com.embarcados.wifilogger

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.embarcados.wifilogger.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.embarcados.wifilogger.service.Service

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val PERMISSIONS_REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissions = arrayOf(
        ACCESS_FINE_LOCATION,
        POST_NOTIFICATIONS,
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        permissions()
        startService(Intent(applicationContext, Service::class.java))
    }

    private fun permissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSIONS_REQUEST_CODE)
            }
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                this.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            }
            val appOps =
                this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                this.packageName
            )
            if (mode != AppOpsManager.MODE_ALLOWED) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                ActivityCompat.startActivityForResult(
                    this as Activity,
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, uri),
                    1,
                    null
                )
            }
            if (!Settings.canDrawOverlays(this)) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                ActivityCompat.startActivityForResult(
                    this as Activity,
                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri),
                    1,
                    null
                )
            }
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
    }

    private fun startServiceIfNotRunning() {
        val serviceIntent = Intent(applicationContext, Service::class.java)

        if (!isServiceRunning(Service::class.java)) {
            startService(serviceIntent)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startServiceIfNotRunning()
            } else {
                // Alguma permissão foi negada. Lide com isso conforme necessário.
                // Por exemplo, exiba uma mensagem ou encerre o aplicativo.
            }
        }
    }
}