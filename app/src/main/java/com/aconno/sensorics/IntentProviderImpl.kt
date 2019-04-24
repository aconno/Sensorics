package com.aconno.sensorics

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.aconno.sensorics.device.notification.AlertNotificationReceiver
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.ui.MainActivity2

class IntentProviderImpl : IntentProvider {

    override fun getSensoricsContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, MainActivity2::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, MainActivity2::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val requestCode = 0
        return PendingIntent.getActivity(
            context,
            requestCode,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationDeleteIntent(context: Context): PendingIntent {
        val outcome = Intent(context, AlertNotificationReceiver::class.java)

        outcome.action = AlertNotificationReceiver.DISMISS
        outcome.putExtra(
            NotificationFactory.ALERT_NOTIFICATION_NAME,
            NotificationFactory.ALERT_NOTIFICATION_ID
        )

        val requestCode = 0
        val flags = 0
        return PendingIntent.getBroadcast(context, requestCode, outcome, flags)
    }
}