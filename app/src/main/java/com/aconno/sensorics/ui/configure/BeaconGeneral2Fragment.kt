package com.aconno.sensorics.ui.configure


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_general2.*
import timber.log.Timber


class BeaconGeneral2Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        arguments.let {
            val beacon = it?.getSerializable("OBJ") as? Beacon
            beacon?.let {
                Timber.d("Beacon is ${beacon.address}")
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beacon_general2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiateWebView()
    }

    private fun initiateWebView() {
        webview_general.settings.builtInZoomControls = false
        webview_general.settings.allowFileAccess = true
        webview_general.settings.allowFileAccessFromFileURLs = true
        webview_general.settings.allowUniversalAccessFromFileURLs = true
        webview_general.settings.allowContentAccess = true
        webview_general.loadUrl(HTML_FILE_PATH)
    }

    companion object {
        const val HTML_FILE_PATH = "file:///android_asset/BeaconConfiguration/views/general/General.html"

        @JvmStatic
        fun newInstance(beacon: Beacon) =
                BeaconGeneral2Fragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("OBJ",beacon)
                    }
                }
    }



}
