package com.aconno.sensorics.data.repository.restpublish

import com.aconno.sensorics.data.mapper.RestHeaderDataMapper
import com.aconno.sensorics.data.mapper.RestHttpGetParamDataMapper
import com.aconno.sensorics.data.mapper.RestPublishDataMapper
import com.aconno.sensorics.data.repository.PublishRepositoryImpl
import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import io.reactivex.Maybe

class RestPublishRepositoryImpl(
    private val restPublishDao: RESTPublishDao,
    restPublishDataMapper: RestPublishDataMapper,
    private val restHeaderDataMapper: RestHeaderDataMapper,
    private val restHttpGetParamDataMapper: RestHttpGetParamDataMapper
) : PublishRepositoryImpl<RestPublish, RestPublishEntity>(
    restPublishDao,
    restPublishDataMapper
), RestPublishRepository {
    override fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RestHeader>> {
        return restPublishDao.getHeadersByRESTPublishId(restPublishId)
            .map(restHeaderDataMapper::toRESTHeaderList)
    }

    override fun addRESTHeader(restHeader: List<RestHeader>) {
        if (restHeader.isNotEmpty()) {
            restPublishDao.insertHeaders(restHeaderDataMapper.toRESTHeaderEntityList(restHeader))
        }
    }

    override fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RestHttpGetParam>> {
        return restPublishDao.getRESTHttpGetParamsByRESTPublishId(restPublishId)
            .map(restHttpGetParamDataMapper::toRESTHttpGetParamList)
    }

    override fun deleteRESTHeader(restHeader: RestHeader) {
        restPublishDao.delete(restHeaderDataMapper.toRESTHeaderEntity(restHeader))
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
}
