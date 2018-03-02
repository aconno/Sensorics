package com.aconno.acnsensa.dagger

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v4.app.NotificationCompat
import com.aconno.acnsensa.AcnSensaNotificationChannel
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BluetoothScanningServiceReceiver
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class BluetoothScanningServiceModule(
    private val bluetoothScanningService: BluetoothScanningService
) {

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningService() = bluetoothScanningService

    @Provides
    @BluetoothScanningServiceScope
    fun provideNotification(): Notification {
        createNotificationsChannel(bluetoothScanningService)
        return NotificationCompat.Builder(
            bluetoothScanningService,
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

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(): BroadcastReceiver =
        BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.acnsensa.STOP")

}
