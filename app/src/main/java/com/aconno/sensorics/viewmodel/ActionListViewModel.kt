package com.aconno.sensorics.viewmodel

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ActionListViewModel(
    private val getAllActionsUseCase: GetAllActionsUseCase,
    private val deleteActionUseCase: DeleteActionUseCase,
    private val addActionUseCase: AddActionUseCase,
    val convertActionsToJsonUseCase: ConvertObjectsToJsonUseCase<Action>,
    val convertJsonToActionsUseCase: ConvertJsonToActionsUseCase
) : ViewModel() {

    fun add(action: Action): Single<Long> {
        return addActionUseCase.execute(action)
    }

    fun save(action: Action): Single<Long> {
        return addActionUseCase.execute(action)
    }

    fun getAllActions(): Single<List<Action>> {
        return getAllActionsUseCase.execute()
    }

    fun delete(action: Action): Disposable {
        return deleteActionUseCase.execute(action)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }
}