package com.aconno.sensorics.dagger.bluetoothscanning

import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.dagger.application.AppComponent
import dagger.Component
import io.reactivex.Flowable

@Component(dependencies = [AppComponent::class], modules = [BluetoothScanningServiceModule::class])
@BluetoothScanningServiceScope
interface BluetoothScanningServiceComponent {

    fun inject(bluetoothScanningService: BluetoothScanningService)
}

