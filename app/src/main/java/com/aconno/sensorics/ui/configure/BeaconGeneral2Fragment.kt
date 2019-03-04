package com.aconno.sensorics.ui.configure


import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import com.aconno.bluetooth.beacon.BeaconInfo
import com.aconno.sensorics.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_beacon_general2.*


class BeaconGeneral2Fragment : Fragment() {

    var beaconInfo: BeaconInfo? = null
    private var listener: BeaconGeneralFragmentListener? = null

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        beaconInfo = BeaconInfo.Builder().build(beaconViewModel.beacon.value) //beaconViewModel.beacon.value

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon_general2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateWebView()
    }

    private fun initiateWebView() {
        webview_general.settings.builtInZoomControls = false
        webview_general.settings.javaScriptEnabled = true
        webview_general.settings.allowFileAccess = true
        webview_general.settings.allowFileAccessFromFileURLs = true
        webview_general.settings.allowUniversalAccessFromFileURLs = true
        webview_general.settings.allowContentAccess = true
        webview_general.loadUrl(HTML_FILE_PATH)
        webview_general.addJavascriptInterface(this,"native")
    }

    @JavascriptInterface
    fun retrieveCurrentBeaconInfo(): String {
        beaconInfo?.let {
            return Gson().toJson(beaconInfo)
        }
        return ""
    }

    @JavascriptInterface
    fun updateFirmware(){
        listener?.updateFirmware()
    }

    @JavascriptInterface
    fun factoryReset(){
        listener?.resetFactory()
    }

    @JavascriptInterface
    fun addPassword(){
        listener?.addPassword()
    }

    @JavascriptInterface
    fun powerOff(){
        listener?.powerOff()
    }

    @JavascriptInterface
    fun changeConnectible(checked: Boolean){
        beaconViewModel.beacon.value?.connectible = checked
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
            "file:///android_asset/BeaconConfiguration/views/general/General.html"

        @JvmStatic
        fun newInstance() =
                BeaconGeneral2Fragment()
    }



}
