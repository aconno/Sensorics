package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.settings_framework.BeaconGeneralFragmentListener
import kotlinx.android.synthetic.main.fragment_beacon_general.*

class BeaconSettingsGeneralFragment : SettingsBaseFragment() {

    private lateinit var listener: BeaconGeneralFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon_general, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        with(webview_general) {
            settings.builtInZoomControls = false
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.allowContentAccess = true
            addJavascriptInterface(this, "native")
            webViewClient = mandatoryWebViewClient
            loadUrl(HTML_FILE_PATH)
        }
    }

    override fun receivedBeaconInfo(beaconInfo: String) {
        webview_general?.loadUrl(
            jsGenerator.generateCall("GeneralView.Actions.setBeaconInformation", beaconInfo)
        )
    }

    @JavascriptInterface
    fun updateFirmware() {
        listener.updateFirmware()
    }

    @JavascriptInterface
    fun factoryReset() {
        listener.resetFactory()
    }

    @JavascriptInterface
    fun addPassword() {
        listener.addPassword()
    }

    @JavascriptInterface
    fun powerOff() {
        listener.powerOff()
    }

    @JavascriptInterface
    fun onDataChanged(updatedBeaconJson: String) {
        sendBeaconsUpdatedJson(updatedBeaconJson)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BeaconGeneralFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBeaconGeneralFragmentInteractionListener")
        }
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/general/General.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsGeneralFragment()
    }

}
