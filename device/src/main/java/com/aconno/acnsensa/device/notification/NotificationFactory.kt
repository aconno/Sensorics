package com.aconno.acnsensa.device.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat

/**
 * @aconno
 */
class NotificationFactory {
    fun makeForegroundServiceNotification(context: Context): Notification {
        createNotificationsChannel(context)
        return NotificationCompat.Builder(
            context,
            AcnSensaNotificationChannel.CHANNEL_ID
        )
            .setContentTitle("Title")
            .setContentText("Text")
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationsChannel(application: Context) {
        val notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val statsNotificationChannel = AcnSensaNotificationChannel(notificationManager)

        statsNotificationChannel.create()
    }
}