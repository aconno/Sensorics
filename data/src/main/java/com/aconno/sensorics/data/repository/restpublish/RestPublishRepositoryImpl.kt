package com.aconno.sensorics.data.repository.restpublish

import com.aconno.sensorics.data.mapper.RestHeaderDataMapper
import com.aconno.sensorics.data.mapper.RestHttpGetParamDataMapper
import com.aconno.sensorics.data.mapper.RestPublishDataMapper
import com.aconno.sensorics.data.mapper.RestPublishEntityDataMapper
import com.aconno.sensorics.domain.ifttt.*
import io.reactivex.Maybe
import io.reactivex.Single

class RestPublishRepositoryImpl(
    private val restPublishDao: RESTPublishDao,
    private val restPublishEntityDataMapper: RestPublishEntityDataMapper,
    private val restPublishDataMapper: RestPublishDataMapper,
    private val restHeaderDataMapper: RestHeaderDataMapper,
    private val restHttpGetParamDataMapper: RestHttpGetParamDataMapper
) :
    RestPublishRepository {
    override fun addRESTPublish(restPublish: RestPublish): Long {
        return restPublishDao.insert(restPublishDataMapper.transform(restPublish))
    }

    override fun addRESTHeader(restHeader: List<RestHeader>) {
        if (restHeader.isNotEmpty()) {
            restPublishDao.insertHeaders(restHeaderDataMapper.toRESTHeaderEntityList(restHeader))
        }
    }

    override fun updateRESTPublish(restPublish: RestPublish) {
        restPublishDao.update(restPublishDataMapper.transform(restPublish))
    }

    override fun deleteRESTPublish(restPublish: RestPublish) {
        restPublishDao.delete(restPublishDataMapper.transform(restPublish))
    }

    override fun deleteRESTHeader(restHeader: RestHeader) {
        restPublishDao.delete(restHeaderDataMapper.toRESTHeaderEntity(restHeader))
    }

    override fun getAllRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.all.map(restPublishEntityDataMapper::transform)
    }

    override fun getAllEnabledRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.getEnabledRESTPublish()
            .map(restPublishEntityDataMapper::transform)
    }

    override fun getRESTPublishById(RESTPublishId: Long): Maybe<RestPublish> {
        return restPublishDao.getRESTPublishById(RESTPublishId)
            .map(restPublishEntityDataMapper::transform)
    }

    override fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeader>> {
        return restPublishDao.getHeadersByRESTPublishId(restPublishId)
            .map(restHeaderDataMapper::toRESTHeaderList)
    }

    override fun addHttpGetParams(restHttpGetParams: List<RestHttpGetParam>) {
        if (restHttpGetParams.isNotEmpty()) {
            restPublishDao.insertHttpGetParams(
                restHttpGetParamDataMapper.toRESTHttpGetParamEntityList(
                    restHttpGetParams
                )
            )
        }
    }

    override fun deleteRESTHttpGetParam(restHttpGetParam: RestHttpGetParam) {
        restPublishDao.delete(restHttpGetParamDataMapper.toRESTHttpGetParamEntity(restHttpGetParam))
    }

    override fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParam>> {
        return restPublishDao.getRESTHttpGetParamsByRESTPublishId(restPublishId)
            .map(restHttpGetParamDataMapper::toRESTHttpGetParamList)
    }
}
