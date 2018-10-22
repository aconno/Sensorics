package com.aconno.sensorics.domain.repository

interface LocalUseCaseRepository {
    fun saveOrReplaceUseCase(name: String, lastModifiedDate: Long, html: String)
    fun getLastUpdateTimestamp(sensorName: String): Long?
    fun getAllUseCaseNames(): List<String>
    fun deleteUseCase(sensorName: String)
    fun getFilePathFor(sensorName: String): String
}