package com.aconno.sensorics.domain.model

import java.io.Serializable

data class Device(val name: String, val macAddress: String, val icon: String = "") : Serializable