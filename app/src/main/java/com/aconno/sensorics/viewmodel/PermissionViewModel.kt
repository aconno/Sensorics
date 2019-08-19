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
                SensoricsPermission.getCode(*sensoricsPermissions)
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
            SensoricsPermission.getCode(*sensoricsPermissions),
            *(sensoricsPermissions.map { it.permission }.toTypedArray())
        )
    }

    fun checkGrantedPermissions(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if(results.isEmpty()) {
            permissionCallbacks.permissionDenied(requestCode)
        } else {
            val parts = partitionResults(permissions, results)
            if(parts.first != 0) {
                permissionCallbacks.permissionAccepted(parts.first)
            }
            if(parts.second != 0) {
                permissionCallbacks.permissionDenied(parts.second)
            }
        }
    }

    private fun partitionResults(permissions: Array<String>, results: IntArray): Pair<Int, Int> {
        val pairList = permissions.map{ SensoricsPermission.CODE_MAP.getValue(it) }
            .zip(results.toTypedArray())
            .partition{ it.second == PackageManager.PERMISSION_GRANTED }
        val first = if(pairList.first.isEmpty()) 0 else pairList.first.map { it.first }
            .reduce{mask, it -> mask or it }
        val second = if(pairList.second.isEmpty()) 0 else pairList.second.map { it.first }
            .reduce{mask, it -> mask or it }
        return first to second
    }

    interface PermissionCallbacks {

        fun permissionAccepted(actionCode: Int)

        fun permissionDenied(actionCode: Int)

        fun showRationale(
            needRationale: List<SensoricsPermission>,
            vararg needGrant: SensoricsPermission)
    }
}