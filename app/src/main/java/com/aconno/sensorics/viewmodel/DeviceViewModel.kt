package com.aconno.sensorics.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceActive
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DeviceViewModel(
    deviceStream: Flowable<Device>,
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDeviceUseCase: SaveDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val savedDevicesLiveData = MutableLiveData<List<DeviceActive>>()

    private val timestamps = hashMapOf<Device, Long>()

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            deviceStream.subscribe { scannedDevice ->
                timestamps[scannedDevice] = System.currentTimeMillis()
                val savedDevices = savedDevicesLiveData.value
                savedDevices?.forEach {
                    if (scannedDevice == it.device && !it.active) {
                        it.active = true
                        savedDevicesLiveData.postValue(savedDevicesLiveData.value)
                        return@forEach
                    }
                }
            }
        )
        disposables.add(
            Observable.interval(10, TimeUnit.SECONDS)
                .subscribe {
                    var refresh = false
                    savedDevicesLiveData.value?.forEach {
                        val lastSeenTimestamp = timestamps[it.device] ?: 0L
                        val timestampDiff = System.currentTimeMillis() - lastSeenTimestamp
                        if (timestampDiff < 10000) {
                            refresh = !it.active
                            it.active = true
                        } else {
                            refresh = it.active
                            it.active = false
                        }
                    }
                    if (refresh) {
                        savedDevicesLiveData.postValue(savedDevicesLiveData.value)
                    }
                }
        )
    }

    fun getSavedDevicesFlowable(): Flowable<List<DeviceActive>> {
        return getSavedDevicesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map { DeviceActive(it, false) }
            }
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

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        Timber.d("Disposed")
    }
}