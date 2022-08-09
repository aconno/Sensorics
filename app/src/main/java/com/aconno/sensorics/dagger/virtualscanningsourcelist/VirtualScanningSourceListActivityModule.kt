package com.aconno.sensorics.dagger.virtualscanningsourcelist

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.domain.interactor.virtualscanningsource.*
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.DeleteMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetAllMqttVirtualScanningSourcesUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetMqttVirtualScanningSourceByIdUseCase
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.ui.settings.virtualscanningsources.VirtualScanningSourceListActivity
import com.aconno.sensorics.viewmodel.VirtualScanningSourceListViewModel
import com.aconno.sensorics.viewmodel.factory.VirtualScanningSourceListViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class VirtualScanningSourceListActivityModule {

    @Provides
    @VirtualScanningSourceListActivityScope
    fun provideSourceListViewModel(
            activity: VirtualScanningSourceListActivity,
            sourceViewModelFactory: VirtualScanningSourceListViewModelFactory
    ) = ViewModelProvider(activity, sourceViewModelFactory)
            .get(VirtualScanningSourceListViewModel::class.java)

    @Provides
    @VirtualScanningSourceListActivityScope
    fun provideSourceListViewModelFactory(
            getAllMqttSourcesUseCase: GetAllMqttVirtualScanningSourcesUseCase,
            deleteMqttSourcesUseCase: DeleteMqttVirtualScanningSourceUseCase,
            updateSourceUseCase: UpdateVirtualScanningSourceUseCase,
            addMqttSourceUseCase: AddMqttVirtualScanningSourceUseCase,
            getMqttSourceByIdUseCase: GetMqttVirtualScanningSourceByIdUseCase,
            mqttVirtualScanningSourceModelDataMapper : MqttVirtualScanningSourceModelDataMapper
    ) =
            VirtualScanningSourceListViewModelFactory(
                    getAllMqttSourcesUseCase,
                    deleteMqttSourcesUseCase,
                    updateSourceUseCase,
                    addMqttSourceUseCase,
                    getMqttSourceByIdUseCase,
                    mqttVirtualScanningSourceModelDataMapper
            )
}