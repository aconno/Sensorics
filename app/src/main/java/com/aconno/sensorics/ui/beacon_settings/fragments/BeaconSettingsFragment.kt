package com.aconno.sensorics.ui.beacon_settings.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.dagger.beacon_settings.BeaconSettingsFragmentListener
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_beacon_settings.*
import timber.log.Timber

open class BeaconSettingsFragment() : DaggerFragment() {

    private lateinit var listener: BeaconSettingsFragmentListener

    private val settingsTransporter by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconSettingsTransporterSharedViewModel::class.java)
    }

    protected var jsGenerator = JavascriptCallGenerator()

    /**
     * When we invoke [sendBeaconsUpdatedJson] method it sends data to the settings activity. Then
     * settings activity set this data into liveData for future observers, but we can receive this data
     * again in the current fragment. So we need to avoid event circularity
     */
    private var occurredAfterUpdateSent = false
    /**
     * In some cases we need retrieve data after updating it in current fragment
     */
    protected open var receiveInfoAfterSelfUpdating = false

    inner class SettingsFragmentsWebViewClient : WebViewClient() {
        private var isPageAlreadyLoaded = false

        override fun onPageFinished(view: WebView?, url: String?) {
            Timber.d("page $url loaded")
            super.onPageFinished(view, url)
            if (viewIsNotDestroyed() && !isPageAlreadyLoaded) {
                settingsTransporter.beaconJsonLiveDataForFragments.observe(
                    viewLifecycleOwner,
                    Observer {
                        it?.let { beaconInfo ->
                            if (shouldPropagate()) {
                                Timber.d("received beaconInfo : $beaconInfo")
                                receivedBeaconInfo(beaconInfo)
                            }
                            occurredAfterUpdateSent = false
                        }
                    })
                isPageAlreadyLoaded = true
            } else {
                Timber.w("view is destroyed or page $url already loaded. not invoking pageLoaded callback")
            }
        }

        private fun shouldPropagate() = receiveInfoAfterSelfUpdating || !occurredAfterUpdateSent
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_settings,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVebViewWithWebClient(SettingsFragmentsWebViewClient())

        if (savedInstanceState != null)
            web_view.restoreState(savedInstanceState)
    }

    open fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        with(web_view) {
            settings.builtInZoomControls = false
            settings.javaScriptEnabled = true
            web_view.webChromeClient = WebAppChromeClient()
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.allowContentAccess = true
            addJavascriptInterface(this@BeaconSettingsFragment, "native")
            webViewClient = mandatoryWebViewClient
            loadUrl(HTML_FILE_PATH)
        }
    }

    open fun receivedBeaconInfo(beaconInfo: String) {
        web_view?.loadUrl(
            jsGenerator.generateCall("setBeaconInformation", beaconInfo)
        )
    }


    @JavascriptInterface
    fun updateFirmware() {
        listener.updateFirmware()
    }

    @JavascriptInterface
    fun factoryReset() {
        listener.resetFactory()
    }

    @JavascriptInterface
    fun addPassword() {
        listener.addPassword()
    }

    @JavascriptInterface
    fun powerOff() {
        listener.powerOff()
    }

    @JavascriptInterface
    fun onDataChanged(updatedBeaconJson: String) {
        sendBeaconsUpdatedJson(updatedBeaconJson)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BeaconSettingsFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnBeaconGeneralFragmentInteractionListener")
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
    }

    protected fun sendBeaconsUpdatedJson(updatedBeaconJson: String) {
        Timber.d("Sending updated beacon: $updatedBeaconJson")
        occurredAfterUpdateSent = true
        settingsTransporter.beaconDataChanged(updatedBeaconJson)
    }


    fun viewIsNotDestroyed() = view != null

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/SettingsMain.html"


        fun newInstance() : BeaconSettingsFragment {
            return BeaconSettingsFragment()
        }
    }
}