package com.aconno.sensorics.model

import android.Manifest

class SensoricsPermission(val code: Int, val permission: String) {

    companion object {

        private const val ACCESS_FINE_LOCATION_CODE = 1
        private const val READ_EXTERNAL_STORAGE_CODE = 2

        val ACCESS_FINE_LOCATION =
            SensoricsPermission(ACCESS_FINE_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
        val READ_EXTERNAL_STORAGE =
            SensoricsPermission(
                READ_EXTERNAL_STORAGE_CODE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }
}