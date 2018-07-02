package com.aconno.acnsensa.device.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * @author aconno
 */
class NotificationChannelFactory(private val notificationManager: NotificationManager) {

    fun makeServiceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun makeAlertsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    ALERTS_CHANNEL_ID,
                    ALERTS_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val CHANNEL_ID = "channel"
        const val ALERTS_CHANNEL_ID = "alertsChannel"
        private const val CHANNEL_NAME = "AcnSensa General"
        private const val ALERTS_CHANNEL_NAME = "AcnSensa Alerts"
        const val ALERTS_CHANNEL = 0
        const val SERVICE_CHANNEL = 1
    }
}