package com.aconno.sensorics.dagger.publish

import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceScope
import com.aconno.sensorics.dagger.gcloudpublisher.GoogleCloudPublisherScope
import com.aconno.sensorics.dagger.mqttpublisher.MqttPublisherScope
import com.aconno.sensorics.dagger.restpublisher.RESTPublisherScope
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllEnabledGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllEnabledRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.DeletePublishDeviceJoinUseCase
import com.aconno.sensorics.domain.interactor.repository.GetRestHeadersByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.GetRestHttpGetParamsByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.SavePublishDeviceJoinUseCase
import dagger.Module
import dagger.Provides

@Module
class PublishModule {

    @Provides
    @PublishScope
    fun provideGetAllEnabledGooglePublishUseCase(
            googlePublishRepository: GooglePublishRepository
    ): GetAllEnabledGooglePublishUseCase {
        return GetAllEnabledGooglePublishUseCase(
                googlePublishRepository
        )
    }

    @Provides
    @PublishScope
    fun provideGetAllEnabledRESTPublishUseCase(
            restPublishRepository: RestPublishRepository
    ): GetAllEnabledRestPublishUseCase {
        return GetAllEnabledRestPublishUseCase(
                restPublishRepository
        )
    }

    @Provides
    @PublishScope
    fun provideGetAllEnabledMqttPublishUseCase(
            mqttPublishRepository: MqttPublishRepository
    ): GetAllEnabledMqttPublishUseCase {
        return GetAllEnabledMqttPublishUseCase(
                mqttPublishRepository
        )
    }

    @Provides
    @PublishScope
    fun provideGetRESTHeadersByIdUseCase(
            restPublishRepository: RestPublishRepository
    ): GetRestHeadersByIdUseCase {
        return GetRestHeadersByIdUseCase(restPublishRepository)
    }

    @Provides
    @PublishScope
    fun provideGetRESTHttpGetParamsByIdUseCase(
            restPublishRepository: RestPublishRepository
    ): GetRestHttpGetParamsByIdUseCase {
        return GetRestHttpGetParamsByIdUseCase(restPublishRepository)
    }


    @Provides
    @PublishScope
    fun provideAddGooglePublishUseCase(
            googlePublishRepository: GooglePublishRepository
    ): AddGooglePublishUseCase {
        return AddGooglePublishUseCase(
                googlePublishRepository
        )
    }


    @Provides
    @PublishScope
    fun provideAddMqttPublishUseCase(
            mqttPublishRepository: MqttPublishRepository
    ): AddMqttPublishUseCase {
        return AddMqttPublishUseCase(mqttPublishRepository)
    }

    @Provides
    @PublishScope
    fun provideAddRESTPublishUseCase(
            restPublishRepository: RestPublishRepository
    ): AddRestPublishUseCase {
        return AddRestPublishUseCase(restPublishRepository)
    }


}