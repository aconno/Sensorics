package com.aconno.sensorics.ui.configure

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import com.aconno.sensorics.R
import com.aconno.sensorics.model.javascript.ArbitraryDataJS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_beacon_general2.*
import timber.log.Timber


class BeaconArbitraryDataHtmlFragment : Fragment() {

    private val beaconViewModel: BeaconViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_general2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            webview_general.restoreState(savedInstanceState)

        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_general.settings.javaScriptEnabled = true
        webview_general.addJavascriptInterface(WebAppInterface(), "Android")
        webview_general.webViewClient = WebAppClient()
        webview_general.webChromeClient = WebAppChromeClient()
        webview_general.loadUrl(HTML_FILE_PATH)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webview_general.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    private fun callJavaScript(methodName: String, vararg params: Any) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("javascript:try{")
        stringBuilder.append(methodName)
        stringBuilder.append("(")
        for (i in params.indices) {
            val param = params[i]
            if (param is String) {
                stringBuilder.append("'")
                stringBuilder.append(param.toString().replace("'", "\\'"))
                stringBuilder.append("'")
            }
            if (i < params.size - 1) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append(")}catch(error){Android.onError(error.message);}")
        webview_general.loadUrl(stringBuilder.toString())
    }

    inner class WebAppClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (isAdded) {
                getArbitraryJsonArray()?.let {
                    callJavaScript("init", it)
                }
            }
        }
    }

    private fun getArbitraryJsonArray(): String? {
        return beaconViewModel.beacon.value?.arbitraryData?.map?.map {
            ArbitraryDataJS(it.key, it.value)
        }?.takeIf {
            it.isNotEmpty()
        }?.let {
            Gson().toJson(it)
        }
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun onDataChanged(arbitraryDataJSONArray: String) {
            val listType = object : TypeToken<List<ArbitraryDataJS>>() {}.type
            val arbitraryDataList =
                Gson().fromJson<List<ArbitraryDataJS>>(arbitraryDataJSONArray, listType)

            beaconViewModel.beacon.value?.arbitraryData?.map?.clear()
            arbitraryDataList.forEach {
                beaconViewModel.beacon.value?.arbitraryData?.map?.put(
                    it.key,
                    it.value
                )
            }
        }

        @JavascriptInterface
        fun onError(string: String) {
            Timber.e(string)
        }
    }

    inner class WebAppChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress < 100 && progress_page.visibility == ProgressBar.GONE) {
                progress_page.visibility = ProgressBar.VISIBLE
            }

            progress_page.progress = newProgress;
            if (newProgress == 100) {
                progress_page.visibility = ProgressBar.GONE
            }
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {

            context?.let {
                AlertDialog.Builder(it)
                    .setTitle("Alert")
                    .setMessage(message)
                    .setNeutralButton(
                        android.R.string.ok
                    ) { dialog, _ -> dialog?.dismiss() }
                    .show()
            }

            result?.cancel()
            return true
        }
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/arbitrary/ArbitraryData.html"

        @JvmStatic
        fun newInstance() =
            BeaconArbitraryDataHtmlFragment()
    }
}