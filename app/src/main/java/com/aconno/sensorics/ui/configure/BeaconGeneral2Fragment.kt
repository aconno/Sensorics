package com.aconno.sensorics.ui.configure


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_general2.*


class BeaconGeneral2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
    }

    companion object {
        //        const val HTML_FILE_PATH = "file:///android_asset/BeaconConfiguration/views/slot/Slot.html"
        const val HTML_FILE_PATH =
            "file:///android_asset/BeaconConfiguration/views/arbitrary/ArbitraryData.html"

        @JvmStatic
        fun newInstance() =
                BeaconGeneral2Fragment()
    }



}
