package com.aconno.sensorics.model

import android.Manifest
import com.aconno.sensorics.R

object SensoricsPermission {

    val RATIONALE_MAP = mapOf(
        Manifest.permission.READ_EXTERNAL_STORAGE to R.string.read_external_storage_permission_rationale,
        Manifest.permission.ACCESS_FINE_LOCATION to R.string.access_fine_location_permission_rationale
    )

    const val SCANNING_PERMISSIONS_REQUEST_CODE = 10
    val SCANNING_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
}
