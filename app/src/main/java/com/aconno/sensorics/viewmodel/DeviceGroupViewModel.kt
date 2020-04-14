package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceGroupUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDeviceGroupsUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceGroupUseCase
import com.aconno.sensorics.domain.model.DeviceGroup
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class DeviceGroupViewModel(
    private val saveDeviceGroupUseCase : SaveDeviceGroupUseCase,
    private val getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
    private val deleteDeviceGroupUseCase: DeleteDeviceGroupUseCase
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

}