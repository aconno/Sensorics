package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceActive
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DeviceViewModel(
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDeviceUseCase: SaveDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val savedDevicesLiveData = MutableLiveData<List<DeviceActive>>()

    init {
        loadSavedDevices()
    }

    private fun loadSavedDevices() {
        getSavedDevicesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { devices ->
                savedDevicesLiveData.value = devices.map { DeviceActive(it, false) }
            }
    }

    fun getSavedDevicesLiveData(): MutableLiveData<List<DeviceActive>> {
        return savedDevicesLiveData
    }

    fun saveDevice(device: Device) {
        saveDeviceUseCase.execute(device)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updateDevice(device: Device, alias: String) {
        val newDevice = Device(
            device.name,
            alias,
            device.macAddress,
            device.icon
        )

        saveDeviceUseCase.execute(newDevice)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteDevice(device: Device) {
        deleteDeviceUseCase.execute(device)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}