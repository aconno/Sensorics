package com.aconno.sensorics.viewmodel

import android.content.pm.PackageManager
import com.aconno.sensorics.device.permissons.PermissionAction

class PermissionViewModel(
    private val permissionAction: PermissionAction,
    private val permissionCallbacks: PermissionCallbacks
) {

    fun requestPermissions(requestCode: Int,vararg permissions : String ) {
        val permissionsToBeGranted = permissions.filter {
            !permissionAction.hasSelfPermission(it)
        }

        if(permissionsToBeGranted.isEmpty()) {
            permissionCallbacks.onPermissionGranted(requestCode)
            return
        }

        val permissionsToShowRationaleFor = permissionsToBeGranted.filter {
            permissionAction.shouldShowRequestPermissionRationale(it)
        }

        if(permissionsToShowRationaleFor.isNotEmpty()) {
            permissionCallbacks.showRationaleForPermissions(permissionsToShowRationaleFor) {
                requestPermissions(permissionsToBeGranted,requestCode)
            }
        } else {
            requestPermissions(permissionsToBeGranted,requestCode)
        }
    }

    private fun requestPermissions(permissions : List<String>,requestCode: Int) {
        permissionAction.requestPermissions(permissions, requestCode)
    }

    fun checkPermissionsRequestResult(permissions: Array<String>,grantResults: IntArray, requestCode: Int) {
        val deniedPermissions = mutableListOf<String>()
        for((index,permissionGranted) in grantResults.withIndex()) {
            if(permissionGranted != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[index])
            }
        }
        if (deniedPermissions.isEmpty()) {
            permissionCallbacks.onPermissionGranted(requestCode)
        } else {
            permissionCallbacks.onPermissionDenied(requestCode,deniedPermissions)
        }
    }


    interface PermissionCallbacks {

        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int, deniedPermissions : List<String>)

        fun showRationaleForPermissions(permissions : List<String>, onRationaleClosed : () -> Unit)
    }
}