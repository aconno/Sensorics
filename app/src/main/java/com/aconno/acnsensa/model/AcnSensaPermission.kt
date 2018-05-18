package com.aconno.acnsensa.model

import android.Manifest

class AcnSensaPermission(val code: Int, val permission: String) {

    companion object {

        private const val ACCESS_FINE_LOCATION_CODE = 1
        private const val READ_EXTERNAL_STORAGE_CODE = 2

        val ACCESS_FINE_LOCATION =
                AcnSensaPermission(ACCESS_FINE_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
        val READ_EXTERNAL_STORAGE =
                AcnSensaPermission(READ_EXTERNAL_STORAGE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}