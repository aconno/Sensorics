package com.aconno.acnsensa.dagger.application

import android.arch.persistence.room.Room
import android.bluetooth.BluetoothAdapter
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.IntentProviderImpl
import com.aconno.acnsensa.data.mapper.*
import com.aconno.acnsensa.data.repository.AcnSensaDatabase
import com.aconno.acnsensa.data.repository.InMemoryRepositoryImpl
import com.aconno.acnsensa.data.repository.action.ActionsRepositoryImpl
import com.aconno.acnsensa.data.repository.devices.DeviceMapper
import com.aconno.acnsensa.data.repository.devices.DeviceRepositoryImpl
import com.aconno.acnsensa.data.repository.gpublish.GooglePublishRepositoryImpl
import com.aconno.acnsensa.data.repository.mpublish.MqttPublishRepositoryImpl
import com.aconno.acnsensa.data.repository.pdjoin.PublishDeviceJoinRepositoryImpl
import com.aconno.acnsensa.data.repository.rpublish.RESTPublishRepositoryImpl
import com.aconno.acnsensa.device.SmsSenderImpl
import com.aconno.acnsensa.device.TextToSpeechPlayerImpl
import com.aconno.acnsensa.device.VibratorImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.device.bluetooth.BluetoothPermissionImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothStateListener
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.device.notification.NotificationDisplayImpl
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.format.AdvertisementFormat
import com.aconno.acnsensa.domain.format.FormatMatcher
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.domain.interactor.consolidation.GenerateDeviceUseCase
import com.aconno.acnsensa.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.acnsensa.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.acnsensa.domain.interactor.filter.FilterByMacUseCase
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.domain.serialization.Deserializer
import com.aconno.acnsensa.domain.serialization.DeserializerImpl
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

