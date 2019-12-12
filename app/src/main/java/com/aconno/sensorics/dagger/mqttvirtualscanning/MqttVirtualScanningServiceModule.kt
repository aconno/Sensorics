package com.aconno.sensorics.dagger.mqttvirtualscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.MqttVirtualScanningService
import com.aconno.sensorics.MqttVirtualScanningServiceReceiver
import com.aconno.sensorics.R
import com.aconno.sensorics.device.mqtt.MqttVirtualScannerImpl
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

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
    @Singleton
    @MqttVirtualScanningServiceScope
    fun provideMqttVirtualScanner(): MqttVirtualScanner {
        return MqttVirtualScannerImpl()
    }


    @Provides
    @Singleton
    fun provideFilteredScanResult(
        mqttVirtualScanner: MqttVirtualScanner,
        filterByFormatUseCase: FilterByFormatUseCase
    ): Flowable<ScanResult> {
        return mqttVirtualScanner.getScanResults().filter { filterByFormatUseCase.execute(it) }
    }

    @Provides
    @Singleton
    fun provideReadings(
        filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
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