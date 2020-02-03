package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.R
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_DEFAULT_DATA
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_MINOR
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_IBEACON_UUID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
import com.aconno.sensorics.device.beacon.Slot.Companion.KEY_ADVERTISING_CONTENT_URL_URL
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.model.mapper.ParametersAdvertisingContentMapper
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsViewModel
import com.aconno.sensorics.ui.settings_framework.ViewPagerSlider
import kotlinx.android.synthetic.main.fragment_beacon_general2.*
import timber.log.Timber
import javax.inject.Inject

open class BeaconSettingsSlotFragment : BeaconSettingsBaseFragment() {


    private val beaconViewModel: BeaconSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BeaconSettingsViewModel::class.java)
    }

    private lateinit var beacon: Beacon

    private lateinit var slots: Slots

    private var slotPosition: Int = 0

    @Inject
    lateinit var parametersAdContentMapper: ParametersAdvertisingContentMapper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            slotPosition = it.getInt(EXTRA_BEACON_SLOT_POSITION)

            beaconViewModel.beacon.value?.let { beacon ->
                this.beacon = beacon
                this.slots = beacon.slots

            } ?: throw IllegalStateException(
                "Started BeaconSlotFragment without beacon!"
            )
        } ?: throw IllegalStateException(
            "Started BeaconSlotFragment without EXTRA_BEACON_SLOT_POSITION argument!"
        )
        return inflater.inflate(R.layout.fragment_beacon_general2, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null)
            webview_general.restoreState(savedInstanceState)
        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_general.settings.builtInZoomControls = false
        webview_general.settings.javaScriptEnabled = true
        webview_general.settings.allowFileAccess = true
        webview_general.settings.allowFileAccessFromFileURLs = true
        webview_general.settings.allowUniversalAccessFromFileURLs = true
        webview_general.settings.allowContentAccess = true
        webview_general.addJavascriptInterface(this, "native")
        webview_general.webViewClient = PageLoadedEventWebViewClient {
            beaconInfoViewModel.beaconInformation.observe(
                viewLifecycleOwner,
                Observer { beaconInfo ->
                    beaconInfo?.let {
                        //                        getSlotJson()?.let {
//                            Timber.d("Call the method again")
//                            callJavaScript("init", it)
//                        }
                        callJavaScript("init", it, slotPosition)
                    }
                })
        }
        webview_general.loadUrl(HTML_FILE_PATH)
        requestBeaconInfo()
    }


    //Prevent running twice or more
    inner class WebAppClient : WebViewClient() {
        var urlFinished: String = ""

        override fun onPageFinished(view: WebView?, url: String?) {

            if (urlFinished != url) {
                getSlotJson()?.let {
                    Timber.d("Call the method again")
                    callJavaScript("init", it, slotPosition)
                }
            }
            url?.let {
                urlFinished = it
            }
            super.onPageFinished(view, url)
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_save -> {
                /*
                * beaconViewModel.beacon.value?.slots?.get(slotPosition)
                * should get the latest slots.
                */
                Timber.d("Values: ${getSlotJson()}")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getSlotJson(): String? {
        val slotPosition = arguments!!.getInt(EXTRA_BEACON_SLOT_POSITION)

//        val data = beaconViewModel.beacon.value?.slots?.get(slotPosition)?.let {
//            SlotJS(
//                it.getType().tabName,//Do not change the order
//                convertToReadableAdvContent(it),
//                it.name,
//                it.active,
//                getAdvertisingModeStatus(it),
//                it.packetCount,
//                beacon.supportedTxPowers,
//                beacon.supportedTxPowers.indexOf(it.txPower),
//                it.readOnly,
//                it.advertisingModeParameters.interval
//            )
//        }?.let {
//            convertKeysToJavascriptFormat(Gson().toJson(it))
//                .replace("\\u0000", "")
//        }
        return ""
    }

    private fun getAdvertisingModeStatus(slot: Slot) = when (slot.advertisingMode) {
        Slot.AdvertisingModeParameters.Mode.INTERVAL -> false
        Slot.AdvertisingModeParameters.Mode.EVENT -> true
    }

    private fun convertKeysToJavascriptFormat(slotJson: String): String {
        var convertedJson = slotJson.replace(
            KEY_ADVERTISING_CONTENT_IBEACON_UUID,
            "KEY_ADVERTISING_CONTENT_IBEACON_UUID"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MINOR"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID,
            "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID,
            "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_URL_URL,
            "KEY_ADVERTISING_CONTENT_URL_URL"
        )
        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
            "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM"
        )

        convertedJson = convertedJson.replace(
            KEY_ADVERTISING_CONTENT_DEFAULT_DATA,
            "KEY_ADVERTISING_CONTENT_DEFAULT_DATA"
        )
        return convertedJson
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

    private fun convertToReadableAdvContent(slot: Slot): MutableMap<String, String> {
        return if (slot.getType() == Slot.Type.CUSTOM || slot.getType() == Slot.Type.DEFAULT) {
            parametersAdContentMapper.getReadableAdContent(
                slot.advertisingContent,
                beacon.parameters
            )
        } else slot.advertisingContent
    }


    private fun convertToHexAdvContent(adContent: MutableMap<String, String>): Map<out String, String> {
        return parametersAdContentMapper.getHexAdContent(adContent, beacon.parameters)
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