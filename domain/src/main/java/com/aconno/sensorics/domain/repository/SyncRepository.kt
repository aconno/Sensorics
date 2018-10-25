package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.Sync

interface SyncRepository {
    fun save(sync: Sync)
    fun getSync(uniqueId: String): List<Sync>
}
