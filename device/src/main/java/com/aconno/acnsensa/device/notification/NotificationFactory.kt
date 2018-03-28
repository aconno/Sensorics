package com.aconno.acnsensa.device.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.aconno.acnsensa.device.R
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay

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

    fun makeAlertNotification(
        context: Context,
        message: String,
        deleteIntent: PendingIntent,
        contentIntent: PendingIntent
    ): Notification {
        createNotificationsChannel(context)
        return NotificationCompat.Builder(context, AcnSensaNotificationChannel.CHANNEL_ID)
            .setContentTitle("Sensor Alert")
            .setContentText(message)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setAutoCancel(true)
            .setDeleteIntent(deleteIntent)
            .setContentIntent(contentIntent)
            .build()
    }

    companion object {
        const val ALERT_NOTIFICATION_NAME = "com.aconno.acnsensa.ALERT_NOTIFICATION"
        const val ALERT_NOTIFICATION_ID = 100
    }

}

class NotificationDisplayImpl(
    private val notificationFactory: NotificationFactory,
    private val intentProvider: IntentProvider,
    private val context: Context
) : NotificationDisplay {
    override fun displayAlertNotification(message: String) {
        display(
            context,
            notificationFactory.makeAlertNotification(
                context,
                message,
                intentProvider.getAlertNotificationDeleteIntent(context),
                intentProvider.getAlertNotificationContentIntent(context)
            ),
            NotificationFactory.ALERT_NOTIFICATION_ID
        )
    }

    fun display(context: Context, notification: Notification, notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(notificationId, notification)
    }
}

class AlertNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val id = intent?.getIntExtra(NotificationFactory.ALERT_NOTIFICATION_NAME, -1)

        when (action) {
            DISMISS -> id?.let { handleDismiss(context, it) }
        }
    }

    private fun handleDismiss(context: Context?, notificationId: Int) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancel(notificationId)

    }

    companion object {
        const val DISMISS = "com.aconno.acnsensa.action.ALERT_CANCEL"
    }
}

interface IntentProvider {
    fun getAlertNotificationContentIntent(context: Context): PendingIntent
    fun getAlertNotificationDeleteIntent(context: Context): PendingIntent
}

