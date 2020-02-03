package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_parameter2.*

class BeaconSettingsParametersFragment : SettingsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_parameter2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_parameters.settings.builtInZoomControls = false
        webview_parameters.settings.javaScriptEnabled = true
        webview_parameters.settings.allowFileAccess = true
        webview_parameters.settings.allowFileAccessFromFileURLs = true
        webview_parameters.settings.allowUniversalAccessFromFileURLs = true
        webview_parameters.settings.allowContentAccess = true
        webview_parameters.addJavascriptInterface(UpdateBeaconJsInterfaceImpl(), "native")
        webview_parameters.webViewClient = PageLoadedEventWebViewClient {
            settingsActivitySharedViewModel.beaconJsonLiveDataForFragments.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    // todo use here javascriptCallGenerator.generateCall("init", it)
                    beaconInfo?.let { webview_parameters?.loadUrl("javascript:ParametersLoader.setBeaconParameters('$it')") }
                })
        }
        webview_parameters.loadUrl(HTML_FILE_PATH)
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/parameters/ParametersNew.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsParametersFragment()
    }

}
