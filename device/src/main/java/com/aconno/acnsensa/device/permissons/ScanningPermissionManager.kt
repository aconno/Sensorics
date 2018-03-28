package com.aconno.acnsensa.device.permissons

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object ScanningPermissionManager {

    private const val REQUEST_CODE = 0x0001

    private val PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun hasPermissions(context: Context): Boolean {
        PERMISSIONS.forEach { permission ->
            val result = ContextCompat.checkSelfPermission(context, permission)
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST_CODE)
    }

    fun getRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode == REQUEST_CODE) {
            grantResults.forEach { result ->
                if (result == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
        }
        return true
    }
}