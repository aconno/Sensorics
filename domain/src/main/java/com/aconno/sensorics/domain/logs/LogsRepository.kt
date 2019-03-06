package com.aconno.sensorics.domain.logs

import io.reactivex.Completable
import io.reactivex.Single

interface LogsRepository {
    fun getAllDeviceLogs(deviceMacAddress: String): Single<List<Log>>

    fun deleteAllDeviceLogs(deviceMacAddress: String): Completable

    fun insertLog(log: Log): Completable
}