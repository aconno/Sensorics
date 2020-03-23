package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slots

class SlotsProtobufImpl(override val size: Int) : Slots(size) {
    override var config: Config
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun fromBytes(data: ByteArray) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toBytes(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}