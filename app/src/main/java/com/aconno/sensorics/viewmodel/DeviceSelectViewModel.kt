package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.repository.GetDevicesConnectedWithPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.model.DeviceRelationModel
import com.aconno.sensorics.model.mapper.DeviceRelationModelMapper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

class DeviceSelectViewModel(
    private val getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val getDevicesConnectedWithPublishUseCase: GetDevicesConnectedWithPublishUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper
) : ViewModel() {

    fun getAllDevices(): Single<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map { deviceRelationModelMapper.toDeviceRelationModel(it, false) }
            .toList()
    }

    fun getAllDevicesWithRelation(publishId: Long, publishType: PublishType): Flowable<List<DeviceRelationModel>> {
        return getSavedDevicesMaybeUseCase.execute().toFlowable()
            .zipWith(getDevicesConnectedWithPublishUseCase.execute(publishId, publishType).toFlowable())
            .map { pair ->
                pair.first.map { device ->
                    deviceRelationModelMapper.toDeviceRelationModel(
                        device,
                        pair.second.any { it.macAddress == device.macAddress }
                    )
                }
            }
    }
}