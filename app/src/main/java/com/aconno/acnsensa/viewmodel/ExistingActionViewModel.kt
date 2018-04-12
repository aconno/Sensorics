package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.domain.ifttt.DeleteActionUseCase
import com.aconno.acnsensa.domain.ifttt.GetActionByIdUseCase
import com.aconno.acnsensa.domain.ifttt.UpdateActionUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ExistingActionViewModel(
    private val updateActionUseCase: UpdateActionUseCase,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val deleteActionUseCase: DeleteActionUseCase
) : ViewModel() {

    val action: MutableLiveData<Action> = MutableLiveData()

    fun getActionById(actionId: Long) {
        getActionByIdUseCase.execute(actionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onActionFound(it) },
                { Timber.e("Failed to get action") })
    }

    private fun onActionFound(action: Action) {
        this.action.value = action
    }

    fun updateAction() {
        action.value?.let {
            updateActionUseCase.execute(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.e("Updated action successfully") },
                    { Timber.e("Failed to update action") })
        }
    }

    fun deleteAction() {
        action.value?.let {
            deleteActionUseCase.execute(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Timber.e("Deleted action successfully") },
                    { Timber.e("Failed to delete action") })
        }
    }
}