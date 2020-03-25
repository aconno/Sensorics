package com.aconno.sensorics.dagger.compositescan

import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanResultsModule
import com.aconno.sensorics.dagger.mqtt.MqttScanResultsModule
import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.model.ScanResult
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Named

@Module(
    includes = [
        MqttScanResultsModule::class,
        BluetoothScanResultsModule::class
    ]
)
class CompositeScanResultsModule {

    @Provides
    @Named("composite")
    @CompositeScanResultsScope
    fun provideDevice(
        @Named("composite") filteredScanResult: Flowable<ScanResult>,
        generateScanDeviceUseCase: GenerateScanDeviceUseCase
    ): Flowable<ScanDevice> {
        return filteredScanResult.concatMap { generateScanDeviceUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Named("composite")
    @CompositeScanResultsScope
    fun provideFilteredScanResult(
        @Named("mqttFilteredScanResult") mqttFilteredScanResult: Flowable<ScanResult>,
        @Named("bluetoothFilteredScanResult") bluetoothFilteredScanResult: Flowable<ScanResult>
    ): Flowable<ScanResult> {
        return mqttFilteredScanResult.mergeWith(bluetoothFilteredScanResult)
    }

    @Provides
    @Named("composite")
    @CompositeScanResultsScope
    fun provideReadings(
        @Named("composite") filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
    }
}