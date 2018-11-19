package com.aconno.sensorics.domain.interactor.sync

import com.aconno.sensorics.domain.ResourceSyncer
import com.aconno.sensorics.domain.interactor.type.BooleanUseCase

class SyncUseCase(
    private val resourceSyncer: ResourceSyncer
) : BooleanUseCase {

    /**
     * return Success
     */
    override fun execute(): Boolean {
        return resourceSyncer.sync()
    }

}