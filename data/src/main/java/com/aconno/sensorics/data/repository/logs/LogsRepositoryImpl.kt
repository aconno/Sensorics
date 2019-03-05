package com.aconno.sensorics.data.repository.logs

import com.aconno.sensorics.data.mapper.LogDataMapper
import com.aconno.sensorics.data.mapper.LogEntityDataMapper
import com.aconno.sensorics.domain.logs.Log
import com.aconno.sensorics.domain.logs.LogsRepository
import io.reactivex.Completable
import io.reactivex.Single

class LogsRepositoryImpl(private val logDao: LogDao,
                         private val logDataMapper: LogDataMapper,
                         private val logEntityDataMapper: LogEntityDataMapper) : LogsRepository {
    override fun getAllDeviceLogs(deviceMacAddress: String): Single<List<Log>> {
        return logDao.getLogsForDevice(deviceMacAddress)
                .map { logEntities -> logEntities.map { logDataMapper.transform(it) } }
    }

    override fun deleteAllDeviceLogs(deviceMacAddress: String): Completable {
        return Completable.fromAction { logDao.deleteAllDeviceLogs(deviceMacAddress) }
    }

    override fun insertLog(log: Log): Completable {
        return Completable.fromAction { logDao.insert(logEntityDataMapper.transform(log)) }
    }

}