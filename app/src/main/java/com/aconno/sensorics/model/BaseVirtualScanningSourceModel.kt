package com.aconno.sensorics.model

import android.os.Parcelable

abstract class BaseVirtualScanningSourceModel(
        val id: Long,
        val name: String, var enabled: Boolean
) : Parcelable