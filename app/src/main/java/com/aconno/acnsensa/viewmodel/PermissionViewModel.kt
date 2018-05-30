package com.aconno.acnsensa.viewmodel

import android.content.pm.PackageManager
import com.aconno.acnsensa.device.permissons.PermissionAction
import com.aconno.acnsensa.model.AcnSensaPermission

/**
 * TODO Refactor // This class has to take multiple permissions at the same time.//
 */
class PermissionViewModel(
    private val permissionAction: PermissionAction,
    private val permissionCallbacks: PermissionCallbacks
) {

    fun requestAccessFineLocation() {
        checkAndRequestPermission(AcnSensaPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessFineLocationAfterRationale() {
        requestPermission(AcnSensaPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessToReadExternalStorage() {
        checkAndRequestPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestAccessToReadExternalStorageAfterRationale() {
        requestPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)
    }

    private fun checkAndRequestPermission(acnSensaPermission: AcnSensaPermission) {
        if (permissionAction.hasSelfPermission(acnSensaPermission.permission)) {
            permissionCallbacks.permissionAccepted(acnSensaPermission.code)
        } else {
            if (permissionAction.shouldShowRequestPermissionRationale(acnSensaPermission.permission)) {
                //TODO: Rationale not implemented yet
                //permissionCallbacks.showRationale(acnSensaPermission.code)
                requestPermission(acnSensaPermission)
            } else {
                requestPermission(acnSensaPermission)
            }
        }
    }

    private fun requestPermission(acnSensaPermission: AcnSensaPermission) {
        permissionAction.requestPermission(acnSensaPermission.permission, acnSensaPermission.code)
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