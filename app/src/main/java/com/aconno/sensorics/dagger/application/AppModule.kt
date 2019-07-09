package com.aconno.sensorics.dagger.application

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.aconno.sensorics.IntentProviderImpl
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.mapper.*
import com.aconno.sensorics.data.repository.InMemoryRepositoryImpl
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.action.ActionsRepositoryImpl
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.data.repository.devices.DeviceRepositoryImpl
import com.aconno.sensorics.data.repository.googlepublish.GooglePublishRepositoryImpl
import com.aconno.sensorics.data.repository.mqttpublish.MqttPublishRepositoryImpl
import com.aconno.sensorics.data.repository.publishdevicejoin.PublishDeviceJoinRepositoryImpl
import com.aconno.sensorics.data.repository.restpublish.RestPublishRepositoryImpl
import com.aconno.sensorics.data.repository.sync.SyncDao
import com.aconno.sensorics.data.repository.sync.SyncRepositoryImpl
import com.aconno.sensorics.device.TextToSpeechPlayerImpl
import com.aconno.sensorics.device.VibratorImpl
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationDisplayImpl
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinderImpl
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.domain.serialization.Deserializer
import com.aconno.sensorics.domain.serialization.DeserializerImpl
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager(sensoricsApplication: SensoricsApplication) =
        LocalBroadcastManager.getInstance(sensoricsApplication.applicationContext)

    @Provides
    @Singleton
    fun provideInMemoryRepository(): InMemoryRepository = InMemoryRepositoryImpl()

    @Provides
    @Singleton
    fun provideSharedPreferences(sensoricsApplication: SensoricsApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(sensoricsApplication)
    }

    @Provides
    @Singleton
    fun provideConnectionCharacteristicsFinder(
        getFormatsUseCase: GetFormatsUseCase
    ): ConnectionCharacteristicsFinder {
        return ConnectionCharacteristicsFinderImpl(getFormatsUseCase)
    }

    @Provides
    @Singleton
    fun provideVibrator(sensoricsApplication: SensoricsApplication): Vibrator {
        return VibratorImpl(sensoricsApplication.applicationContext)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechPlayer(sensoricsApplication: SensoricsApplication): TextToSpeechPlayer {
        return TextToSpeechPlayerImpl(sensoricsApplication)
    }

    @Provides
    @Singleton
    fun provideActionsRepository(
        sensoricsDatabase: SensoricsDatabase
    ): ActionsRepository {
        return ActionsRepositoryImpl(sensoricsDatabase.actionDao())
    }

    @Provides
    @Singleton
    fun provideGooglePublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        googlePublishEntityDataMapper: GooglePublishEntityDataMapper,
        googlePublishDataMapper: GooglePublishDataMapper
    ): GooglePublishRepository {
        return GooglePublishRepositoryImpl(
            sensoricsDatabase.googlePublishDao(),
            googlePublishEntityDataMapper,
            googlePublishDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideRESTPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        restPublishEntityDataMapper: RestPublishEntityDataMapper,
        restPublishDataMapper: RestPublishDataMapper,
        restHeaderDataMapper: RestHeaderDataMapper,
        restHttpGetParamDataMapper: RestHttpGetParamDataMapper
    ): RestPublishRepository {
        return RestPublishRepositoryImpl(
            sensoricsDatabase.restPublishDao(),
            restPublishEntityDataMapper,
            restPublishDataMapper,
            restHeaderDataMapper,
            restHttpGetParamDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideMqttPublishRepository(
        sensoricsDatabase: SensoricsDatabase,
        mqttPublishDataMapper: MqttPublishDataMapper
    ): MqttPublishRepository {
        return MqttPublishRepositoryImpl(
            sensoricsDatabase.mqttPublishDao(),
            mqttPublishDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideSensoricsDatabase(sensoricsApplication: SensoricsApplication): SensoricsDatabase {
        return Room.databaseBuilder(
            sensoricsApplication,
            SensoricsDatabase::class.java,
            "Sensorics"
        )
            .addMigrations(SensoricsDatabase.MIGRATION_11_12)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationDisplay(
        sensoricsApplication: SensoricsApplication,
        intentProvider: IntentProvider
    ): NotificationDisplay {
        return NotificationDisplayImpl(
            NotificationFactory(), intentProvider, sensoricsApplication
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
        sensoricsDatabase: SensoricsDatabase,
        deviceMapper: DeviceMapper
    ): DeviceRepository {
        return DeviceRepositoryImpl(sensoricsDatabase.deviceDao(), deviceMapper)
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
    fun provideGetSavedDevicesMaybeUseCase(
        deviceRepository: DeviceRepository
    ): GetSavedDevicesMaybeUseCase {
        return GetSavedDevicesMaybeUseCase(deviceRepository)
    }

    @Provides
    @Singleton
    fun provideReadingToInputUseCase() = ReadingToInputUseCase()

    @Provides
    @Singleton
    fun providePublishDeviceJoinRepository(
        sensoricsDatabase: SensoricsDatabase,
        deviceMapper: DeviceMapper,
        publishDeviceJoinMapper: PublishDeviceJoinMapper
    ): PublishDeviceJoinRepository {
        return PublishDeviceJoinRepositoryImpl(
            sensoricsDatabase.publishDeviceJoinDao(),
            deviceMapper,
            publishDeviceJoinMapper
        )
    }

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
    fun provideGenerateScanDeviceUseCase(
        formatMatcher: FormatMatcher
    ) = GenerateScanDeviceUseCase(formatMatcher)

    @Provides
    @Singleton
    fun provideGenerateReadingsUseCase(
        formatMatcher: FormatMatcher,
        deserializer: Deserializer
    ) = GenerateReadingsUseCase(formatMatcher, deserializer)

    @Provides
    @Singleton
    fun provideGson() =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    @Provides
    @Singleton
    fun provideGetDevicesThatConnectedWithGooglePublishUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithGooglePublishUseCase {
        return GetDevicesThatConnectedWithGooglePublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @Singleton
    fun provideGetDevicesThatConnectedWithRESTPublishUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithRestPublishUseCase {
        return GetDevicesThatConnectedWithRestPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @Singleton
    fun provideGetDevicesThatConnectedWithMqttPublishUseCase(
        publishDeviceJoinRepository: PublishDeviceJoinRepository
    ): GetDevicesThatConnectedWithMqttPublishUseCase {
        return GetDevicesThatConnectedWithMqttPublishUseCase(publishDeviceJoinRepository)
    }

    @Provides
    @Singleton
    fun provideUpdatePublishUseCase(
        googlePublishRepository: GooglePublishRepository,
        mqttPublishRepository: MqttPublishRepository,
        restPublishRepository: RestPublishRepository
    ): UpdatePublishUseCase =
        UpdatePublishUseCase(googlePublishRepository, mqttPublishRepository, restPublishRepository)

    @Provides
    @Singleton
    fun provideSyncDao(database: SensoricsDatabase): SyncDao = database.syncDao()

    @Provides
    @Singleton
    fun provideSyncRepository(dao: SyncDao): SyncRepository = SyncRepositoryImpl(dao)
}