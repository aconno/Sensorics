package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsViewModel
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.fragment_beacon_parameter.*
import timber.log.Timber

class BeaconSettingsParametersFragment(fragmentId: Int?) : BeaconSettingsBaseFragment(fragmentId) {

    private val beaconViewModel: BeaconSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconSettingsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_parameter, container, false)
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
        webview_parameters.webViewClient = PageLoadedEventWebViewClient {
            beaconInfoViewModel.beaconInformation.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    beaconInfo?.let { webview_parameters?.loadUrl("javascript:ParametersLoader.setBeaconParameters('$it')") }
                })
        }
        webview_parameters.loadUrl(HTML_FILE_PATH)
        webview_parameters.addJavascriptInterface(this, "native")
    }

    @JavascriptInterface
    fun beaconParametersUpdated(beaconString: String) {
        Timber.d("beacon parameters updated, $beaconString")
        beaconViewModel.beacon.value?.loadChangesFromJson(JsonParser().parse(beaconString).asJsonObject)

    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/parameters/ParametersNew.html"

        @JvmStatic
        fun newInstance(fragmentId: Int? = null) =
            BeaconSettingsParametersFragment(fragmentId)
    }

}
