package com.aconno.sensorics.viewmodel

import android.content.pm.PackageManager
import com.aconno.sensorics.device.permissons.PermissionAction
import com.aconno.sensorics.model.SensoricsPermission

/**
 * TODO Refactor // This class has to take multiple permissions at the same time.//
 */
class PermissionViewModel(
    private val permissionAction: PermissionAction,
    private val permissionCallbacks: PermissionCallbacks
) {

    fun requestAccessFineLocation() {
        checkAndRequestPermission(SensoricsPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessFineLocationAfterRationale() {
        requestPermission(SensoricsPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessToReadExternalStorage() {
        checkAndRequestPermission(SensoricsPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestAccessToReadExternalStorageAfterRationale() {
        requestPermission(SensoricsPermission.READ_EXTERNAL_STORAGE)
    }

    private fun checkAndRequestPermission(sensoricsPermission: SensoricsPermission) {
        if (permissionAction.hasSelfPermission(sensoricsPermission.permission)) {
            permissionCallbacks.permissionAccepted(sensoricsPermission.code)
        } else {
            if (permissionAction.shouldShowRequestPermissionRationale(sensoricsPermission.permission)) {
                //TODO: Rationale not implemented yet
                //permissionCallbacks.showRationale(sensoricsPermission.code)
                requestPermission(sensoricsPermission)
            } else {
                requestPermission(sensoricsPermission)
            }
        }
    }

    private fun requestPermission(sensoricsPermission: SensoricsPermission) {
        permissionAction.requestPermission(sensoricsPermission.permission, sensoricsPermission.code)
    }

    fun checkGrantedPermission(grantResults: IntArray, requestCode: Int) {
        if (verifyGrantedPermission(grantResults)) {
            permissionCallbacks.permissionAccepted(requestCode)
        } else {
            permissionCallbacks.permissionDenied(requestCode)
        }
    }

    private fun verifyGrantedPermission(grantResults: IntArray): Boolean {
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    interface PermissionCallbacks {

        fun permissionAccepted(actionCode: Int)

        fun permissionDenied(actionCode: Int)

        fun showRationale(actionCode: Int)
    }
}