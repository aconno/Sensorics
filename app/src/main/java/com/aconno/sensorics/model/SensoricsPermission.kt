package com.aconno.sensorics.model

import android.Manifest
import com.aconno.sensorics.R

class SensoricsPermission(val code: Int, val permission: String) {


    /**
     * to add new permission, create its code (power of 2) and add it to rationale map
     */
    companion object {

        private const val ACCESS_COARSE_LOCATION_CODE = 1 shl 0

        val RATIONALE_MAP = mapOf(
            ACCESS_COARSE_LOCATION_CODE to R.string.location_rationale
            )

        val CODE_MAP = mapOf(
            Manifest.permission.ACCESS_COARSE_LOCATION to ACCESS_COARSE_LOCATION_CODE
        )

        val ACCESS_COARSE_LOCATION = SensoricsPermission(
            ACCESS_COARSE_LOCATION_CODE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        fun getCode(vararg permissions: SensoricsPermission): Int =
            permissions.map { it.code }.reduce{ mask, it -> mask or it }
    }
}