package com.aconno.sensorics.data.repository.azuremqttpublish

import com.aconno.sensorics.data.mapper.AzureMqttPublishDataMapper
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.AzureMqttPublishRepository
import com.aconno.sensorics.domain.ifttt.BasePublish
import io.reactivex.Maybe
import io.reactivex.Single

class AzureMqttPublishRepositoryImpl(private val azureMqttPublishDao: AzureMqttPublishDao,
                                     private val azureMqttPublishDataMapper: AzureMqttPublishDataMapper) : AzureMqttPublishRepository {
    override fun addAzureMqttPublish(azureMqttPublish: AzureMqttPublish): Long {
        return azureMqttPublishDao.insert(azureMqttPublishDataMapper.toAzureMqttPublishEntity(azureMqttPublish))
    }

    override fun updateAzureMqttPublish(azureMqttPublish: AzureMqttPublish) {
        return azureMqttPublishDao.update(azureMqttPublishDataMapper.toAzureMqttPublishEntity(azureMqttPublish))
    }

    override fun deleteAzureMqttPublish(azureMqttPublish: AzureMqttPublish) {
        azureMqttPublishDao.delete(azureMqttPublishDataMapper.toAzureMqttPublishEntity(azureMqttPublish))
    }

    override fun getAllAzureMqttPublish(): Single<List<BasePublish>> {
        return azureMqttPublishDao.all.map(azureMqttPublishDataMapper::toAzureMqttPublishList)
    }

    override fun getAllEnabledAzureMqttPublish(): List<BasePublish> {
        return azureMqttPublishDao.getEnabledAzureMqttPublish()
                .map(azureMqttPublishDataMapper::toAzureMqttPublish)
    }

    override fun getAzureMqttPublishById(azureMqttPublishId: Long): Maybe<AzureMqttPublish> {
        return azureMqttPublishDao.getAzureMqttPublishById(azureMqttPublishId)
                .map(azureMqttPublishDataMapper::toAzureMqttPublish)
    }

}