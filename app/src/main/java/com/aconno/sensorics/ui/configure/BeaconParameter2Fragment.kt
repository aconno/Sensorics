package com.aconno.sensorics.ui.configure


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.aconno.bluetooth.beacon.Parameter
import com.aconno.sensorics.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_beacon_parameter2.*

class BeaconParameter2Fragment : Fragment() {


    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }
    private var standartParameters: List<Parameter>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        standartParameters = beaconViewModel.beacon.value?.parameters?.map?.flatMap { x -> x.value }
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
        webview_parameters.loadUrl(BeaconParameter2Fragment.HTML_FILE_PATH)
        webview_parameters.addJavascriptInterface(this, "native")
    }

    @JavascriptInterface
    fun getParameters(): String {
        standartParameters?.let { parameters ->
            val defaultParameters =
                parameters.map { DefaultParameter.Builder().buildFromParameter(it) }
            return Gson().toJson(defaultParameters)
        }
        return ""
    }

    @JavascriptInterface
    fun setDropDown(id: Int, value: String, position: Int, index: Int) {
        standartParameters?.get(index)?.value = position
    }

    @JavascriptInterface
    fun setTextEdit(id: Int, value: String, index: Int) {
        standartParameters?.get(index)?.value = value
    }

    @JavascriptInterface
    fun setTextNumber(id: Int, value: Int, index: Int) {
        standartParameters?.get(index)?.value = value
    }

    @JavascriptInterface
    fun onSwitchChanged(id: Int, index: Int, value: Boolean) {
        standartParameters?.get(index)?.value = value
    }


    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/parameters/Parameters.html"

        @JvmStatic
        fun newInstance() =
            BeaconParameter2Fragment()
    }


}
