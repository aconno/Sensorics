package com.aconno.sensorics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import com.aconno.sensorics.device.location.LocationStateListener

class LocationStateReceiver(private val locationStateListener: LocationStateListener) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.takeIf { it.action == Intent.ACTION_PROVIDER_CHANGED }?.let { _ ->
            context?.let {
                locationStateListener.onLoationStateEvent(isLocationOn(context))
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isLocationOn(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isLocationEnabled
        } else {
            val contentResolver = context.contentResolver
            val mode = Settings.Secure.getInt(
                contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF
            )
            mode == Settings.Secure.LOCATION_MODE_OFF
        }
    }
}