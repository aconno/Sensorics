package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.DeviceActive
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DeviceViewModel(
    deviceStream: Flowable<Device>,
    private val getSavedDevicesUseCase: GetSavedDevicesUseCase,
    private val saveDeviceUseCase: SaveDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val getIconUseCase: GetIconUseCase
) : ViewModel() {

    val deviceActiveObservable: PublishSubject<List<DeviceActive>> =
        PublishSubject.create<List<DeviceActive>>()
    private var deviceList: List<DeviceActive>? = null

    private val timestamps = hashMapOf<Device, Long>()

    private val disposables = CompositeDisposable()

    init {
        disposables.add(
            deviceStream.subscribe { scannedDevice ->
                timestamps[scannedDevice] = System.currentTimeMillis()
                val savedDevices = deviceList
                savedDevices?.forEach {
                    if (scannedDevice == it.device && !it.active) {
                        it.active = true
                        deviceActiveObservable.onNext(savedDevices)
                        return@forEach
                    }
                }
            }
        )

        disposables.add(
            Observable.interval(10, TimeUnit.SECONDS)
                .subscribe {
                    var refresh = false
                    deviceList?.let {
                        it.forEach {
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
                            deviceActiveObservable.onNext(it)
                        }
                    }
                }
        )
    }

    fun getSavedDevicesFlowable(): Flowable<List<DeviceActive>> {
        return getSavedDevicesUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                deviceList = it.map { DeviceActive(it, false) }
                deviceList
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

    fun getIconPath(deviceName: String): String? {
        return getIconUseCase.execute(deviceName)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        Timber.d("Disposed")
    }
}