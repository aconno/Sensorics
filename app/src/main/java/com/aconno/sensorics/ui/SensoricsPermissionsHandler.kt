package com.aconno.sensorics.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.model.SensoricsPermission
import com.aconno.sensorics.viewmodel.PermissionViewModel
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalStateException

class SensoricsPermissionsHandler(private val context : Context) : PermissionViewModel.PermissionCallbacks {
    var contentContainer : View? = null
    var permissionViewModel : PermissionViewModel? = null

    private var onScanningPermissionsGranted : PermissionGrantedListener? = null

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        permissionViewModel?.checkPermissionsRequestResult(permissions,grantResults, requestCode)
    }

    fun handleScanningPermissions(onPermissionsGranted : PermissionGrantedListener) {
        onScanningPermissionsGranted = onPermissionsGranted
        permissionViewModel?.requestPermissions(
            SensoricsPermission.SCANNING_PERMISSIONS_REQUEST_CODE,
            *SensoricsPermission.SCANNING_PERMISSIONS
        )
    }

    override fun onPermissionGranted(requestCode: Int) {
        if(requestCode == SensoricsPermission.SCANNING_PERMISSIONS_REQUEST_CODE) {
            onScanningPermissionsGranted?.onPermissionGranted()
        }
    }

    override fun onPermissionDenied(requestCode: Int,deniedPermissions : List<String>) {
        var snackbarMessage = R.string.snackbar_permission_message
        if(requestCode == SensoricsPermission.SCANNING_PERMISSIONS_REQUEST_CODE) {
            if(deniedPermissions.size < SensoricsPermission.SCANNING_PERMISSIONS.size) {
                snackbarMessage = R.string.permissions_partially_granted
            }
        }

        contentContainer?.let {
            Snackbar.make(it, snackbarMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_settings) {
                    context.startActivity(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        }
                    )
                }
                .setActionTextColor(ContextCompat.getColor(context, R.color.primaryColor))
                .show()
        }

    }

    override fun showRationaleForPermissions(
        permissions: List<String>,
        onRationaleClosed: () -> Unit
    ) {
        val message = buildRationaleString(permissions)

        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialogInterface, id ->
                dialogInterface.dismiss()
                onRationaleClosed()
            }.create().show()

    }

    private fun buildRationaleString(permissions: List<String>): String {
        val sb = StringBuilder(context.getString(R.string.the_app_needs_permission))
        sb.append(":\n\n")
        permissions.forEach {
            sb.append(context.getString(SensoricsPermission.RATIONALE_MAP[it] ?: throw IllegalStateException("Rationale for this permission is missing in rationale map")))
            sb.append(".\n\n")
        }
        return sb.toString()
    }

    interface PermissionGrantedListener {
        fun onPermissionGranted()
    }


    companion object {


    }
}