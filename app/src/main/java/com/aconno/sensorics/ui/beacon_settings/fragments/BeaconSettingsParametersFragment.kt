package com.aconno.sensorics.ui.beacon_settings.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_parameter.*

class BeaconSettingsParametersFragment : SettingsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_parameter, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        with(webview_parameters) {
            settings.builtInZoomControls = false
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.allowContentAccess = true
            addJavascriptInterface(UpdateBeaconJsInterfaceImpl(), "native")
            webViewClient = mandatoryWebViewClient
            loadUrl(HTML_FILE_PATH)
        }
    }

    override fun receivedBeaconInfo(beaconInfo: String) {
        webview_parameters?.loadUrl(
            jsGenerator.generateCall("ParametersLoader.setBeaconParameters", beaconInfo)
        )
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/parameters/ParametersNew.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsParametersFragment()
    }

}
