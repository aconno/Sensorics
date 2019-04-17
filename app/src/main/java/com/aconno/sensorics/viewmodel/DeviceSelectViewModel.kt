package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetDevicesThatConnectedWithRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.DeviceRelationModel
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

class DeviceSelectViewModel(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val getDevicesThatConnectedWithRestPublishUseCase: GetDevicesThatConnectedWithRestPublishUseCase,
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
            .zipWith(getDevicesThatConnectedWithRestPublishUseCase.execute(id).toFlowable())
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
            .zipWith(Maybe.fromCallable { getDevicesThatConnectedWithMqttPublishUseCase.execute(id) }.toFlowable())
            .map {
                val list = mutableListOf<DeviceRelationModel>()

                loop@ for (i in 0..(it.first.size - 1)) {
                    for (j in 0..(it.second!!.size - 1)) {
                        if (it.first[i].macAddress == it.second!![j].macAddress) {
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