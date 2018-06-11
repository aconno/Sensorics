package com.aconno.acnsensa.domain.format

data class GenericFormat(
    private val formatName: String,
    private val icon: String,
    private val format: List<ByteFormat>,
    private val requiredFormat: List<ByteFormatRequired>
) : AdvertisementFormat {

    override fun getFormat(): Map<String, ByteFormat> {
        val map = hashMapOf<String, ByteFormat>()

        format.forEach {
            map[it.name] = ByteFormat(
                it.name,
                it.startIndexInclusive,
                it.endIndexExclusive,
                it.isReversed,
                it.dataType
            )
        }

        return map
    }

    override fun getRequiredFormat(): List<ByteFormatRequired> {
        return requiredFormat
    }
}