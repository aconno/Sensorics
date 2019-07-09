package com.aconno.sensorics.domain.actions

import io.reactivex.Completable
import io.reactivex.Single

interface ActionsRepository {

    fun getAllActions(): Single<List<Action>>

    fun getActionById(actionId: Long): Single<Action>

    fun getActionsByDeviceMacAddress(macAddress: String): Single<List<Action>>

    fun addAction(action: Action): Completable

    fun deleteAction(action: Action): Completable
}