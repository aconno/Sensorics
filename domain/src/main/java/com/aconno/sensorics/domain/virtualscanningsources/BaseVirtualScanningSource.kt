package com.aconno.sensorics.domain.virtualscanningsources

interface BaseVirtualScanningSource {
    val id: Long
    val name: String
    var enabled: Boolean
    val type: VirtualScanningSourceType
}