package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.fragment_beacon_general2.*

class BeaconSettingsArbitraryDataHtmlFragment : SettingsBaseFragment() {

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
        webview_general.addJavascriptInterface(UpdateBeaconJsInterfaceImpl(), "Android")
        webview_general.webChromeClient = WebAppChromeClient()
        webview_general.webViewClient = PageLoadedEventWebViewClient {
            settingsActivitySharedViewModel.beaconJsonLiveDataForFragments.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    beaconInfo?.let {
                        webview_general?.loadUrl(
                            javascriptCallGenerator.generateCall(
                                "init",
                                it
                            )
                        )
                    }
                })
        }
        webview_general.loadUrl(HTML_FILE_PATH)
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