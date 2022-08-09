package com.aconno.sensorics.ui.dialogs


import dagger.android.support.DaggerAppCompatDialogFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class DisposerDialogFragment : DaggerAppCompatDialogFragment() {

    private val compositeDisposable = CompositeDisposable()

    override fun onDetach() {
        compositeDisposable.clear()
        super.onDetach()
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}