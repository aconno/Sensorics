package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.AddRestPublishUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.RestHeaderModel
import com.aconno.sensorics.model.RestHttpGetParamModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

class RestPublisherViewModel(
    private val addRestPublishUseCase: AddRestPublishUseCase,
    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    private val deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,
    private val saveRestHeaderUseCase: SaveRestHeaderUseCase,
    private val getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,
    private val saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
    private val getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper
) : ViewModel() {

    fun save(
        restPublishModel: RestPublishModel
    ): Single<Long> {

        val transform = restPublishModelDataMapper.transform(restPublishModel)
        return addRestPublishUseCase.execute(transform)
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

    fun addRESTHeader(
        list: List<RestHeaderModel>,
        it: Long
    ): Completable {
        return saveRestHeaderUseCase.execute(
            restHeaderModelMapper.toRESTHeaderListByRESTPublishId(list, it)
        )
    }

    fun addRESTHttpGetParams(
        list: List<RestHttpGetParamModel>,
        it: Long
    ): Completable {
        return saveRestHttpGetParamUseCase.execute(
            restHttpGetParamModelMapper.toRESTHttpGetParamListByRESTPublishId(list, it)
        )
    }

    fun getRESTHeadersById(rId: Long): Maybe<List<RestHeaderModel>> {
        return getRestHeadersByIdUseCase.execute(rId)
            .map(restHeaderModelMapper::toRESTHeaderModelList)
    }

    fun getRESTHttpGetParamsById(rId: Long): Maybe<List<RestHttpGetParamModel>> {
        return getRestHttpGetParamsByIdUseCase.execute(rId)
            .map(restHttpGetParamModelMapper::toRESTHttpGetParamModelList)
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