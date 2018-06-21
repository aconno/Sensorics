package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.mapper.DeviceRelationModelMapper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

class DeviceSelectViewModel(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : ViewModel() {

    fun getAllDevices(): Single<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map { deviceRelationModelMapper.toDeviceRelationModel(it, false) }
            .toList()
            .map { it as List<DeviceRelationModel> }
    }

    fun getAllDevicesWithGoogleRelation(id: Long): Flowable<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute().toFlowable()
            .zipWith(getDevicesThatConnectedWithGooglePublishUseCase.execute(id).toFlowable())
            .map {
                val list = mutableListOf<DeviceRelationModel>()

                loop@ for (i in 0..(it.first.size - 1)) {
                    for (j in 0..(it.second.size - 1)) {
                        if (it.first[i].macAddress == it.second[j].macAddress) {
                            list.add(
                                deviceRelationModelMapper.toDeviceRelationModel(it.first[i], true)
                            )
                            continue@loop
                        }
                    }
                    list.add(
                        deviceRelationModelMapper.toDeviceRelationModel(it.first[i], false)
                    )
                }
                list
            }
    }

    fun getAllDevicesWithRESTRelation(id: Long): Flowable<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute().toFlowable()
            .zipWith(getDevicesThatConnectedWithRESTPublishUseCase.execute(id).toFlowable())
            .map {
                val list = mutableListOf<DeviceRelationModel>()

                loop@ for (i in 0..(it.first.size - 1)) {
                    for (j in 0..(it.second.size - 1)) {
                        if (it.first[i].macAddress == it.second[j].macAddress) {
                            list.add(
                                deviceRelationModelMapper.toDeviceRelationModel(it.first[i], true)
                            )
                            continue@loop
                        }
                    }
                    list.add(
                        deviceRelationModelMapper.toDeviceRelationModel(it.first[i], false)
                    )
                }
                list
            }
    }

    fun getAllDevicesWithMqttRelation(id: Long): Flowable<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute().toFlowable()
            .zipWith(getDevicesThatConnectedWithMqttPublishUseCase.execute(id).toFlowable())
            .map {
                val list = mutableListOf<DeviceRelationModel>()

                loop@ for (i in 0..(it.first.size - 1)) {
                    for (j in 0..(it.second.size - 1)) {
                        if (it.first[i].macAddress == it.second[j].macAddress) {
                            list.add(
                                deviceRelationModelMapper.toDeviceRelationModel(it.first[i], true)
                            )
                            continue@loop
                        }
                    }
                    list.add(
                        deviceRelationModelMapper.toDeviceRelationModel(it.first[i], false)
                    )
                }
                list
            }
    }
}