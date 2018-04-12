package com.aconno.acnsensa.device.permissons

import android.app.Activity

object PermissionActionFactory {

    fun getPermissionAction(activity: Activity): PermissionAction {
        return PermissionActionImpl(activity)
    }
}