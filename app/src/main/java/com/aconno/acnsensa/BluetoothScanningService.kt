package com.aconno.acnsensa

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.domain.Bluetooth
import javax.inject.Inject

/**
 * @author aconno
 */
class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    val receiver = BluetoothScanningServiceReceiver()
    private val filter = IntentFilter(STOP)


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(receiver, filter)

        val builder =
            NotificationCompat.Builder(
                this,
                AcnSensaNotificationChannel.CHANNEL_ID
            )
                .setContentTitle("Title")
                .setContentText("Text")
                .setAutoCancel(true)

        val notification: Notification = builder.build()
        startForeground(1, notification)

        val application: AcnSensaApplication? = application as? AcnSensaApplication
        application?.appComponent?.inject(this)

        bluetooth.startScanning()
        return START_STICKY
    }

    companion object {
        private const val STOP = "com.aconno.acnsensa.STOP"
        private const val DEFAULT_CHANNEL_ID: String = "con.aconno.acnsensa.DEFAULT_CHANNEL"

        fun start(context: Context) {
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
            bluetooth.stopScanning()
            stopSelf()
        }
    }

}




