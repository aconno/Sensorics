package com.aconno.acnsensa.dagger.bluetoothscanning

import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.dagger.application.AppComponent
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

