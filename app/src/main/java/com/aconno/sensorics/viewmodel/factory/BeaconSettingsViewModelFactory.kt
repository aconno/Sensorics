package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.model.mapper.WebViewAppBeaconMapper
import com.aconno.sensorics.viewmodel.BeaconSettingsViewModel

class BeaconSettingsViewModelFactory(
    private val bluetooth: Bluetooth,
    private val beacon: Beacon,
    private val webViewAppBeaconMapper: WebViewAppBeaconMapper
) : BaseViewModelFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BeaconSettingsViewModel(bluetooth, beacon, webViewAppBeaconMapper)
        return getViewModel(viewModel, modelClass)
    }
}