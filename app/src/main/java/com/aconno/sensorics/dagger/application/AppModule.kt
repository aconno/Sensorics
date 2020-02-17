package com.aconno.sensorics.dagger.application

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import com.aconno.sensorics.AlarmServiceControllerImpl
import com.aconno.sensorics.IntentProviderImpl
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.dagger.time.TimeModule
import com.aconno.sensorics.data.repository.InMemoryRepositoryImpl
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.sync.SyncDao
import com.aconno.sensorics.data.repository.sync.SyncRepositoryImpl
import com.aconno.sensorics.device.DeviceAudioManagerImpl
import com.aconno.sensorics.device.DeviceTelephonyManagerImpl
import com.aconno.sensorics.device.TextToSpeechPlayerImpl
import com.aconno.sensorics.device.VibratorImpl
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.device.notification.NotificationDisplayImpl
import com.aconno.sensorics.device.notification.NotificationFactory
import com.aconno.sensorics.domain.AlarmServiceController
import com.aconno.sensorics.domain.DeviceAudioManager
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinderImpl
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.NotificationDisplay
import com.aconno.sensorics.domain.ifttt.TextToSpeechPlayer
import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.resources.GetFormatsUseCase
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.domain.serialization.Deserializer
import com.aconno.sensorics.domain.serialization.DeserializerImpl
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module (includes = [TimeModule::class])
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
    fun provideAlarmServiceController(
        sensoricsApplication: SensoricsApplication,
        broadcastManager: LocalBroadcastManager
    ): AlarmServiceController = AlarmServiceControllerImpl(
        sensoricsApplication.applicationContext,
        broadcastManager
    )

    @Provides
    @Singleton
    fun provideSensoricsDatabase(sensoricsApplication: SensoricsApplication): SensoricsDatabase {
        return Room.databaseBuilder(
            sensoricsApplication,
            SensoricsDatabase::class.java,
            "Sensorics"
        )
            .addMigrations(SensoricsDatabase.MIGRATION_11_12)
            .addMigrations(SensoricsDatabase.MIGRATION_12_13)
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
    fun provideReadingToInputUseCase() = ReadingToInputUseCase()

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
    fun provideSyncDao(database: SensoricsDatabase): SyncDao = database.syncDao()

    @Provides
    @Singleton
    fun provideSyncRepository(dao: SyncDao): SyncRepository = SyncRepositoryImpl(dao)


    @Provides
    @Singleton
    fun provideDeviceAudioManager(
        sensoricsApplication: SensoricsApplication
    ): DeviceAudioManager = DeviceAudioManagerImpl(
        sensoricsApplication.applicationContext
    )

    @Provides
    @Singleton
    fun provideDeviceTelephonyManager(
        sensoricsApplication: SensoricsApplication
    ): DeviceTelephonyManager = DeviceTelephonyManagerImpl(
        sensoricsApplication.applicationContext
    )
}