package com.aconno.sensorics.domain.ifttt.outcome

import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.ActionsRepository
import io.reactivex.disposables.Disposable

class InputToActionsResolverImpl(
    actionsRepository: ActionsRepository
) : InputToActionsResolver {
    private var actionsProviderDisposable : Disposable? = null

    private var actionsMap : MutableMap<ResolverKey,MutableList<Action>> = mutableMapOf()

    init {
        actionsProviderDisposable = actionsRepository.getAllActionsAsFlowable().subscribe {
            updateMapWithActions(it)
        }
    }

    private fun updateMapWithActions(actions : List<Action>) {
        actionsMap.clear()
        actions.forEach { action ->
            val resolverKey = ResolverKey(action.device.macAddress,action.condition.readingType)
            val actionsForKey = actionsMap.getOrPut(resolverKey) { mutableListOf() }
            actionsForKey.add(action)
            actionsMap[resolverKey] = actionsForKey
        }
    }

    protected fun finalize() {
        actionsProviderDisposable?.dispose()
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