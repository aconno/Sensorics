package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.ifttt.UpdateAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish.GetAzureMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetGooglePublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetMqttPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.DeleteAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllPublishersUseCase
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
    private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
    private val getGooglePublishByIdUseCase: GetGooglePublishByIdUseCase,
    private val getMqttPublishByIdUseCase: GetMqttPublishByIdUseCase,
    private val getAzureMqttPublishByIdUseCase: GetAzureMqttPublishByIdUseCase,

    private val getAllPublishersUseCase: GetAllPublishersUseCase,
    private val deleteAnyPublishUseCase: DeleteAnyPublishUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,
    private val updateAnyPublishUseCase: UpdateAnyPublishUseCase,

    private val googlePublishDataMapper: GooglePublishDataMapper, // TODO: Standardize
    private val googlePublishModelDataMapper: GooglePublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val mqttPublishModelDataMapper: MqttPublishModelDataMapper,
    private val azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper
) : ViewModel() {

    fun add(publish: BasePublish): Single<Long> {
        return addAnyPublishUseCase.execute(publish)
    }

    fun getGooglePublishModelById(id: Long): Maybe<GooglePublishModel> {
        return getGooglePublishByIdUseCase.execute(id).flatMap {
            Maybe.just(googlePublishDataMapper.transform(it))
        }
    }

    fun getRestPublishModelById(id: Long): Maybe<RestPublishModel> {
        return getRestPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(restPublishDataMapper.transform(it))
        }
    }

    fun getMqttPublishModelById(id: Long): Maybe<MqttPublishModel> {
        return getMqttPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(mqttPublishModelDataMapper.toMqttPublishModel(it))
        }
    }

    fun getAzureMqttPublishModelById(id: Long): Maybe<AzureMqttPublishModel> {
        return getAzureMqttPublishByIdUseCase.execute(id).flatMap {
            Maybe.just(azureMqttPublishModelDataMapper.toAzureMqttPublishModel(it))
        }
    }

    fun update(publishModel: BasePublishModel): Disposable {
        val mappedPublish = when (publishModel) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(publishModel)
            is RestPublishModel -> restPublishModelDataMapper.transform(publishModel)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(publishModel)
            is AzureMqttPublishModel -> azureMqttPublishModelDataMapper.toAzureMqttPublish(
                publishModel
            )
            else -> throw IllegalArgumentException("Invalid publish model.")
        }

        return Completable.fromAction { updateAnyPublishUseCase.execute(mappedPublish) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    //TODO make this a single
    fun getAllPublish(): Flowable<List<BasePublishModel>> {
        return getAllPublishersUseCase.execute()
            .toFlowable()
            .flatMapIterable { it }
            .map { dataToModel(it) }
            .toList()
            .toFlowable()
    }

    private fun modelToData(publishModel: BasePublishModel): BasePublish {
        return when (publishModel) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(publishModel)
            is RestPublishModel -> restPublishModelDataMapper.transform(publishModel)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(publishModel)
            is AzureMqttPublishModel -> azureMqttPublishModelDataMapper.toAzureMqttPublish(
                publishModel
            )
            else -> throw IllegalArgumentException("Invalid publish model.")
        }
    }

    private fun dataToModel(publish: BasePublish): BasePublishModel {
        return when (publish) {
            is GooglePublish -> googlePublishDataMapper.transform(publish)
            is RestPublish -> restPublishDataMapper.transform(publish)
            is MqttPublish -> mqttPublishModelDataMapper.toMqttPublishModel(publish)
            is AzureMqttPublish -> azureMqttPublishModelDataMapper.toAzureMqttPublishModel(
                publish
            )
            else -> throw IllegalArgumentException("Invalid publish model.")
        }
    }

    fun delete(publishModel: BasePublishModel): Disposable {
        return deleteAnyPublishUseCase.execute(modelToData(publishModel))
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}