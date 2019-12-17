package com.aconno.sensorics.ui.settings_framework

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.device.beacon.Parameter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_beacon_parameter2.*
import timber.log.Timber

class BeaconSettingsParametersFragment : Fragment() {

    private val beaconViewModel: BeaconSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconSettingsViewModel::class.java)
    }

    private var standartParameters: List<Parameter<Any>>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        standartParameters = beaconViewModel.beacon.value?.parameters?.flatMap { x -> x.value }
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
        webview_parameters.loadUrl(HTML_FILE_PATH)
        webview_parameters.addJavascriptInterface(this, "native")
    }

    @JavascriptInterface
    fun getParameters(): String {
        standartParameters?.let { parameters ->
            val defaultParameters =
                parameters.map { parameter ->

                    BeaconSettingsDefaultParameter.Builder().buildFromParameter(parameter)

                }
            val json = Gson().toJson(defaultParameters)
            return json
        }

        return ""
    }

    @JavascriptInterface
    fun setDropDown(id: Int, value: String, position: Int, index: Int) {
        standartParameters?.get(index)?.setValue(position)
    }

    @JavascriptInterface
    fun setTextEdit(id: Int, value: String, index: Int) {
        standartParameters?.get(index)?.setValue(value)
    }

    @JavascriptInterface
    fun setTextNumber(id: Int, value: Int, index: Int) {
        standartParameters?.get(index)?.setValue(value)
    }

    @JavascriptInterface
    fun onSwitchChanged(id: Int, index: Int, value: Boolean) {
        standartParameters?.get(index)?.setValue(value)
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/parameters/ParametersNew.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsParametersFragment()
    }

}
