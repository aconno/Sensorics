package com.aconno.sensorics.data.repository.sync

import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val dao: SyncDao
) : SyncRepository {

    override fun save(sync: Sync) {
        dao.insert(
            SyncEntity(
                sync.publisherUniqueId,
                sync.macAddress,
                sync.advertisementId,
                sync.lastSyncTimestamp
            )
        )
    }

    override fun getSync(uniqueId: String): List<Sync> {
        val list = dao.getByUniqueId(uniqueId)

        return list.map {
            Sync(it.publisherUniqueId, it.macAddress, it.advertisementId, it.lastSyncTimestamp)
        }
    }
}