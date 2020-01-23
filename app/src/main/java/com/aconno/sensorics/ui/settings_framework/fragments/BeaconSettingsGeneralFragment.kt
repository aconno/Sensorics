package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.configure.BeaconGeneralFragmentListener
import kotlinx.android.synthetic.main.fragment_beacon_general2.*

class BeaconSettingsGeneralFragment: BeaconSettingsBaseFragment() {

    private lateinit var listener: BeaconGeneralFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon_general2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_general.settings.builtInZoomControls = false
        webview_general.settings.javaScriptEnabled = true
        webview_general.settings.allowFileAccess = true
        webview_general.settings.allowFileAccessFromFileURLs = true
        webview_general.settings.allowUniversalAccessFromFileURLs = true
        webview_general.settings.allowContentAccess = true
        webview_general.webViewClient = PageLoadedEventWebViewClient {
            beaconInfoViewModel.beaconInformation.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    beaconInfo?.let { webview_general?.loadUrl("javascript:GeneralView.Actions.setBeaconInformation('${it}')") }
                })
        }
        webview_general.loadUrl(HTML_FILE_PATH)
        webview_general.addJavascriptInterface(this, "native")
        requestBeaconInfo()
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
    fun changeConnectible(checked: Boolean) {
        //beaconViewModel.beacon.value?.connectible = checked
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
