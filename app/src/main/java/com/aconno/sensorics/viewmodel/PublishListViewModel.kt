package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PublishListViewModel(
    private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
    private val getAllRestPublishUseCase: GetAllRestPublishUseCase,
    private val googlePublishDataMapper: GooglePublishDataMapper,
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
    private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
    private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
    private val updatePublishUseCase: UpdatePublishUseCase,
    private val addGooglePublishUseCase: AddGooglePublishUseCase,
    private val addRestPublishUsecase: AddRestPublishUseCase,
    private val addMqttPublishUseCase: AddMqttPublishUseCase
) : ViewModel() {

    fun add(publish: BasePublish): Disposable {
        return when (publish) {
            is GooglePublish -> addGooglePublishUseCase.execute(publish)
            is RestPublish -> addRestPublishUsecase.execute(publish)
            is MqttPublish -> addMqttPublishUseCase.execute(publish)
            else -> throw IllegalArgumentException("Invalid publish type.")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun update(publishModel: BasePublishModel): Disposable {
        val mappedPublish = when (publishModel) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(publishModel)
            is RestPublishModel -> restPublishModelDataMapper.transform(publishModel)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(publishModel)
            else -> throw IllegalArgumentException("Invalid publish model.")
        }

        return Completable.fromAction { updatePublishUseCase.execute(mappedPublish) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun getAllPublish(): Flowable<List<BasePublishModel>> {
        return Single.merge(
            getAllGooglePublishUseCase.execute(),
            getAllRestPublishUseCase.execute(),
            getAllMqttPublishUseCase.execute()
        ).flatMapIterable { it }
            .map {
                when (it) {
                    is GooglePublish -> {
                        val transform = googlePublishDataMapper.transform(it)
                        transform
                    }
                    is RestPublish -> {
                        val transform = restPublishDataMapper.transform(it)
                        transform
                    }
                    is MqttPublish -> {
                        val transform = mqttPublishModelDataMapper.toMqttPublishModel(it)
                        transform
                    }
                    else -> throw NullPointerException("Illegal parameter provided. ")
                }
            }.toList()
            .toFlowable()
    }

    fun delete(googlePublishModel: GooglePublishModel): Disposable {
        val googlePublish = googlePublishModelDataMapper.transform(googlePublishModel)

        return deleteGooglePublishUseCase.execute(googlePublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(restPublishModel: RestPublishModel): Disposable {
        val restPublish = restPublishModelDataMapper.transform(restPublishModel)

        return deleteRestPublishUseCase.execute(restPublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun delete(mqttPublishModel: MqttPublishModel): Disposable {
        val mqttPublish = mqttPublishModelDataMapper.toMqttPublish(mqttPublishModel)

        return deleteMqttPublishUseCase.execute(mqttPublish)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

}