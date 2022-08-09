package com.aconno.sensorics.data.repository.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

class ActionsRepositoryImpl(
    private val actionDao: ActionDao,
    private val actionMapper : ActionMapper
) : ActionsRepository {

    override fun getAllActions(): Single<List<Action>> {
        return actionDao.getAll().map { actionEntities -> actionEntities.map { actionMapper.toAction(it) } }
    }

    override fun getAllActionsAsFlowable(): Flowable<List<Action>> {
        return actionDao.getAllAsFlowable().map { actionEntities -> actionEntities.map { actionMapper.toAction(it) } }
    }

    override fun getActionById(actionId: Long): Single<Action> {
        return actionDao.getActionById(actionId).map { actionEntity -> actionMapper.toAction(actionEntity) }
    }

    override fun getActionsByDeviceMacAddress(macAddress: String): Single<List<Action>> {
        return actionDao.getActionsByDeviceMacAddress(macAddress).map { actionEntities ->
            actionEntities.map {
                actionMapper.toAction(it)
            }
        }
    }

    override fun addAction(action: Action): Single<Long> {
        return Single.fromCallable {
            Timber.i("Inserted Action")
            actionDao.insert(actionMapper.toEntity(action))
        }
    }

    override fun deleteAction(action: Action): Completable {
        return Completable.fromAction {
            actionDao.delete(actionMapper.toEntity(action))
        }
    }


}