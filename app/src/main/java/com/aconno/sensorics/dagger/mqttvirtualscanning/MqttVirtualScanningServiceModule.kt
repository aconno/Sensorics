package com.aconno.sensorics.dagger.mqttvirtualscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.MqttVirtualScanningService
import com.aconno.sensorics.MqttVirtualScanningServiceReceiver
import com.aconno.sensorics.R
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import dagger.Module
import dagger.Provides

@Module
class MqttVirtualScanningServiceModule {

    @Provides
    @MqttVirtualScanningServiceScope
    fun provideNotification(
        mqttVirtualScanningService: MqttVirtualScanningService,
        intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = mqttVirtualScanningService.getString(R.string.service_notification_title)
        val content = mqttVirtualScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
            mqttVirtualScanningService,
            intentProvider.getSensoricsContentIntent(mqttVirtualScanningService),
            title,
            content
        )
    }

    @Provides
    @MqttVirtualScanningServiceScope
    fun provideMqttVirtualScanningServiceReceiver(
        mqttVirtualScanningService: MqttVirtualScanningService
    ): BroadcastReceiver =
        MqttVirtualScanningServiceReceiver(mqttVirtualScanningService)

    @Provides
    @MqttVirtualScanningServiceScope
    fun provideMqttVirtualScanningReceiverIntentFilter() = IntentFilter(MqttVirtualScanningService.STOP)

}