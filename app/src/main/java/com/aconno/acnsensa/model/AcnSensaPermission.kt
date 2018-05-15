package com.aconno.acnsensa.model

import android.Manifest

class AcnSensaPermission(val code: Int, val permission: String) {

    companion object {

        private const val ACCESS_FINE_LOCATION_CODE = 1

        val ACCESS_FINE_LOCATION =
            AcnSensaPermission(ACCESS_FINE_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}