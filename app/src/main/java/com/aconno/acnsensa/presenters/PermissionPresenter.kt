package com.aconno.acnsensa.presenters

import android.content.pm.PackageManager
import com.aconno.acnsensa.device.permissons.PermissionAction
import com.aconno.acnsensa.model.Action

class PermissionPresenter(
    private val permissionAction: PermissionAction,
    private val permissionCallbacks: PermissionCallbacks
) {

    fun requestAccessFineLocation() {
        checkAndRequestPermission(Action.ACCESS_FINE_LOCATION)
    }

    private fun checkAndRequestPermission(action: Action) {
        if (permissionAction.hasSelfPermission(action.permission)) {
            permissionCallbacks.permissionAccepted(action.code)
        } else {
            permissionAction.requestPermission(action.permission, action.code)
        }
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