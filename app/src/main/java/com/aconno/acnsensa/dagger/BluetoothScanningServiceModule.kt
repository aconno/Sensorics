package com.aconno.acnsensa.dagger

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BluetoothScanningServiceReceiver
import com.aconno.acnsensa.data.mqtt.AconnoCumulocityPublisher
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.device.storage.FileStorageImpl
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.SyncReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.RecordSensorValuesUseCase
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
    fun provideNotification(): Notification {
        val notificationFactory = NotificationFactory()
        return notificationFactory.makeForegroundServiceNotification(bluetoothScanningService)
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
    ): RecordSensorValuesUseCase {
        return RecordSensorValuesUseCase(inMemoryRepository)
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
    fun providePublisher(): Publisher {
        //TODO: Do not hardcode user name and password
        val username = ""
        val password = ""
        return AconnoCumulocityPublisher(bluetoothScanningService, username, password)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideSyncReadingsUseCase(publisher: Publisher): SyncReadingsUseCase {
        return SyncReadingsUseCase(publisher)
    }
}