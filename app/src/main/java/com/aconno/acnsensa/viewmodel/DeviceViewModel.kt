package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DeviceViewModel(
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDeviceUseCase: SaveDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val savedDevicesLiveData = MutableLiveData<List<Device>>()

    init {
        loadSavedDevices()
    }

    private fun loadSavedDevices() {
        getSavedDevicesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { devices ->
                savedDevicesLiveData.value = devices
            }
    }

    fun getSavedDevicesLiveData(): MutableLiveData<List<Device>> {
        return savedDevicesLiveData
    }

    fun saveDevice(device: Device) {
        saveDeviceUseCase.execute(device)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun deleteDevice(device: Device) {
        deleteDeviceUseCase.execute(device)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}