package com.aconno.sensorics.ui.settings_framework

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.model.javascript.BeaconInfo
import com.aconno.sensorics.ui.configure.BeaconGeneral2Fragment
import com.aconno.sensorics.ui.configure.BeaconGeneralFragmentListener
import com.aconno.sensorics.ui.configure.BeaconViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_beacon_general2.*

class BeaconSettingsGeneralFragment: Fragment() {

    var beaconInfo: BeaconSettingsInfo? = null
    private var listener: BeaconGeneralFragmentListener? = null

    private val beaconViewModel: BeaconSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconSettingsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        beaconInfo =
            BeaconSettingsInfo.Builder().build(beaconViewModel.beacon.value) //beaconViewModel.beacon.value

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
        webview_general.loadUrl(HTML_FILE_PATH)
        webview_general.addJavascriptInterface(this, "native")
    }

    @JavascriptInterface
    fun retrieveCurrentBeaconInfo(): String {
        beaconInfo?.let {
            return Gson().toJson(beaconInfo)
        }
        return ""
    }

    @JavascriptInterface
    fun updateFirmware() {
        listener?.updateFirmware()
    }

    @JavascriptInterface
    fun factoryReset() {
        listener?.resetFactory()
    }

    @JavascriptInterface
    fun addPassword() {
        listener?.addPassword()
    }

    @JavascriptInterface
    fun powerOff() {
        listener?.powerOff()
    }

    @JavascriptInterface
    fun changeConnectible(checked: Boolean) {
        //beaconViewModel.beacon.value?.connectible = checked
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*if (context is BeaconGeneralFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBeaconGeneralFragmentInteractionListener")
        }*/
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/general/General.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsGeneralFragment()
    }

}
