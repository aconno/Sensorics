package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_general.*

class BeaconSettingsArbitraryDataHtmlFragment : SettingsBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            webview_general.restoreState(savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        webview_general.settings.javaScriptEnabled = true
        webview_general.webChromeClient = WebAppChromeClient()
        webview_general.addJavascriptInterface(UpdateBeaconJsInterfaceImpl(), "native")
        webview_general.webViewClient = mandatoryWebViewClient
        webview_general.loadUrl(HTML_FILE_PATH)
    }

    override fun receivedBeaconInfo(beaconInfo: String) {
        val jsCode = jsGenerator.generateCall("init", beaconInfo)
        webview_general?.loadUrl(jsCode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webview_general.saveState(outState)
        super.onSaveInstanceState(outState)
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
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/arbitrary/ArbitraryData.html"

        @JvmStatic
        fun newInstance() =
            BeaconSettingsArbitraryDataHtmlFragment()
    }
}