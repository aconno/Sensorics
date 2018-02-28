package com.aconno.acnsensa

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

//TODO: This needs refactoring.
private fun createNotificationsChannel(application: Context) {
    val notificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val statsNotificationChannel = MyNotificationChannel(notificationManager)

    statsNotificationChannel.create()
}

class BluetoothScanningService : Service() {


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    val receiver = BluetoothScanningServiceReceiver()
    val filter = IntentFilter(STOP)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("Startservice", "startservice")


        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, filter)

        val builder =
            NotificationCompat.Builder(
                this,
                MyNotificationChannel.CHANNEL_ID
            )
                .setContentTitle("Title")
                .setContentText("Text")
                .setAutoCancel(true)

        val notification: Notification = builder.build()
        startForeground(1, notification)

        val application: AcnSensaApplication? = application as? AcnSensaApplication
        application?.bluetooth?.startScanning()
        return START_STICKY
    }

    companion object {
        private const val STOP = "com.aconno.acnsensa.STOP"
        private const val DEFAULT_CHANNEL_ID: String = "con.aconno.acnsensa.DEFAULT_CHANNEL"

        fun start(context: Context) {
            Log.e("static start", "static start")
            createNotificationsChannel(context)
            val intent = Intent(context, BluetoothScanningService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    inner class BluetoothScanningServiceReceiver : BroadcastReceiver() {

        override fun onReceive(arg0: Context, intent: Intent) {
            val localBroadcastManager =
                LocalBroadcastManager.getInstance(this@BluetoothScanningService)
            localBroadcastManager.unregisterReceiver(receiver)
            val application: AcnSensaApplication? = application as? AcnSensaApplication
            application?.bluetooth?.stopScanning()
            stopSelf()
        }
    }

}

class MyNotificationChannel(private val notificationManager: NotificationManager) {

    fun create() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    IMPORTANCE
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        val CHANNEL_ID = "channel"
        private val CHANNEL_NAME = "Default"
        private val IMPORTANCE = 5
    }
}



