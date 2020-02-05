package com.aconno.sensorics.ui.settings_framework.fragments

import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import com.aconno.sensorics.viewmodel.SettingsActivitySharedViewModel
import dagger.android.support.DaggerFragment
import timber.log.Timber

abstract class SettingsBaseFragment() : DaggerFragment() {

    private val settingsActivitySharedViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SettingsActivitySharedViewModel::class.java)
    }

    protected var jsGenerator = JavascriptCallGenerator()

    /**
     * When we invoke [sendBeaconsUpdatedJson] method it sends data to the settings activity. Then
     * settings activity set this data into liveData for future observers, but we can receive this data
     * again in the current fragment. So we need to avoid event circularity
     */
    private var occurredAfterUpdateSent = false

    inner class SettingsFragmentsWebViewClient : WebViewClient() {
        private var isPageAlreadyLoaded = false

        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.d("page $url loaded")
            super.onPageFinished(view, url)
            if (viewIsNotDestroyed() && !isPageAlreadyLoaded) {
                settingsActivitySharedViewModel.beaconJsonLiveDataForFragments.observe(
                    viewLifecycleOwner,
                    Observer {
                        it?.let { beaconInfo ->
                            if (!occurredAfterUpdateSent) {
                                Timber.d("received beaconInfo : $beaconInfo")
                                receivedBeaconInfo(beaconInfo)
                            }
                            occurredAfterUpdateSent = false
                        }
                    })
                isPageAlreadyLoaded = true
            } else {
                Timber.w("view is destroyed or page $url already loaded. not invoking pageLoaded callback")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVebViewWithWebClient(SettingsFragmentsWebViewClient())
    }

    abstract fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient)

    abstract fun receivedBeaconInfo(beaconInfo: String)

    protected fun sendBeaconsUpdatedJson(updatedBeaconJson: String) {
        Timber.d("Sending updated beacon: $updatedBeaconJson")
        occurredAfterUpdateSent = true
        settingsActivitySharedViewModel.beaconDataChanged(updatedBeaconJson)
    }


    protected inner class UpdateBeaconJsInterfaceImpl {
        @JavascriptInterface
        fun onDataChanged(updatedBeaconJson: String) {
            sendBeaconsUpdatedJson(updatedBeaconJson)
        }
    }

    fun viewIsNotDestroyed() = view != null
}