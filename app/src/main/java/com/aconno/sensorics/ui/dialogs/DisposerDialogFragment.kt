package com.aconno.sensorics.ui.dialogs

import android.support.v4.app.DialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class DisposerDialogFragment : DialogFragment() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDetach() {
        compositeDisposable.clear()
        super.onDetach()
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}