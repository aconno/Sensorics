package com.aconno.sensorics.dagger.bluetoothscanning

import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.consolidation.GenerateScanDeviceUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByFormatUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.model.ScanResult
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Named

@Module
class BluetoothScanResultsModule {

    @Provides
    @Named("bluetoothDevice")
    @BluetoothScanResultsScope
    fun provideDevice(
        @Named("bluetoothFilteredScanResult") filteredScanResult: Flowable<ScanResult>,
        generateScanDeviceUseCase: GenerateScanDeviceUseCase
    ): Flowable<ScanDevice> {
        return filteredScanResult.concatMap { generateScanDeviceUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Named("bluetoothReadings")
    @BluetoothScanResultsScope
    fun provideReadings(
        @Named("bluetoothFilteredScanResult") filteredScanResult: Flowable<ScanResult>,
        generateReadingsUseCase: GenerateReadingsUseCase
    ): Flowable<List<Reading>> {
        return filteredScanResult.concatMap { generateReadingsUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Named("bluetoothFilteredScanResult")
    @BluetoothScanResultsScope
    fun provideFilteredScanResult(
        bluetooth: Bluetooth,
        filterByFormatUseCase: FilterByFormatUseCase
    ): Flowable<ScanResult> {
        return bluetooth.getScanResults().filter {
            filterByFormatUseCase.execute(it)
        }
    }

}