package com.aconno.sensorics.dagger.mqttvirtualscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.sensorics.MqttVirtualScanningService
import com.aconno.sensorics.MqttVirtualScanningServiceReceiver
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
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveSensorReadingsUseCase
import com.aconno.sensorics.domain.interactor.time.GetLocalTimeOfDayInSecondsUseCase
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.time.TimeProvider
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

    @Provides
    @MqttVirtualScanningServiceScope
    fun provideRecordSensorValuesUseCase(
        inMemoryRepository: InMemoryRepository
    ): SaveSensorReadingsUseCase {
        return SaveSensorReadingsUseCase(inMemoryRepository)
    }

    @Provides
    @MqttVirtualScanningServiceScope
    fun provideLogReadingsUseCase(
        mqttVirtualScanningService: MqttVirtualScanningService
    ): LogReadingUseCase {
        return LogReadingUseCase(FileStorageImpl(mqttVirtualScanningService))
    }


    @Provides
    @MqttVirtualScanningServiceScope
    fun provideHandleInputUseCase(
        actionsRepository: ActionsRepository,
        getLocalTimeOfDayInSecondsUseCase: GetLocalTimeOfDayInSecondsUseCase
    ): InputToOutcomesUseCase {
        return InputToOutcomesUseCase(actionsRepository, getLocalTimeOfDayInSecondsUseCase)
    }


    @Provides
    @MqttVirtualScanningServiceScope
    fun provideRunOutcomeUseCase(
        notificationDisplay: NotificationDisplay,
        textToSpeechPlayer: TextToSpeechPlayer,
        vibrator: Vibrator,
        alarmServiceController: AlarmServiceController
    ): RunOutcomeUseCase {
        val notificationOutcomeExecutor = NotificationOutcomeExecutor(notificationDisplay)
        val textToSpeechOutcomeExecutor = TextToSpeechOutcomeExecutor(textToSpeechPlayer)
        val vibrationOutcomeExecutor = VibrationOutcomeExecutor(vibrator)
        val alarmOutcomeExecutor = AlarmOutcomeExecutor(alarmServiceController)
        val outcomeExecutorSelector = OutcomeExecutorSelector(
            notificationOutcomeExecutor,
            textToSpeechOutcomeExecutor,
            vibrationOutcomeExecutor,
            alarmOutcomeExecutor
        )

        return RunOutcomeUseCase(outcomeExecutorSelector)
    }

}