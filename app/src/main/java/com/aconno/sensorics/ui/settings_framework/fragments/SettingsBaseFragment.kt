package com.aconno.sensorics.ui.settings_framework.fragments

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import dagger.android.support.DaggerFragment
import timber.log.Timber

abstract class SettingsBaseFragment() : DaggerFragment() {

    protected val settingsActivitySharedViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SettingsActivitySharedViewModel::class.java)
    }

    protected var javascriptCallGenerator = JavascriptCallGenerator()

    inner class PageLoadedEventWebViewClient(
        val pageLoaded: () -> Unit
    ) : WebViewClient() {
        private var isPageAlreadyLoaded = false

        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.d("page $url loaded")
            super.onPageFinished(view, url)
            if (viewIsNotDestroyed() && !isPageAlreadyLoaded) {
                pageLoaded()
                isPageAlreadyLoaded = false
            } else {
                Timber.w("view is destroyed or page $url already loaded. not invoking pageLoaded callback")
            }
        }
    }

    protected fun sendBeaconsUpdatedJson(updatedBeaconJson: String) =
        settingsActivitySharedViewModel.beaconDataChanged(updatedBeaconJson)


    protected inner class UpdateBeaconJsInterfaceImpl {
        @JavascriptInterface
        fun onDataChanged(updatedBeaconJson: String) {
            sendBeaconsUpdatedJson(updatedBeaconJson)
        }
    }

    fun viewIsNotDestroyed() = view != null
}