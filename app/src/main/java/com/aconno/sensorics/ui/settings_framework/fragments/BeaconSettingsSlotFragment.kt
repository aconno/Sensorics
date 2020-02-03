package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import androidx.lifecycle.Observer
import com.aconno.sensorics.R
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_DEFAULT_DATA
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MINOR
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_UUID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_URL_URL
import com.aconno.sensorics.ui.settings_framework.ViewPagerSlider
import kotlinx.android.synthetic.main.fragment_beacon_general.*
import timber.log.Timber


open class BeaconSettingsSlotFragment : SettingsBaseFragment() {

    private var slotPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        slotPosition = arguments?.getInt(EXTRA_BEACON_SLOT_POSITION, -1)?.takeIf {
            it != -1
        } ?: throw IllegalStateException(
            "Started BeaconSlotFragment without EXTRA_BEACON_SLOT_POSITION argument!"
        )
        return inflater.inflate(R.layout.fragment_beacon_general, container, false)
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
        webview_general.webViewClient = PageLoadedEventWebViewClient {
            settingsActivitySharedViewModel.beaconJsonLiveDataForFragments.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    beaconInfo?.let {
                        callJavaScript("init", it, slotPosition)
                    }
                })
        }
        webview_general.loadUrl(HTML_FILE_PATH)
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
                stringBuilder.append(param.toString().replace("'", "\\'").replace("\\\"", "&quot;"))
                stringBuilder.append("'")
            } else {
                stringBuilder.append(param)
            }
            if (i < params.size - 1) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append(")}catch(error){console.log(error.message);}")
        webview_general.loadUrl(stringBuilder.toString())
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.beacon_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @JavascriptInterface
    fun onDataChanged(slotJsonRaw: String) {
        Timber.d("OnDataChanged: $slotJsonRaw")
//            beaconViewModel?.beacon?.value?.loadChangesFromJson(JsonParser().parse(slotJsonRaw).asJsonObject)
    }

    @JavascriptInterface
    fun onDataReceived(slotJsonRaw: String) {
        Timber.d("Data is received: $slotJsonRaw")
    }

    private fun convertKeysToOriginals(slotJson: String): String {
        var convertedJson = slotJson.replace(
            "KEY_ADVERTISING_CONTENT_IBEACON_UUID",
            KEY_ADVERTISING_CONTENT_IBEACON_UUID
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR",
            KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_IBEACON_MINOR",
            KEY_ADVERTISING_CONTENT_IBEACON_MINOR
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID",
            KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID",
            KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_URL_URL",
            KEY_ADVERTISING_CONTENT_URL_URL
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM",
            KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
        )
        convertedJson = convertedJson.replace(
            "KEY_ADVERTISING_CONTENT_DEFAULT_DATA",
            KEY_ADVERTISING_CONTENT_DEFAULT_DATA
        )
        return convertedJson
    }

    @JavascriptInterface
    fun stopViewPager() {
        activity?.let {
            it as ViewPagerSlider
        }?.let {
            it.stopViewPager()
        }
    }

    @JavascriptInterface
    fun startViewPager() {
        activity?.let {
            it as ViewPagerSlider
        }?.let {
            it.startViewPager()
        }
    }

    @JavascriptInterface
    fun onError(string: String) {
        Timber.e(string)
    }

    override fun onDestroyView() {
        view?.let {
        }

        super.onDestroyView()
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/resources/settings/views/slot/Slot.html"
        const val EXTRA_BEACON_SLOT_POSITION = "com.aconno.beaconapp.BEACON_SLOT_POSITION"

        @JvmStatic
        fun newInstance(slotPosition: Int) =
            BeaconSettingsSlotFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_BEACON_SLOT_POSITION, slotPosition)
                }
            }
    }
}