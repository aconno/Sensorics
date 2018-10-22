package com.aconno.sensorics.ui.base

import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : DaggerAppCompatActivity() {
    private var compositeDisposable: CompositeDisposable? = null

    override fun onStop() {
        clearCompositeDisposable()
        super.onStop()
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