package com.aconno.sensorics.dagger.application

import android.content.SharedPreferences
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.data.repository.SettingsImpl
import com.aconno.sensorics.device.storage.FileStorageImpl
import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.data.*
import com.aconno.sensorics.domain.repository.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideSettings(sharedPreferences: SharedPreferences): Settings =
        SettingsImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideFileStorage(
        sensoricsApplication: SensoricsApplication
    ): FileStorage {
        return FileStorageImpl(sensoricsApplication.applicationContext)
    }

    @Provides
    @Singleton
    fun provideStoreDataUseCase(
        fileStorage: FileStorage
    ): StoreDataUseCase {
        return StoreDataUseCase(fileStorage)
    }

    @Provides
    @Singleton
    fun provideStoreTextUseCase(
        storeDataUseCase: StoreDataUseCase
    ): StoreTextUseCase {
        return StoreTextUseCase(storeDataUseCase)
    }

    @Provides
    @Singleton
    fun provideReadDataUseCase(
        fileStorage: FileStorage
    ): ReadDataUseCase {
        return ReadDataUseCase(fileStorage)
    }

    @Provides
    @Singleton
    fun provideReadTextUseCase(
        readDataUseCase: ReadDataUseCase
    ): ReadTextUseCase {
        return ReadTextUseCase(readDataUseCase)
    }

    @Provides
    @Singleton
    fun provideStoreTempDataUseCase(
        fileStorage: FileStorage
    ): StoreTempDataUseCase {
        return StoreTempDataUseCase(fileStorage)
    }

    @Provides
    @Singleton
    fun provideStoreTempTextUseCase(
        storeTempDataUseCase: StoreTempDataUseCase
    ): StoreTempTextUseCase {
        return StoreTempTextUseCase(storeTempDataUseCase)
    }
}