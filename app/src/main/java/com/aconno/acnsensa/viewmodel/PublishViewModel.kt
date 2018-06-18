package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.GeneralGooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GeneralMqttPublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.AddGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.AddMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.DeleteMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.GetAllMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.AddRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.model.*
import com.aconno.acnsensa.model.mapper.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PublishViewModel(
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRESTPublishUseCase: AddRESTPublishUseCase,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val devicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase,
    private val devicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase,
    private val savedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase,
    private val deviceRelationModelMapper: DeviceRelationModelMapper,
    private val saveRESTHeaderUseCase: SaveRESTHeaderUseCase,
    private val deleteRESTHeaderUseCase: DeleteRESTHeaderUseCase,
    private val getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,
    private val addMqttPublishUseCase: AddMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val devicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase
) : ViewModel() {

    fun save(
        googlePublishModel: GooglePublishModel
    ): Single<Long> {

        val transform = googlePublishModelDataMapper.transform(googlePublishModel)
        return addGooglePublishUseCase.execute(transform)
    }

    fun save(
        restPublishModel: RESTPublishModel
    ): Single<Long> {

        val transform = restPublishModelDataMapper.transform(restPublishModel)
        return addRESTPublishUseCase.execute(transform)
    }

    fun save(
        mqttPublishModel: MqttPublishModel
    ): Single<Long> {

        val transform = mqttPublishModelDataMapper.toMqttPublish(mqttPublishModel)
        return addMqttPublishUseCase.execute(transform)
    }

    fun addOrUpdateRestRelation(
        deviceId: String,
        restId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        )
    }

    fun addOrUpdateGoogleRelation(
        deviceId: String,
        googleId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
                deviceId
            )
        )
    }

    fun addOrUpdateMqttRelation(
        deviceId: String,
        mqttId: Long
    ): Completable {
        return savePublishDeviceJoinUseCase.execute(
            GeneralMqttPublishDeviceJoin(
                mqttId,
                deviceId
            )
        )
    }

    fun getAllDevices(): Single<MutableList<DeviceRelationModel>> {
        return savedDevicesMaybeUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it)
            }.toList()
    }

    fun getDevicesThatConnectedWithPublish(basePublishModel: BasePublishModel): Single<MutableList<DeviceRelationModel>> {
        return when (basePublishModel) {
            is GooglePublishModel -> getDevicesThatConnectedWithGooglePublish(basePublishModel.id)
            is RESTPublishModel -> getDevicesThatConnectedWithRESTPublish(basePublishModel.id)
            is MqttPublishModel -> getDevicesThatConnectedWithMqttPublish(basePublishModel.id)
            else -> throw IllegalArgumentException()
        }
    }

    private fun getDevicesThatConnectedWithGooglePublish(googleId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithGooglePublishUseCase.execute(googleId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    private fun getDevicesThatConnectedWithRESTPublish(restId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithRESTPublishUseCase.execute(restId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    private fun getDevicesThatConnectedWithMqttPublish(mqttId: Long): Single<MutableList<DeviceRelationModel>> {
        return devicesThatConnectedWithMqttPublishUseCase.execute(mqttId)
            .toFlowable()
            .flatMapIterable { it }
            .map {
                deviceRelationModelMapper.toDeviceRelationModel(it, true)
            }.toList()
    }

    fun deleteRelationGoogle(
        deviceId: String,
        googleId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralGooglePublishDeviceJoin(
                googleId,
                deviceId
            )
        )
    }

    fun deleteRelationRest(
        deviceId: String,
        restId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralRestPublishDeviceJoin(
                restId,
                deviceId
            )
        )
    }

    fun deleteRelationMqtt(
        deviceId: String,
        mqttId: Long
    ): Completable {
        return deletePublishDeviceJoinUseCase.execute(
            GeneralMqttPublishDeviceJoin(
                mqttId,
                deviceId
            )
        )
    }

    fun addRESTHeader(
        list: List<RESTHeaderModel>,
        it: Long
    ): Completable {
        return saveRESTHeaderUseCase.execute(
            restHeaderModelMapper.toRESTHeaderListByRESTPublishId(list, it)
        )
    }

    fun deleteRESTHeader(restHeader: RESTHeader): Disposable {
        return deleteRESTHeaderUseCase.execute(
            restHeader
        ).subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getRESTHeadersById(rId: Long): Maybe<List<RESTHeaderModel>> {
        return getRESTHeadersByIdUseCase.execute(rId)
            .map(restHeaderModelMapper::toRESTHeaderModelList)
    }

    fun checkFieldsAreEmpty(
        vararg strings: String
    ): Boolean {

        strings.forEach {
            if (it.isBlank()) {
                return true
            }
        }

        return false
    }

}