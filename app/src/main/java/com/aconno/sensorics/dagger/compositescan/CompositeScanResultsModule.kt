package com.aconno.sensorics.dagger.compositescan

import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class CompositeScanResultsModule {

    @Provides
    @CompositeScanResultsScope
    fun provideDevice(
        filteredScanResult: Flowable<ScanResult>,
        generateScanDeviceUseCase: GenerateScanDeviceUseCase
    ): Flowable<ScanDevice> {
        return filteredScanResult.concatMap { generateScanDeviceUseCase.execute(it).toFlowable() }
    }

    @Provides
    @CompositeScanResultsScope
    fun provideFilteredScanResult(
        mqttVirtualScanner: MqttVirtualScanner,
        bluetooth: Bluetooth,
        filterByFormatUseCase: FilterByFormatUseCase
    ): Flowable<ScanResult> {
        return mqttVirtualScanner.getScanResults()
            .mergeWith(bluetooth.getScanResults())
            .filter { filterByFormatUseCase.execute(it) }
    }

    @Provides
    @CompositeScanResultsScope
    fun provideReadings(
        filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
    }
}