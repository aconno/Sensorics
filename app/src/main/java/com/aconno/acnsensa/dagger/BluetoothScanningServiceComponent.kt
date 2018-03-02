package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.BluetoothScanningService
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [BluetoothScanningServiceModule::class])
@BluetoothScanningServiceScope
interface BluetoothScanningServiceComponent {
    fun inject(bluetoothScanningService: BluetoothScanningService)
}

