package com.aconno.sensorics

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.aconno.sensorics.device.notification.AlarmNotificationReceiver
import com.aconno.sensorics.device.notification.AlertNotificationReceiver
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.AlarmServiceController
import com.aconno.sensorics.ui.MainActivity

class IntentProviderImpl : IntentProvider {

    override fun getSensoricsContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val requestCode = 0
        return PendingIntent.getActivity(
            context,
            requestCode,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationDeleteIntent(context: Context,notificationId : Int): PendingIntent {
        val outcome = Intent(context, AlertNotificationReceiver::class.java)

        outcome.action = AlertNotificationReceiver.DISMISS
        outcome.putExtra(
            NotificationFactory.ALERT_NOTIFICATION_NAME,
            notificationId
        )

        val requestCode = 0
        val flags = 0
        return PendingIntent.getBroadcast(context, requestCode, outcome, flags)
    }

    override fun getAlarmSnoozeIntent(context: Context): PendingIntent {
        val outcome = Intent(context, AlarmNotificationReceiver::class.java)

        outcome.action = AlarmServiceController.ACTION_ALARM_SNOOZE

        val requestCode = 0
        val flags = 0
        return PendingIntent.getBroadcast(context, requestCode, outcome, flags)
    }
}