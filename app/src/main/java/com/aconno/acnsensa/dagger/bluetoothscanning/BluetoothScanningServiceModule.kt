package com.aconno.acnsensa.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BluetoothScanningServiceReceiver
import com.aconno.acnsensa.R
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.device.storage.FileStorageImpl
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.domain.ifttt.outcome.*
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.*
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.SaveSensorReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.SensorValuesToReadingsUseCase
import com.aconno.acnsensa.domain.repository.InMemoryRepository
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
    fun provideNotification(
        acnSensaApplication: AcnSensaApplication,
        intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = bluetoothScanningService.getString(R.string.service_notification_title)
        val content = bluetoothScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
            bluetoothScanningService,
            intentProvider.getAcnSensaContentIntent(acnSensaApplication),
            title,
            content
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(): BroadcastReceiver =
        BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.acnsensa.STOP")

    @Provides
    @BluetoothScanningServiceScope
    fun provideRecordSensorValuesUseCase(
        inMemoryRepository: InMemoryRepository
    ): SaveSensorReadingsUseCase {
        return SaveSensorReadingsUseCase(inMemoryRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideSensorValuesToReadingsUseCase(): SensorValuesToReadingsUseCase {
        return SensorValuesToReadingsUseCase()

    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideLogReadingsUseCase(): LogReadingUseCase {
        return LogReadingUseCase(FileStorageImpl(bluetoothScanningService))
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideHandleInputUseCase(actionsRepository: ActionsRepository): InputToOutcomesUseCase {
        return InputToOutcomesUseCase(actionsRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideRunOutcomeUseCase(
        notificationDisplay: NotificationDisplay,
        smsSender: SmsSender,
        textToSpeechPlayer: TextToSpeechPlayer,
        vibrator: Vibrator
    ): RunOutcomeUseCase {
        val notificationOutcomeExecutor = NotificationOutcomeExecutor(notificationDisplay)
        val smsOutcomeExecutor = SmsOutcomeExecutor(smsSender)
        val textToSpeechOutcomeExecutor = TextToSpeechOutcomeExecutor(textToSpeechPlayer)
        val vibrationOutcomeExecutor = VibrationOutcomeExecutor(vibrator)
        val outcomeExecutorSelector = OutcomeExecutorSelector(
            notificationOutcomeExecutor,
            smsOutcomeExecutor,
            textToSpeechOutcomeExecutor,
            vibrationOutcomeExecutor
        )

        return RunOutcomeUseCase(outcomeExecutorSelector)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllEnabledGooglePublishUseCase {
        return GetAllEnabledGooglePublishUseCase(googlePublishRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledRESTPublishUseCase(restPublishRepository: RESTPublishRepository): GetAllEnabledRESTPublishUseCase {
        return GetAllEnabledRESTPublishUseCase(restPublishRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideUpdateRESTPublishUseCase(restPublishRepository: RESTPublishRepository): UpdateRESTPublishUserCase {
        return UpdateRESTPublishUserCase(restPublishRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideUpdateGooglePublishUseCase(restPublishRepository: GooglePublishRepository): UpdateGooglePublishUseCase {
        return UpdateGooglePublishUseCase(restPublishRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository: PublishDeviceJoinRepository): GetDevicesThatConnectedWithRESTPublishUseCase {
        return GetDevicesThatConnectedWithRESTPublishUseCase(publishDeviceJoinRepository)
    }
}