package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.AddAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.DeleteAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAllAzureMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.AddGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.DeleteGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.AddMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.DeleteMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.DeleteRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllRestPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.model.*
import com.aconno.sensorics.model.mapper.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PublishListViewModel(
        private val getAllGooglePublishUseCase: GetAllGooglePublishUseCase,
        private val getAllRestPublishUseCase: GetAllRestPublishUseCase,
        private val getAllAzureMqttPublishUseCase: GetAllAzureMqttPublishUseCase,
        private val getAllMqttPublishUseCase: GetAllMqttPublishUseCase,

        private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
        private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
        private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
        private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

        private val addGooglePublishUseCase: AddGooglePublishUseCase,
        private val addRestPublishUsecase: AddRestPublishUseCase,
        private val addMqttPublishUseCase: AddMqttPublishUseCase,
        private val addAzureMqttPublishUseCase: AddAzureMqttPublishUseCase,

        private val deleteGooglePublishUseCase: DeleteGooglePublishUseCase,
        private val deleteRestPublishUseCase: DeleteRestPublishUseCase,
        private val deleteMqttPublishUseCase: DeleteMqttPublishUseCase,
        private val deleteAzureMqttPublishUseCase: DeleteAzureMqttPublishUseCase,

        private val updatePublishUseCase: UpdatePublishUseCase,

        private val googlePublishDataMapper: GooglePublishDataMapper,
        private val restPublishDataMapper: RESTPublishDataMapper,

        private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
        private val restPublishModelDataMapper: RESTPublishModelDataMapper,
        private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
        private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper


) : ViewModel() {

    fun add(publish: BasePublish): Single<Long> {
        return when (publish) {
            is GooglePublish -> addGooglePublishUseCase.execute(publish)
            is RestPublish -> addRestPublishUsecase.execute(publish)
            is MqttPublish -> addMqttPublishUseCase.execute(publish)
            is AzureMqttPublish -> addAzureMqttPublishUseCase.execute(publish)
            else -> throw IllegalArgumentException("Invalid publish type.")
        }
    }

    fun getGooglePublishModelById(id: Long) : Maybe<GooglePublishModel> {
        return getGooglePublishByIdUseCase.execute(id).flatMap {
            Maybe.just(googlePublishDataMapper.transform(it))
        }
    }

    fun getRestPublishModelById(id: Long) : Maybe<RestPublishModel> {
        return getRestPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(restPublishDataMapper.transform(it))
        }
    }

    fun getMqttPublishModelById(id: Long) : Maybe<MqttPublishModel> {
        return getMqttPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(mqttPublishModelDataMapper.toMqttPublishModel(it))
        }
    }

    fun getAzureMqttPublishModelById(id: Long) : Maybe<AzureMqttPublishModel> {
        return getAzureMqttPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(azureMqttPublishModelDataMapper.toAzureMqttPublishModel(it))
        }
    }

    fun update(publishModel: BasePublishModel): Disposable {
        val mappedPublish = when (publishModel) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(publishModel)
            is RestPublishModel -> restPublishModelDataMapper.transform(publishModel)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(publishModel)
            is AzureMqttPublishModel -> azureMqttPublishModelDataMapper.toAzureMqttPublish(publishModel)
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
            getAllMqttPublishUseCase.execute(),
                getAllAzureMqttPublishUseCase.execute()
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
                    is AzureMqttPublish -> {
                        val transform = azureMqttPublishModelDataMapper.toAzureMqttPublishModel(it)
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

    fun delete(azureMqttPublishModel: AzureMqttPublishModel): Disposable {
        val azureMqttPublish = azureMqttPublishModelDataMapper.toAzureMqttPublish(azureMqttPublishModel)

        return deleteAzureMqttPublishUseCase.execute(azureMqttPublish)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}