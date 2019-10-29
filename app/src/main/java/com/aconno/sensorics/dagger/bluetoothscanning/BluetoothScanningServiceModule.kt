package com.aconno.sensorics.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BluetoothScanningServiceReceiver
import com.aconno.sensorics.R
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.device.storage.FileStorageImpl
import com.aconno.sensorics.domain.AlarmServiceController
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.*
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllEnabledGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllEnabledRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetRestHeadersByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.GetRestHttpGetParamsByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveSensorReadingsUseCase
import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.time.TimeProvider
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
        BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.sensorics.STOP")



}