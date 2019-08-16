package com.aconno.sensorics.viewmodel

import android.content.pm.PackageManager
import com.aconno.sensorics.device.permissons.PermissionAction
import com.aconno.sensorics.model.SensoricsPermission
import timber.log.Timber

/**
 * @param permissionAction is used for communicating with android's permission API
 * @param permissionCallbacks is used for handling permission result callbacks
 *
 */
class PermissionViewModel(
    private val permissionAction: PermissionAction,
    private val permissionCallbacks: PermissionCallbacks
) {

    fun handlePermissionsRequest(vararg sensoricsPermissions: SensoricsPermission) {
        if(sensoricsPermissions.isEmpty()) {
            Timber.e("checkAndRequestPermission method called with 0 arguments")
            return
        }
        val notGrantedPermissions = sensoricsPermissions.filter {
            !permissionAction.hasSelfPermission(it.permission)
        }.toTypedArray()

        if(notGrantedPermissions.isEmpty()) {
            permissionCallbacks.permissionAccepted(
                if (sensoricsPermissions.size > 1)
                    SensoricsPermission.MULTIPLE_PERMISSIONS_CODE
                else
                    sensoricsPermissions.first().code
            )
            return
        }

        val permissionsNeedingRationale = notGrantedPermissions.filter {
            permissionAction.shouldShowRequestPermissionRationale(it.permission)
        }
        if(permissionsNeedingRationale.isNotEmpty()) {
            permissionCallbacks.showRationale(permissionsNeedingRationale, *notGrantedPermissions)
        } else {
            requestPermissions(*(notGrantedPermissions))
        }
    }

    fun requestPermissions(vararg sensoricsPermissions: SensoricsPermission) {
        permissionAction.requestPermissions(
            if (sensoricsPermissions.size > 1)
                SensoricsPermission.MULTIPLE_PERMISSIONS_CODE
            else
                sensoricsPermissions.first().code,
            *(sensoricsPermissions.map { it.permission }.toTypedArray())
        )
    }

    fun checkGrantedPermissions(grantResults: IntArray, requestCode: Int) {
        if (verifyGrantedPermission(grantResults)) {
            permissionCallbacks.permissionAccepted(requestCode)
        } else {
            permissionCallbacks.permissionDenied(requestCode)
        }
    }

    private fun verifyGrantedPermission(grantResults: IntArray): Boolean {
        return !grantResults.contains(PackageManager.PERMISSION_DENIED)
    }

    interface PermissionCallbacks {

        fun permissionAccepted(actionCode: Int)

        fun permissionDenied(actionCode: Int)

        fun showRationale(
            needRationale: List<SensoricsPermission>,
            vararg needGrant: SensoricsPermission)
    }
}