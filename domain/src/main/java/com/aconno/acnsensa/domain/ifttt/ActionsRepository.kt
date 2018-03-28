package com.aconno.acnsensa.domain.ifttt

import io.reactivex.Single

/**
 * @author aconno
 */
interface ActionsRepository {
    fun addAction(action: Action)
    fun deleteAction(action: Action)
    fun getAllActions(): Single<List<Action>>
}