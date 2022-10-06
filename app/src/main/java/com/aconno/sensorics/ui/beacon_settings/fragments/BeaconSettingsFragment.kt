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
import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.dagger.beacon_settings.BeaconSettingsFragmentListener
import com.aconno.sensorics.databinding.FragmentBeaconSettingsBinding
import com.aconno.sensorics.domain.serialization.JavascriptCallGenerator
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import dagger.android.support.DaggerFragment
import timber.log.Timber

open class BeaconSettingsFragment() : DaggerFragment() {

    private lateinit var listener: BeaconSettingsFragmentListener

    private var binding: FragmentBeaconSettingsBinding? = null

    private val settingsTransporter by lazy {
        ViewModelProvider(requireActivity()).get(BeaconSettingsTransporterSharedViewModel::class.java)
    }

    private var jsGenerator = JavascriptCallGenerator()

    /**
     * When we invoke [sendBeaconsUpdatedJson] method it sends data to the settings activity. Then
     * settings activity set this data into liveData for future observers, but we can receive this data
     * again in the current fragment. So we need to avoid event circularity
     */
    private var occurredAfterUpdateSent = false

    /**
     * In some cases we need retrieve data after updating it in current fragment
     */
    protected open var receiveInfoAfterSelfUpdating = true

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
        binding = FragmentBeaconSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVebViewWithWebClient(SettingsFragmentsWebViewClient())

        if (savedInstanceState != null)
            binding?.webView?.restoreState(savedInstanceState)
    }

    private fun setupVebViewWithWebClient(mandatoryWebViewClient: WebViewClient) {
        binding?.let {
            with(it.webView) {
                settings.builtInZoomControls = false
                settings.javaScriptEnabled = true
                binding?.webView?.webChromeClient = WebAppChromeClient()
                settings.allowFileAccess = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.allowContentAccess = true
                addJavascriptInterface(this@BeaconSettingsFragment, "native")
                webViewClient = mandatoryWebViewClient
                loadUrl(HTML_FILE_PATH)
            }
        }
    }

    fun receivedBeaconInfo(beaconInfo: String) {
        binding?.webView?.loadUrl(
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    inner class WebAppChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress < 100 && binding?.progressPage?.visibility == ProgressBar.GONE) {
                binding?.progressPage?.visibility = ProgressBar.VISIBLE
            }

            binding?.progressPage?.progress = newProgress;
            if (newProgress == 100) {
                binding?.progressPage?.visibility = ProgressBar.GONE
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
            "file:///android_asset/resources/settings/views/main/SettingsMain.html"


        fun newInstance(): BeaconSettingsFragment {
            return BeaconSettingsFragment()
        }
    }
}