@Module
class AppModule(
    private val acnSensaApplication: AcnSensaApplication,
    private val supportedFormats: List<AdvertisementFormat>
) {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() =
        LocalBroadcastManager.getInstance(acnSensaApplication.applicationContext)

    @Provides
    @Singleton
    fun provideBluetoothStateReceiver(bluetoothStateListener: BluetoothStateListener) =
        BluetoothStateReceiver(bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothStateListener() = BluetoothStateListener()

    @Provides
    @Singleton
    fun provideBluetooth(
        application: AcnSensaApplication,
        bluetoothAdapter: BluetoothAdapter,
        bluetoothPermission: BluetoothPermission,
        bluetoothStateListener: BluetoothStateListener
    ): Bluetooth =
        BluetoothImpl(application, bluetoothAdapter, bluetoothPermission, bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Provides
    @Singleton
    fun provideBluetoothPermission(): BluetoothPermission = BluetoothPermissionImpl()

    @Provides
    @Singleton
    fun provideAcnSensaApplication(): AcnSensaApplication = acnSensaApplication

    @Provides
    @Singleton
    fun provideInMemoryRepository(): InMemoryRepository = InMemoryRepositoryImpl()

    @Provides
    @Singleton
    fun provideVibrator(): Vibrator {
        return VibratorImpl(acnSensaApplication.applicationContext)
    }

    @Provides
    @Singleton
    fun provideSmsSender(): SmsSender {
        return SmsSenderImpl()
    }

    @Provides
    @Singleton
    fun provideTextToSpeechPlayer(): TextToSpeechPlayer {
        return TextToSpeechPlayerImpl(acnSensaApplication)
    }

    @Provides
    @Singleton
    fun provideActionsRepository(
        acnSensaDatabase: AcnSensaDatabase
    ): ActionsRepository {
        return ActionsRepositoryImpl(acnSensaDatabase.actionDao())
    }

    @Provides
    @Singleton
    fun provideGooglePublishRepository(
        acnSensaDatabase: AcnSensaDatabase,
        googlePublishEntityDataMapper: GooglePublishEntityDataMapper,
        googlePublishDataMapper: GooglePublishDataMapper
    ): GooglePublishRepository {
        return GooglePublishRepositoryImpl(
            acnSensaDatabase.googlePublishDao(),
            googlePublishEntityDataMapper,
            googlePublishDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideRESTPublishRepository(
        acnSensaDatabase: AcnSensaDatabase,
        restPublishEntityDataMapper: RESTPublishEntityDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restHeaderDataMapper: RESTHeaderDataMapper
    ): RESTPublishRepository {
        return RESTPublishRepositoryImpl(
            acnSensaDatabase.restPublishDao(),
            restPublishEntityDataMapper,
            restPublishDataMapper,
            restHeaderDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideMqttPublishRepository(
        acnSensaDatabase: AcnSensaDatabase,
        mqttPublishDataMapper: MqttPublishDataMapper
    ): MqttPublishRepository {
        return MqttPublishRepositoryImpl(
            acnSensaDatabase.mqttPublishDao(),
            mqttPublishDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideAcnSensaDatabase(): AcnSensaDatabase {
        return Room.databaseBuilder(acnSensaApplication, AcnSensaDatabase::class.java, "AcnSensa")
            .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    @Singleton
    fun provideNotificationDisplay(intentProvider: IntentProvider): NotificationDisplay {
        return NotificationDisplayImpl(
            NotificationFactory(), intentProvider, acnSensaApplication
        )
    }

    @Provides
    @Singleton
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(
        acnSensaDatabase: AcnSensaDatabase,
        deviceMapper: DeviceMapper
    ): DeviceRepository {
        return DeviceRepositoryImpl(acnSensaDatabase.deviceDao(), deviceMapper)
    }

    @Provides
    @Singleton
    fun provideGetSavedDevicesList(
        deviceRepository: DeviceRepository
    ): Flowable<List<Device>> {
        return GetSavedDevicesUseCase(deviceRepository).execute()
    }

    @Provides
    @Singleton
    fun provideReadingToInputUseCase() = ReadingToInputUseCase()

    @Provides
    @Singleton
    fun providePublishDeviceJoinRepository(
        acnSensaDatabase: AcnSensaDatabase,
        deviceMapper: DeviceMapper,
        publishDeviceJoinJoinMapper: PublishPublishDeviceJoinJoinMapper
    ): PublishDeviceJoinRepository {
        return PublishDeviceJoinRepositoryImpl(
            acnSensaDatabase.publishDeviceJoinDao(),
            deviceMapper,
            publishDeviceJoinJoinMapper
        )
    }

    @Provides
    @Singleton
    fun provideFormatMatcher() = FormatMatcher(supportedFormats)

    @Provides
    @Singleton
    fun provideDeserializer(): Deserializer = DeserializerImpl()

    @Provides
    @Singleton
    fun provideFilterByFormatUseCase(
        formatMatcher: FormatMatcher
    ) = FilterByFormatUseCase(formatMatcher)

    @Provides
    @Singleton
    fun provideFilterByMacUseCase() = FilterByMacUseCase()

    @Provides
    @Singleton
    fun provideGenerateDeviceUseCase(
        formatMatcher: FormatMatcher
    ) = GenerateDeviceUseCase(formatMatcher)

    @Provides
    @Singleton
    fun provideGenerateReadingsUseCase(
        formatMatcher: FormatMatcher,
        deserializer: Deserializer
    ) = GenerateReadingsUseCase(formatMatcher, deserializer)

    @Provides
    @Singleton
    fun provideFilteredScanResult(
        bluetooth: Bluetooth,
        filterByFormatUseCase: FilterByFormatUseCase
    ): Flowable<ScanResult> {
        return bluetooth.getScanResults()
            .concatMap { filterByFormatUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Singleton
    fun provideDevice(
        filteredScanResult: Flowable<ScanResult>,
        generateDeviceUseCase: GenerateDeviceUseCase
    ): Flowable<Device> {
        return filteredScanResult.concatMap { generateDeviceUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Singleton
    fun provideReadings(
        filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
    }
}