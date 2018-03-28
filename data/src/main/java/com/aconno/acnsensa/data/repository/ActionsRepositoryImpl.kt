package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.ifttt.*
import io.reactivex.Single

class ActionsRepositoryImpl(
    private val actionDao: ActionDao,
    private val notificationDisplay: NotificationDisplay
) : ActionsRepository {
    override fun addAction(action: Action) {
        actionDao.insert(toEntity(action))
    }

    override fun deleteAction(action: Action) {
        actionDao.delete(toEntity(action))
    }

    override fun getAllActions(): Single<List<Action>> {
        return actionDao.all.map { actionEntities -> actionEntities.map { toAction(it) } }
    }

    private fun toEntity(action: Action): ActionEntity {
        val name = action.name
        val sensorType = action.condition.sensorType
        val conditionType = action.condition.type
        val value = action.condition.limit
        val outcome = action.outcome as? NotificationOutcome
        val message = outcome?.message ?: ""
        return ActionEntity(
            name = name,
            sensorType = sensorType,
            conditionType = conditionType,
            value = value,
            outcomeMessage = message
        )
    }

    private fun toAction(actionEntity: ActionEntity): Action {
        val name = actionEntity.name
        val condition =
            LimitCondition(actionEntity.sensorType, actionEntity.value, actionEntity.conditionType)

        val outcome = NotificationOutcome(
            actionEntity.outcomeMessage, notificationDisplay
        )

        return GeneralAction(name, condition, outcome)
    }
}