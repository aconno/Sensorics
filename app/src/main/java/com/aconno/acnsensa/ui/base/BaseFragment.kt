package com.aconno.acnsensa.ui.base

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment : Fragment() {

    private var compositeDisposable: CompositeDisposable? = null

    override fun onDetach() {
        clearCompositeDisposable()
        super.onDetach()
    }

    protected fun addDisposable(disposable: Disposable) {
        getDisposable().add(disposable)
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