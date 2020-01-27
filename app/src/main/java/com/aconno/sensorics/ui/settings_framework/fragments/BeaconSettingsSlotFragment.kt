package com.aconno.sensorics.ui.settings_framework.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import com.aconno.bluetooth.beacon.Slot.Companion.EXTRA_BEACON_SLOT_POSITION
import com.aconno.sensorics.R
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.Slot
import com.aconno.sensorics.device.beacon.Slots
import com.aconno.sensorics.model.javascript.SlotJS
import com.aconno.sensorics.model.mapper.ParametersAdvertisingContentMapper
import com.aconno.sensorics.ui.configure.ViewPagerSlider
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsViewModel
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_beacon_general2.*
import timber.log.Timber
import javax.inject.Inject

open class BeaconSettingsSlotFragment : DaggerFragment() {

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
        webview_general.settings.javaScriptEnabled = true
        webview_general.addJavascriptInterface(WebAppInterface(), "Android")
        webview_general.webViewClient = WebAppClient()
        webview_general.loadUrl(HTML_FILE_PATH)
    }


    //Prevent running twice or more
    inner class WebAppClient : WebViewClient() {
        var urlFinished: String = ""

        override fun onPageFinished(view: WebView?, url: String?) {

            if (urlFinished != url && "$urlFinished#" != url) {
                getSlotJson()?.let {
                    Timber.d("Call the method again. urlFinished=$urlFinished, url = $url")
                    callJavaScript("init", it)
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
            }
            if (i < params.size - 1) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append(")}catch(error){Android.onError(error.message);}")
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

        val data = beaconViewModel.beacon.value?.slots?.get(slotPosition)?.let {
            SlotJS(
                it.getType().tabName,//Do not change the order
                convertToReadableAdvContent(it),
                it.name,
                it.active,
                getAdvertisingModeStatus(it),
                it.packetCount,
                beacon.supportedTxPowers,
                beacon.supportedTxPowers.indexOf(it.txPower),
                it.readOnly,
                it.advertisingModeParameters.interval
            )
        }?.let {
            convertKeysToJavascriptFormat(Gson().toJson(it))
                .replace("\\u0000", "")
        }
        return data
    }

    private fun getAdvertisingModeStatus(slot: Slot) = when (slot.advertisingMode) {
        Slot.AdvertisingModeParameters.Mode.INTERVAL -> false
        Slot.AdvertisingModeParameters.Mode.EVENT -> true
    }

    private fun convertKeysToJavascriptFormat(slotJson: String): String {
        var convertedJson = slotJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID,
            "KEY_ADVERTISING_CONTENT_IBEACON_UUID"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MINOR"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID,
            "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID,
            "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_URL_URL,
            "KEY_ADVERTISING_CONTENT_URL_URL"
        )
        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
            "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM"
        )

        convertedJson = convertedJson.replace(
            com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA,
            "KEY_ADVERTISING_CONTENT_DEFAULT_DATA"
        )
        return convertedJson
    }

    inner class WebAppInterface {

        @JavascriptInterface
        fun onDataChanged(slotJsonRaw: String) {
            Timber.d("OnDataChanged: $slotJsonRaw")
            val slotJson = convertKeysToOriginals(slotJsonRaw)
            val slotJS = Gson().fromJson<SlotJS>(slotJson, SlotJS::class.java)
            val slotPosition = arguments!!.getInt(EXTRA_BEACON_SLOT_POSITION)
            var dataSlot: Slot? = null

            beaconViewModel.beacon.value?.slots?.get(slotPosition)?.let {
                dataSlot = it
            }

            dataSlot?.let {
                it.name = slotJS.name
                it.advertisingContent.clear()
                it.advertisingContent.putAll(convertToHexAdvContent(slotJS.frame))
                it.packetCount = slotJS.packetCount
                it.advertisingModeParameters.interval = slotJS.addInterval

            }

        }

        @JavascriptInterface
        fun onDataReceived(slotJsonRaw: String) {
            Timber.d("Data is received: $slotJsonRaw")
        }

        private fun convertKeysToOriginals(slotJson: String): String {
            var convertedJson = slotJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_UUID",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_MINOR",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_URL_URL",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_URL_URL
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_DEFAULT_DATA",
                com.aconno.bluetooth.beacon.Slot.KEY_ADVERTISING_CONTENT_DEFAULT_DATA
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

        @JvmStatic
        fun newInstance(slotPosition: Int) =
            BeaconSettingsSlotFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_BEACON_SLOT_POSITION, slotPosition)
                }
            }
    }
}