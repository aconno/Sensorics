package com.aconno.sensorics

import androidx.lifecycle.Observer

class LiveDataObserver<T>(private val callback: (T) -> Unit) : Observer<T> {
    override fun onChanged(t: T?) {
        t?.let { callback.invoke(it) }
    }
}