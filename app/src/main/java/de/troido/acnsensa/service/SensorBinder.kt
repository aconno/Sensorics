package de.troido.acnsensa.service

import android.os.Binder
import de.troido.acnsensa.data.SensorData
import de.troido.ekstend.android.services.SubscriptionBinding

typealias SensorListener = (SensorData) -> Unit

class SensorBinder : Binder(), SubscriptionBinding<SensorData> {
    private val listeners = mutableListOf<SensorListener>()

    override fun subscribe(listener: SensorListener) {
        listeners += listener
    }

    override fun unsubscribe(listener: SensorListener) {
        listeners -= listener
    }

    override fun onData(data: SensorData) {
        for (listener in listeners) listener(data)
    }
}
