package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.virtualscanningsource.*
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.AddMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.DeleteMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetAllMqttVirtualScanningSourcesUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetMqttVirtualScanningSourceByIdUseCase
import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.RuntimeException

class VirtualScanningSourceListViewModel(
        private val getAllMqttSourcesUseCase: GetAllMqttVirtualScanningSourcesUseCase,
        private val deleteMqttSourcesUseCase: DeleteMqttVirtualScanningSourceUseCase,
        private val updateSourceUseCase: UpdateVirtualScanningSourceUseCase,
        private val addMqttSourceUseCase: AddMqttVirtualScanningSourceUseCase,
        private val getMqttSourceByIdUseCase: GetMqttVirtualScanningSourceByIdUseCase,
        private val mqttVirtualScanningSourceModelDataMapper : MqttVirtualScanningSourceModelDataMapper
) : ViewModel() {

    fun add(virtualScanningSource: BaseVirtualScanningSource): Single<Long> {
        return when (virtualScanningSource) {
            is MqttVirtualScanningSource -> addMqttSourceUseCase.execute(virtualScanningSource)
            else -> throw IllegalArgumentException("Invalid virtual scanning source type.")
        }
    }

    fun getMqttVirtualScanningSourceModelById(id: Long) : Maybe<MqttVirtualScanningSourceModel> {
        return getMqttSourceByIdUseCase.execute(id).flatMap {
            Maybe.just(mqttVirtualScanningSourceModelDataMapper.toMqttVirtualScanningSourceModel(it))
        }
    }

    fun update(sourceModel: BaseVirtualScanningSourceModel): Disposable {
        val mappedSource = when (sourceModel) {
            is MqttVirtualScanningSourceModel -> mqttVirtualScanningSourceModelDataMapper.toMqttVirtualScanningSource(sourceModel)
            else -> throw IllegalArgumentException("Invalid virtual scanning source model.")
        }

        return Completable.fromAction { updateSourceUseCase.execute(mappedSource) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun getAllVirtualScanningSources(): Single<List<BaseVirtualScanningSourceModel>> {
        return getAllMqttSourcesUseCase.execute().map {
            it.map { source ->
                when(source) {
                    is MqttVirtualScanningSource -> mqttVirtualScanningSourceModelDataMapper.toMqttVirtualScanningSourceModel(source)
                    else -> throw RuntimeException("Invalid virtual scanning source type")
                }

            }
        }
    }

    fun deleteMqttVirtulScanningSource(mqttScanningSourceModel: MqttVirtualScanningSourceModel): Disposable {
        val mqttSource = mqttVirtualScanningSourceModelDataMapper.toMqttVirtualScanningSource(mqttScanningSourceModel)

        return deleteMqttSourcesUseCase.execute(mqttSource)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}