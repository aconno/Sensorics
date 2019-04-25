package com.aconno.sensorics

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.aconno.sensorics.device.location.LocationStateListener

class LocationStateReceiver(private val locationStateListener: LocationStateListener) :
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.takeIf { it.action == Intent.ACTION_PROVIDER_CHANGED }?.let { _ ->
            context?.let {
                locationStateListener.onLocationStateChanged(isLocationOn(context))
            }
        }
    }

    private fun isLocationOn(context: Context): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
}