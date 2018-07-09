package com.aconno.sensorics.data.repository.rpublish

import com.aconno.sensorics.data.mapper.RESTHeaderDataMapper
import com.aconno.sensorics.data.mapper.RESTHttpGetParamDataMapper
import com.aconno.sensorics.data.mapper.RESTPublishDataMapper
import com.aconno.sensorics.data.mapper.RESTPublishEntityDataMapper
import com.aconno.sensorics.domain.ifttt.*
import io.reactivex.Maybe
import io.reactivex.Single

class RESTPublishRepositoryImpl(
    private val restPublishDao: RESTPublishDao,
    private val restPublishEntityDataMapper: RESTPublishEntityDataMapper,
    private val restPublishDataMapper: RESTPublishDataMapper,
    private val restHeaderDataMapper: RESTHeaderDataMapper,
    private val restHttpGetParamDataMapper: RESTHttpGetParamDataMapper
) :
    RESTPublishRepository {
    override fun addRESTPublish(restPublish: RESTPublish): Long {
        return restPublishDao.insert(restPublishDataMapper.transform(restPublish))
    }

    override fun addRESTHeader(restHeader: List<RESTHeader>) {
        if (restHeader.isNotEmpty()) {
            restPublishDao.insertHeaders(restHeaderDataMapper.toRESTHeaderEntityList(restHeader))
        }
    }

    override fun updateRESTPublish(restPublish: RESTPublish) {
        restPublishDao.update(restPublishDataMapper.transform(restPublish))
    }

    override fun deleteRESTPublish(restPublish: RESTPublish) {
        restPublishDao.delete(restPublishDataMapper.transform(restPublish))
    }

    override fun deleteRESTHeader(restHeader: RESTHeader) {
        restPublishDao.delete(restHeaderDataMapper.toRESTHeaderEntity(restHeader))
    }

    override fun getAllRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.all.map(restPublishEntityDataMapper::transform)
    }

    override fun getAllEnabledRESTPublish(): Single<List<BasePublish>> {
        return restPublishDao.getEnabledRESTPublish()
            .map(restPublishEntityDataMapper::transform)
    }

    override fun getRESTPublishById(RESTPublishId: Long): Maybe<RESTPublish> {
        return restPublishDao.getRESTPublishById(RESTPublishId)
            .map(restPublishEntityDataMapper::transform)
    }

    override fun getHeadersByRESTPublishId(restPublishId: Long): Maybe<List<RESTHeader>> {
        return restPublishDao.getHeadersByRESTPublishId(restPublishId)
            .map(restHeaderDataMapper::toRESTHeaderList)
    }

    override fun addHttpGetParams(restHttpGetParams: List<RESTHttpGetParam>) {
        if (restHttpGetParams.isNotEmpty()) {
            restPublishDao.insertHttpGetParams(
                restHttpGetParamDataMapper.toRESTHttpGetParamEntityList(
                    restHttpGetParams
                )
            )
        }
    }

    override fun deleteRESTHttpGetParam(restHttpGetParam: RESTHttpGetParam) {
        restPublishDao.delete(restHttpGetParamDataMapper.toRESTHttpGetParamEntity(restHttpGetParam))
    }

    override fun getRESTHttpGetParamsByRESTPublishId(restPublishId: Long): Maybe<List<RESTHttpGetParam>> {
        return restPublishDao.getRESTHttpGetParamsByRESTPublishId(restPublishId)
            .map(restHttpGetParamDataMapper::toRESTHttpGetParamList)
    }
}
