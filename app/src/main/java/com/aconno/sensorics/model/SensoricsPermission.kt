package com.aconno.sensorics.model

import android.Manifest
import com.aconno.sensorics.R

class SensoricsPermission(val code: Int, val permission: String) {

    companion object {

        const val MULTIPLE_PERMISSIONS_CODE = 1
        private const val ACCESS_FINE_LOCATION_CODE = 2
        private const val READ_EXTERNAL_STORAGE_CODE = 3

        val RATIONALE_MAP = mapOf(
            ACCESS_FINE_LOCATION_CODE to R.string.location_rationale,
            READ_EXTERNAL_STORAGE_CODE to R.string.read_storage_rationale
            )

        val ACCESS_FINE_LOCATION = SensoricsPermission(
            ACCESS_FINE_LOCATION_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val READ_EXTERNAL_STORAGE = SensoricsPermission(
            READ_EXTERNAL_STORAGE_CODE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}