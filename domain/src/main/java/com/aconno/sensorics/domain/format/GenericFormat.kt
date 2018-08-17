package com.aconno.sensorics.domain.format

data class GenericFormat(
    private val formatName: String,
    private val icon: String,
    private val format: List<ByteFormat>,
    private val requiredFormat: List<ByteFormatRequired>,
    private val connectible: Boolean,
    private val connectionWrite: List<ConnectionWrite>?,
    private val connectionRead: List<ConnectionRead>?
) : AdvertisementFormat {

    override fun getName(): String {
        return formatName
    }

    override fun getIcon(): String {
        return icon
    }

    override fun getFormat(): Map<String, ByteFormat> {
        val map = hashMapOf<String, ByteFormat>()

        format.forEach {
            map[it.name] = it
        }

        return map
    }

    override fun getRequiredFormat(): List<ByteFormatRequired> {
        return requiredFormat
    }

    override fun isConnectible(): Boolean {
        return connectible
    }

    override fun getConnectionWriteList(): List<ConnectionWrite>? {
        return connectionWrite
    }

    override fun getConnectionReadList(): List<ConnectionRead>? {
        return connectionRead
    }
}