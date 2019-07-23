package com.aconno.sensorics.dagger.alarm

import android.app.Notification
import com.aconno.sensorics.AlarmService
import com.aconno.sensorics.R
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.AudioAlarmImpl
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.AudioAlarm
import com.aconno.sensorics.domain.DeviceAudioManager
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager
import dagger.Module
import dagger.Provides

@Module
class AlarmServiceModule {

    @Provides
    @AlarmServiceScope
    fun provideNotification(
        alarmService: AlarmService,
        intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = alarmService.getString(R.string.service_notification_title)
        val content = alarmService.getString(R.string.alarm_active)
        val snoozeIntent = intentProvider.getAlarmSnoozeIntent(alarmService.applicationContext)
        return notificationFactory.makeForegroundServiceNotificationWithAction(
            alarmService,
            snoozeIntent,
            title,
            content,
            snoozeIntent,
            alarmService.getString(R.string.stop_alarm),
            R.drawable.ic_action_notify_cancel
        )
    }

    @Provides
    @AlarmServiceScope
    fun proviceAudioAlarm(
        sensoricsApplication: SensoricsApplication,
        telephonyManager: DeviceTelephonyManager,
        audioManager: DeviceAudioManager
    ): AudioAlarm = AudioAlarmImpl(
        sensoricsApplication.applicationContext,
        telephonyManager,
        audioManager
    )
}