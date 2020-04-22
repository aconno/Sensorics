package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.model.DeviceGroupDeviceJoin
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DeviceGroupViewModel(
    private val saveDeviceGroupUseCase : SaveDeviceGroupUseCase,
    private val getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
    private val deleteDeviceGroupUseCase: DeleteDeviceGroupUseCase,
    private val updateDeviceGroupUseCase: UpdateDeviceGroupUseCase,
    private val saveDeviceGroupDeviceJoinUseCase: SaveDeviceGroupDeviceJoinUseCase,
    private val deleteDeviceGroupDeviceJoinUseCase: DeleteDeviceGroupDeviceJoinUseCase,
    private val getDevicesInDeviceGroupUseCase: GetDevicesInDeviceGroupUseCase,
    private val getDevicesBelongingSomeDeviceGroupUseCase : GetDevicesBelongingSomeDeviceGroupUseCase
) : ViewModel() {


    fun saveDeviceGroup(deviceGroup: DeviceGroup) : Single<DeviceGroup> {
        return saveDeviceGroupUseCase.execute(deviceGroup)
            .subscribeOn(Schedulers.io())
            .map { id -> DeviceGroup(id,deviceGroup.groupName) }
    }

    fun saveDeviceGroup(groupName : String) : Single<DeviceGroup> {
        return saveDeviceGroup(DeviceGroup(0,groupName))
    }

    fun getDeviceGroups() : Single<List<DeviceGroup>> {
        return getSavedDeviceGroupsUseCase
            .execute()
            .subscribeOn(Schedulers.io())
    }

    fun deleteDeviceGroup(deviceGroup: DeviceGroup) {
        deleteDeviceGroupUseCase.execute(deviceGroup)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun updateDeviceGroup(deviceGroup: DeviceGroup) : Completable {
        return updateDeviceGroupUseCase.execute(deviceGroup)
            .subscribeOn(Schedulers.io())
    }

    fun addDeviceGroupDeviceRelation(
        deviceId: String,
        deviceGroupId: Long
    ): Completable {
        return saveDeviceGroupDeviceJoinUseCase.execute(
            createDeviceGroupDeviceJoin(deviceId, deviceGroupId)
        )
    }

    private fun createDeviceGroupDeviceJoin(deviceId: String, deviceGroupId: Long): DeviceGroupDeviceJoin {
        return DeviceGroupDeviceJoin(
            deviceGroupId,deviceId
        )
    }

    fun deleteDeviceGroupDeviceRelation(
        deviceId: String,
        deviceGroupId: Long
    ): Completable {
        return deleteDeviceGroupDeviceJoinUseCase.execute(
            createDeviceGroupDeviceJoin(deviceId,deviceGroupId)
        )
    }

    fun getDevicesFromDeviceGroup(
        deviceGroupId: Long
    ) : Maybe<List<Device>> {
        return getDevicesInDeviceGroupUseCase.execute(deviceGroupId)
            .subscribeOn(Schedulers.io())
    }

    fun moveDevicesToDeviceGroup(devices : List<Device>, deviceGroup: DeviceGroup) : Completable {
        val actions = mutableListOf<Completable>()

        actions.apply {
            devices.forEach {
                add(addDeviceGroupDeviceRelation(it.macAddress,deviceGroup.id))
            }
        }

        return Completable.merge(actions).subscribeOn(Schedulers.io())
    }

    fun getDevicesBelongingSomeDeviceGroup() : Maybe<List<Device>> {
        return getDevicesBelongingSomeDeviceGroupUseCase.execute()
            .subscribeOn(Schedulers.io())
    }

    fun removeDevicesFromDeviceGroup(devices: List<Device>, deviceGroup: DeviceGroup): Completable {
        val actions = mutableListOf<Completable>()

        actions.apply {
            devices.forEach {
                add(deleteDeviceGroupDeviceRelation(it.macAddress,deviceGroup.id))
            }
        }

        return Completable.merge(actions).subscribeOn(Schedulers.io())
    }
}