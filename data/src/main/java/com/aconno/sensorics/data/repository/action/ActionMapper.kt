package com.aconno.sensorics.data.repository.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionMapper @Inject constructor() {

    fun toEntity(action: Action): ActionEntity {
        return ActionEntity(
                id = action.id,
                name = action.name,
                deviceName = action.device.name,
                deviceAlias = action.device.alias,
                deviceMacAddress = action.device.macAddress,
                deviceIcon = action.device.icon,
                readingType = action.condition.readingType,
                conditionType = action.condition.type,
                value = action.condition.limit,
                textMessage = action.outcome.parameters[Outcome.TEXT_MESSAGE] ?: "",
                outcomeType = action.outcome.type,
                active = if (action.active) 1 else 0,
                timeFrom = action.timeFrom,
                timeTo = action.timeTo
        )
    }

    fun toAction(actionEntity: ActionEntity): Action {
        val id = actionEntity.id
        val name = actionEntity.name
        val condition =
                LimitCondition(
                    actionEntity.readingType,
                    actionEntity.conditionType,
                    actionEntity.value
                )

        val parameters = mapOf(
                Pair(Outcome.TEXT_MESSAGE, actionEntity.textMessage)
        )

        val outcome = Outcome(
                parameters,
                actionEntity.outcomeType
        )

        val device = Device(
                actionEntity.deviceName,
                actionEntity.deviceAlias,
                actionEntity.deviceMacAddress,
                actionEntity.deviceIcon
        )

        return GeneralAction(
                id,
                name,
                device,
                condition,
                outcome,
                actionEntity.active == 1,
                actionEntity.timeFrom,
                actionEntity.timeTo
        ).apply {
            outcome.sourceAction = this
        }
    }

}