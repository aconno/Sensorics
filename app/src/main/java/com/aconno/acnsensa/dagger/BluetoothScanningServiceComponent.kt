package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.BluetoothScanningService
import dagger.Component
import io.reactivex.Flowable

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [BluetoothScanningServiceModule::class])
@BluetoothScanningServiceScope
interface BluetoothScanningServiceComponent {
    fun sensorValues(): Flowable<Map<String, Number>>
    fun inject(bluetoothScanningService: BluetoothScanningService)
}

