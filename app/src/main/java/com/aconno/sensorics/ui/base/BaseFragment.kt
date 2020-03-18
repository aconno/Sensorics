package com.aconno.sensorics.ui.base

import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : DaggerFragment() {

    private var compositeDisposable: CompositeDisposable? = null

    override fun onDestroyView() {
        clearCompositeDisposable()
        super.onDestroyView()
    }

    protected open fun addDisposable(vararg disposable: Disposable) {
        getDisposable().addAll(*disposable)
    }

    private fun clearCompositeDisposable() {
        getDisposable().clear()
    }

    private fun getDisposable(): CompositeDisposable {
        if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        return compositeDisposable as CompositeDisposable
    }
}