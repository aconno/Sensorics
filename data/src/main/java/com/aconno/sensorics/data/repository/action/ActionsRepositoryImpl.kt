package com.aconno.sensorics.data.repository.action

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.LimitCondition
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Completable
import io.reactivex.Single

class ActionsRepositoryImpl(
    private val actionDao: ActionDao
) : ActionsRepository {

    override fun getAllActions(): Single<List<Action>> {
        return actionDao.getAll().map { actionEntities -> actionEntities.map { toAction(it) } }
    }

    override fun getActionById(actionId: Long): Single<Action> {
        return actionDao.getActionById(actionId).map { actionEntity -> toAction(actionEntity) }
    }

    override fun addAction(action: Action): Completable {
        return Completable.fromAction {
            actionDao.insert(toEntity(action))
        }
    }

    override fun deleteAction(action: Action): Completable {
        return Completable.fromAction {
            actionDao.delete(toEntity(action))
        }
    }

    //TODO: Move this to mapper object
    private fun toEntity(action: Action): ActionEntity {
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
            phoneNumber = action.outcome.parameters[Outcome.PHONE_NUMBER] ?: "",
            outcomeType = action.outcome.type
        )
    }

    private fun toAction(actionEntity: ActionEntity): Action {
        val id = actionEntity.id
        val name = actionEntity.name
        val condition =
            LimitCondition(
                actionEntity.readingType,
                actionEntity.value,
                actionEntity.conditionType
            )

        val parameters = mapOf(
            Pair(Outcome.TEXT_MESSAGE, actionEntity.textMessage),
            Pair(Outcome.PHONE_NUMBER, actionEntity.phoneNumber)
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
            outcome
        )
    }
}