package com.aconno.sensorics.device.permissons

interface PermissionAction {

    fun hasSelfPermission(permission: String): Boolean

    fun requestPermissions(requestCode: Int, vararg permissions: String)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean
}