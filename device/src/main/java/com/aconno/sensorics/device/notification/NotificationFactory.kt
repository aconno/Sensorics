package com.aconno.sensorics.device.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.device.R
import com.aconno.sensorics.domain.AlarmServiceController.Companion.ACTION_ALARM_SNOOZE
import com.aconno.sensorics.domain.ifttt.NotificationDisplay

/**
 * @aconno
 */
class NotificationFactory {
    fun makeForegroundServiceNotification(
        context: Context,
        contentIntent: PendingIntent,
        title: String,
        contentText: String
    ): Notification {
        createNotificationsChannel(context, NotificationChannelFactory.SERVICE_CHANNEL)
        return NotificationCompat.Builder(
            context,
            NotificationChannelFactory.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
    }

    fun makeForegroundServiceNotificationWithAction(
        context: Context,
        contentIntent: PendingIntent,
        title: String,
        contentText: String,
        actionIntent: PendingIntent,
        actionTitle: String,
        actionIcon: Int
    ): Notification {
        createNotificationsChannel(context, NotificationChannelFactory.SERVICE_CHANNEL)
        return NotificationCompat.Builder(
            context,
            NotificationChannelFactory.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .addAction(actionIcon, actionTitle, actionIntent)
            .build()
    }

    private fun createNotificationsChannel(application: Context, channelType: Int) {
        val notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val statsNotificationChannel = NotificationChannelFactory(notificationManager)

        when (channelType) {
            NotificationChannelFactory.ALERTS_CHANNEL ->
                statsNotificationChannel.makeAlertsChannel()
            NotificationChannelFactory.SERVICE_CHANNEL ->
                statsNotificationChannel.makeServiceNotificationChannel()
        }
    }

    //TODO: This method needs to get a configuration object which contains all the settings.
    fun makeAlertNotification(
        context: Context,
        message: String,
        deleteIntent: PendingIntent,
        contentIntent: PendingIntent
    ): Notification {
        createNotificationsChannel(context, NotificationChannelFactory.ALERTS_CHANNEL)
        return NotificationCompat.Builder(context, NotificationChannelFactory.ALERTS_CHANNEL_ID)
            .setContentTitle("Sensorics")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .setDeleteIntent(deleteIntent)
            .setContentIntent(contentIntent)
            .build()
    }

    companion object {
        const val ALERT_NOTIFICATION_NAME = "com.aconno.sensorics.ALERT_NOTIFICATION"
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

    private fun display(context: Context, notification: Notification, notificationId: Int) {
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
        const val DISMISS = "com.aconno.sensorics.action.ALERT_CANCEL"
    }
}

class AlarmNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.also { ctx ->
            when (intent?.action) {
                ACTION_ALARM_SNOOZE -> LocalBroadcastManager.getInstance(
                    ctx
                ).sendBroadcast(Intent(ACTION_ALARM_SNOOZE))
            }
        }
    }
}

interface IntentProvider {

    fun getSensoricsContentIntent(context: Context): PendingIntent

    fun getAlertNotificationContentIntent(context: Context): PendingIntent

    fun getAlertNotificationDeleteIntent(context: Context): PendingIntent

    fun getAlarmSnoozeIntent(context: Context): PendingIntent
}

