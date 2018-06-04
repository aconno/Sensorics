package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetAllDevicesUseCase
import com.aconno.acnsensa.domain.model.Device

class DeviceListViewModel(
    private val getAllDevicesUseCase: GetAllDevicesUseCase
) : ViewModel() {

    private val preferredDevicesLiveData = MutableLiveData<List<Device>>()

    init {
        loadDevices()
    }

    fun loadDevices() {
        getAllDevicesUseCase.execute().subscribe { devices ->
            preferredDevicesLiveData.value = devices
        }
    }
}