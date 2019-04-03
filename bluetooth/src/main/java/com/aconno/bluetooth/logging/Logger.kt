package com.aconno.bluetooth.logging

import com.aconno.bluetooth.BluetoothDevice
import com.aconno.bluetooth.DeviceSpec
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class Logger(override val device: BluetoothDevice) : DeviceSpec(device), Observer<String> {
    private var list: MutableList<String> = mutableListOf()

    override fun onComplete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSubscribe(d: Disposable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNext(t: String) {
        list.add(t)
    }

    override fun onError(e: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}