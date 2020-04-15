package com.aconno.sensorics.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.service.BluetoothScanningService
import com.aconno.sensorics.service.BluetoothScanningServiceReceiver
import com.aconno.sensorics.R
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import dagger.Module
import dagger.Provides

@Module
class BluetoothScanningServiceModule {

    @Provides
    @BluetoothScanningServiceScope
    fun provideNotification(
        bluetoothScanningService: BluetoothScanningService,
        intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = bluetoothScanningService.getString(R.string.service_notification_title)
        val content = bluetoothScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
            bluetoothScanningService,
            intentProvider.getSensoricsContentIntent(bluetoothScanningService),
            title,
            content
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(
        bluetoothScanningService: BluetoothScanningService
    ): BroadcastReceiver =
        BluetoothScanningServiceReceiver(
            bluetoothScanningService
        )

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.sensorics.STOP")
}