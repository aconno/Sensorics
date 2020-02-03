package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_cacheable.*

class BeaconSettingsCacheableParamsFragment : SettingsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon_cacheable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_cacheable_params.settings.builtInZoomControls = false
        webview_cacheable_params.settings.javaScriptEnabled = true
        webview_cacheable_params.settings.allowFileAccess = true
        webview_cacheable_params.settings.allowFileAccessFromFileURLs = true
        webview_cacheable_params.settings.allowUniversalAccessFromFileURLs = true
        webview_cacheable_params.settings.allowContentAccess = true
        webview_cacheable_params.addJavascriptInterface(UpdateBeaconJsInterfaceImpl(), "native")
        webview_cacheable_params.webViewClient = PageLoadedEventWebViewClient {
            settingsActivitySharedViewModel.beaconJsonLiveDataForFragments.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    // todo JavascriptCallGenerator
                    beaconInfo?.let { webview_cacheable_params?.loadUrl("javascript:GeneralView.Actions.setBeaconInformation('${it}')") }
                })
        }
        webview_cacheable_params.loadUrl(HTML_FILE_PATH)

    }


    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/cacheable/CacheableParameters.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsCacheableParamsFragment()
    }

}
