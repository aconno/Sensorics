package com.aconno.sensorics.device.permissons

interface PermissionAction {

    fun hasSelfPermission(permission: String): Boolean

    fun requestPermission(permission: String, requestCode: Int)

    fun shouldShowRequestPermissionRationale(permission: String): Boolean
}