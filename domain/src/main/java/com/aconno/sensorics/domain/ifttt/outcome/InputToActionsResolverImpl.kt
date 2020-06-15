package com.aconno.sensorics.domain.ifttt.outcome

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import io.reactivex.disposables.Disposable

class InputToActionsResolverImpl(
    actionsRepository: ActionsRepository
) : InputToActionsResolver {
    private var disposables : MutableList<Disposable> = mutableListOf()

    private var actionsMap : MutableMap<ResolverKey,MutableList<Action>> = mutableMapOf()

    init {
        actionsRepository.getAllActionsAsFlowable().subscribe {
            actionsMap = actionsToMap(it)
        }.also { disposables.add(it) }
    }

    private fun actionsToMap(actions : List<Action>) : MutableMap<ResolverKey,MutableList<Action>> {
        return mutableMapOf<ResolverKey,MutableList<Action>>().apply {
            actions.forEach { action ->
                val resolverKey = ResolverKey(action.device.macAddress,action.condition.readingType)
                val actionsForKey = this[resolverKey] ?: mutableListOf()
                actionsForKey.add(action)
                this[resolverKey] = actionsForKey
            }
        }
    }

    protected fun finalize() {
        disposables.forEach { it.dispose() }
    }

    override fun getActionsForInputParameters(
        inputDeviceMacAddress: String,
        inputType: String
    ): List<Action> {
        return actionsMap[ResolverKey(inputDeviceMacAddress,inputType)] ?: listOf()
    }

    data class ResolverKey(
        private val macAddress : String,
        private val inputType : String
    )
}