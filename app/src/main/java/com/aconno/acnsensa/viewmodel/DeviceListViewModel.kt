package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetAllDevicesUseCase
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DeviceListViewModel(
    private val getAllDevicesUseCase: GetAllDevicesUseCase
) : ViewModel() {

    private val preferredDevicesLiveData = MutableLiveData<List<Device>>()

    init {
        loadDevices()
    }

    private fun loadDevices() {
        getAllDevicesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { devices ->
                preferredDevicesLiveData.value = devices
            }
    }

    fun getPreferredDevicesLiveData(): MutableLiveData<List<Device>> {
        return preferredDevicesLiveData
    }
}