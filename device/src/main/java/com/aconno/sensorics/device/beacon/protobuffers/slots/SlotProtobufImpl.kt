package com.aconno.sensorics.device.beacon.protobuffers.slots

import com.aconno.sensorics.device.beacon.Slot

class SlotProtobufImpl : Slot() {
    override var shownInUI: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var name: String
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override val readOnly: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override var active: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var packetCount: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var txPower: Byte
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var advertisingMode: AdvertisingModeParameters.Mode
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
    override var advertisingModeParameters: AdvertisingModeParameters
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun toBytes(): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}