package com.aconno.sensorics.domain.ifttt

import io.reactivex.Single

/**
 * @author aconno
 */
interface ActionsRepository {
    fun addAction(action: Action)
    fun updateAction(action: Action)
    fun deleteAction(action: Action)
    fun getAllActions(): Single<List<Action>>
    fun getActionById(actionId: Long): Single<Action>
}