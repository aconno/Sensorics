package com.aconno.sensorics.dagger.readings

import android.app.Application
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.mainactivity.MainActivityScope
import com.aconno.sensorics.device.storage.FileStorageImpl
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.repository.GetReadingsUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveSensorReadingsUseCase
import com.aconno.sensorics.domain.repository.InMemoryRepository
import dagger.Module
import dagger.Provides

@Module
class SensorReadingsModule {

    @Provides
    @SensorReadingsScope
    fun provideRecordSensorValuesUseCase(
            inMemoryRepository: InMemoryRepository
     ): SaveSensorReadingsUseCase {
         return SaveSensorReadingsUseCase(inMemoryRepository)
     }

    @Provides
    @SensorReadingsScope
    fun provideGetSensorReadingsUseCase(
            inMemoryRepository: InMemoryRepository
    ) = GetReadingsUseCase(inMemoryRepository)

    @Provides
    @SensorReadingsScope
    fun provideLogReadingsUseCase(
            context : SensoricsApplication
    ): LogReadingUseCase {
        return LogReadingUseCase(FileStorageImpl(context))
    }
}