package com.aconno.sensorics.ui.beacon_settings.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
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

    @SuppressLint("SetJavaScriptEnabled")

    override fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        with(webview_cacheable_params) {
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
        webview_cacheable_params?.apply {
            loadUrl("javascript:GeneralView.Actions.setBeaconInformation('${beaconInfo}')")
        }
    }


    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/cacheable/CacheableParameters.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsCacheableParamsFragment()
    }

}
