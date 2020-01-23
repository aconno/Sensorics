package com.aconno.sensorics.ui.settings_framework.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.ui.settings_framework.SettingsFrameworkActivity
import com.aconno.sensorics.viewmodel.BeaconInformationViewModel
import com.aconno.sensorics.viewmodel.SaveChangesEvent
import timber.log.Timber

abstract class BeaconSettingsBaseFragment(private val fragmentId: Int? = null) : Fragment() {
    protected var beaconInformation: String? = null

    lateinit var beaconInfoViewModel: BeaconInformationViewModel

    private var beaconBroadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                return
            }
            beaconInformation =
                intent.getStringExtra(SettingsFrameworkActivity.BEACON_JSON_BROADCAST_EXTRA)
            beaconInfoViewModel.beaconInformationLoaded(beaconInformation!!)
        }
    }

    private var saveActionBroadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                return
            }
            val targetFragmentId =
                intent.getIntExtra(SettingsFrameworkActivity.TARGET_FRAGMENT_ID_BROADCAST_EXTRA, -1)
            if (targetFragmentId == fragmentId) {
                beaconInfoViewModel.saveChangesLiveData.value = SaveChangesEvent
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        beaconInfoViewModel =
            ViewModelProviders.of(this).get(BeaconInformationViewModel::class.java)
    }

    override fun onStop() {
        super.onStop()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }
        saveActionBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(it)
        }

    }

    override fun onStart() {
        super.onStart()
        beaconBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                it,
                IntentFilter(SettingsFrameworkActivity.BEACON_JSON_BROADCAST)
            )
        }
        saveActionBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                it,
                IntentFilter(SettingsFrameworkActivity.SAVE_CHANGES_BROADCAST)
            )
        }
    }

    inner class PageLoadedEventWebViewClient(
        val pageLoaded: () -> Unit
    ) : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.d("page $url loaded")
            super.onPageFinished(view, url)
            pageLoaded()
        }
    }

    protected fun requestBeaconInfo() {
        val intent = Intent(SettingsFrameworkActivity.BEACON_JSON_REQUEST_BROADCAST)
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }
}