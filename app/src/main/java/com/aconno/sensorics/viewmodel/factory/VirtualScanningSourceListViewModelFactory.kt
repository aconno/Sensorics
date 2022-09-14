package com.aconno.sensorics.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.virtualscanningsource.*
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.DeleteMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetAllMqttVirtualScanningSourcesUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetMqttVirtualScanningSourceByIdUseCase
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.viewmodel.VirtualScanningSourceListViewModel

class VirtualScanningSourceListViewModelFactory(
        private val getAllMqttSourcesUseCase: GetAllMqttVirtualScanningSourcesUseCase,
        private val deleteMqttSourcesUseCase: DeleteMqttVirtualScanningSourceUseCase,
        private val updateSourceUseCase: UpdateVirtualScanningSourceUseCase,
        private val addMqttSourceUseCase: AddMqttVirtualScanningSourceUseCase,
        private val getMqttSourceByIdUseCase: GetMqttVirtualScanningSourceByIdUseCase,
        private val mqttVirtualScanningSourceModelDataMapper : MqttVirtualScanningSourceModelDataMapper
) : BaseViewModelFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = VirtualScanningSourceListViewModel(
                getAllMqttSourcesUseCase,
                deleteMqttSourcesUseCase,
                updateSourceUseCase,
                addMqttSourceUseCase,
                getMqttSourceByIdUseCase,
                mqttVirtualScanningSourceModelDataMapper
        )
        return getViewModel(viewModel,modelClass)
    }
}