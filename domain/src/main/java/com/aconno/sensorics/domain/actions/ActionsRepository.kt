package com.aconno.sensorics.domain.actions

import io.reactivex.Single

interface ActionsRepository {

    fun getAllActions(): Single<List<Action>>

    fun getActionById(actionId: Long): Single<Action>

    fun addAction(action: Action)

    fun deleteAction(action: Action)
}