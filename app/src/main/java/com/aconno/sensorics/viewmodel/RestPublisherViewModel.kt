package com.aconno.sensorics.viewmodel

import com.aconno.sensorics.domain.ifttt.GeneralRestPublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.interactor.ifttt.publish.AddAnyPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllDeviceParameterPlaceholderStringsUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetRestPublishByIdUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.model.RestHeaderModel
import com.aconno.sensorics.model.RestHttpGetParamModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishDataMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single


class RestPublisherViewModel(
    private val getRestPublishByIdUseCase: GetRestPublishByIdUseCase,
    private val addAnyPublishUseCase: AddAnyPublishUseCase,

    private val restPublishModelDataMapper: RESTPublishModelDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,

    private val getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase,
    private val saveRestHeaderUseCase: SaveRestHeaderUseCase,
    private val restHeaderModelMapper: RESTHeaderModelMapper,

    private val getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase,
    private val saveRestHttpGetParamUseCase: SaveRestHttpGetParamUseCase,
    private val restHttpGetParamModelMapper: RESTHttpGetParamModelMapper,

    savePublishDeviceJoinUseCase: SavePublishDeviceJoinUseCase,
    deletePublishDeviceJoinUseCase: DeletePublishDeviceJoinUseCase,

    getAllDeviceParameterPlaceholderStringsUseCase: GetAllDeviceParameterPlaceholderStringsUseCase
) : PublisherViewModel<RestPublishModel>(
    savePublishDeviceJoinUseCase, deletePublishDeviceJoinUseCase,getAllDeviceParameterPlaceholderStringsUseCase
) {

    override fun getById(id: Long): Maybe<RestPublishModel> {
        return getRestPublishByIdUseCase.execute(id).map {
            restPublishDataMapper.transform(it)
        }
    }

    override fun save(
        model: RestPublishModel
    ): Single<Long> {
        val transform = restPublishModelDataMapper.transform(model)
        return addAnyPublishUseCase.execute(transform)
    }

    override fun createPublishDeviceJoin(deviceId: String, publishId: Long): PublishDeviceJoin {
        return GeneralRestPublishDeviceJoin(
            publishId,
            deviceId
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
}