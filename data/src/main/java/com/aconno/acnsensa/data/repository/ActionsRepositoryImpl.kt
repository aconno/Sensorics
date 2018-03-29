package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.*
import io.reactivex.Single

class ActionsRepositoryImpl(
    private val actionDao: ActionDao,
    private val notificationDisplay: NotificationDisplay,
    private val vibrator: Vibrator,
    private val smsSender: SmsSender
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

        var type = 0
        var message = ""
        when (action.outcome) {
            is NotificationOutcome -> {
                type = 1
                message = (action.outcome as? NotificationOutcome)?.message ?: "null"
            }
            is VibrationOutcome -> type = 2
            is SmsOutcome -> {
                type = 3
                message = (action.outcome as? SmsOutcome)?.message ?: ""
            }
        }

        val number = (action.outcome as? SmsOutcome)?.phoneNumber ?: ""
        return ActionEntity(
            name = name,
            sensorType = sensorType,
            conditionType = conditionType,
            value = value,
            outcomeMessage = message,
            outcomeType = type,
            destination = number
        )
    }

    private fun toAction(actionEntity: ActionEntity): Action {
        val name = actionEntity.name
        val condition =
            LimitCondition(actionEntity.sensorType, actionEntity.value, actionEntity.conditionType)

        val type = actionEntity.outcomeType

        val outcome = when (type) {
            1 -> NotificationOutcome(
                actionEntity.outcomeMessage, notificationDisplay
            )
            2 -> VibrationOutcome(vibrator)
            3 -> SmsOutcome(smsSender, actionEntity.destination, actionEntity.outcomeMessage)
            else -> throw Exception("Persistence exception")
        }

        return GeneralAction(name, condition, outcome)
    }
}