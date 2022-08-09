package com.aconno.sensorics.data.repository.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            textMessage = Gson().toJson(action.outcome.parameters) ?: "",
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

        val gson = Gson()

        val parameters = try {
            gson.fromJson(actionEntity.textMessage, mapStringStringTypeToken)
        } catch (e: Exception) {
            mapOf(
                Pair(Outcome.TEXT_MESSAGE, actionEntity.textMessage)
            )
        }

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

    companion object {
        private val mapStringStringTypeToken = object : TypeToken<Map<String, String>>() {}.type
    }
}