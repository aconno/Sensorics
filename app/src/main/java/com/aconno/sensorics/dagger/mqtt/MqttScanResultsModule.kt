package com.aconno.sensorics.dagger.mqtt

import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class MqttScanResultsModule {


    @Provides
    @MqttScanResultsScope
    fun provideDevice(
        filteredScanResult: Flowable<ScanResult>,
        generateScanDeviceUseCase: GenerateScanDeviceUseCase
    ): Flowable<ScanDevice> {
        return filteredScanResult.concatMap { generateScanDeviceUseCase.execute(it).toFlowable() }
    }

    @Provides
    @MqttScanResultsScope
    fun provideFilteredScanResult(
        mqttVirtualScanner: MqttVirtualScanner,
        filterByFormatUseCase: FilterByFormatUseCase
    ): Flowable<ScanResult> {
        return mqttVirtualScanner.getScanResults().filter { filterByFormatUseCase.execute(it) }
    }

    @Provides
    @MqttScanResultsScope
    fun provideReadings(
        filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
    }
}