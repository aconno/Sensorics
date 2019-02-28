package com.aconno.sensorics.ui.configure

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.aconno.bluetooth.beacon.Slot
import com.aconno.sensorics.R
import com.aconno.sensorics.model.javascript.SlotJS
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_beacon_general2.*
import timber.log.Timber

class BeaconSlotHtmlFragment : Fragment() {

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
        initiateWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initiateWebView() {
        webview_general.settings.javaScriptEnabled = true
        webview_general.addJavascriptInterface(WebAppInterface(), "Android")
        webview_general.webViewClient = WebAppClient()
        webview_general.loadUrl(HTML_FILE_PATH)
        WebView.setWebContentsDebuggingEnabled(true)
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

    //Prevent running twice or more
    var inited = false

    inner class WebAppClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (!inited && isAdded) {
                inited = true

                getSlotJson()?.let {
                    callJavaScript("init", it)
                }
            }
        }
    }

    private fun getSlotJson(): String? {
        val slotPosition = arguments!!.getInt(BeaconSlotFragment.EXTRA_BEACON_SLOT_POSITION)

        return beaconViewModel.beacon.value?.slots?.get(slotPosition)?.let {
            SlotJS(
                it.type.ordinal,//Do not change the order
                it.slotAdvertisingContent,
                it.advertisingInterval.toLong(),//TODO Convert to Long by Default
                it.rssi1m,
                it.radioTx,
                it.triggerEnabled,
                it.triggerType.ordinal//Do not change the order
            )
        }?.let {
            convertKeysToJavascriptFormat(Gson().toJson(it))
                .replace("\\u0000", "")
        }
    }

    private fun convertKeysToJavascriptFormat(slotJson: String): String {
        var convertedJson = slotJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID,
            "KEY_ADVERTISING_CONTENT_IBEACON_UUID"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR,
            "KEY_ADVERTISING_CONTENT_IBEACON_MINOR"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID,
            "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID,
            "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_URL_URL,
            "KEY_ADVERTISING_CONTENT_URL_URL"
        )
        convertedJson = convertedJson.replace(
            Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM,
            "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM"
        )
        return convertedJson
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun onDataChanged(slotJsonRaw: String) {
            val slotJson = convertKeysToOriginals(slotJsonRaw)

            val slotJS = Gson().fromJson<SlotJS>(slotJson, SlotJS::class.java)
            val slotPosition = arguments!!.getInt(BeaconSlotFragment.EXTRA_BEACON_SLOT_POSITION)

            val newSlot = Slot(
                Slot.Type.values()[slotJS.frameType],
                slotJS.frame,
                slotJS.advertisingInterval.toInt(),
                slotJS.rssi1m,
                slotJS.radioTx,
                slotJS.triggerEnabled,
                Slot.TriggerType.values()[slotJS.triggerType]
            )

            beaconViewModel.beacon.value?.slots?.set(slotPosition, newSlot)
            Timber.d(newSlot.toString())
        }

        private fun convertKeysToOriginals(slotJson: String): String {
            var convertedJson = slotJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_UUID",
                Slot.KEY_ADVERTISING_CONTENT_IBEACON_UUID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_MAJOR",
                Slot.KEY_ADVERTISING_CONTENT_IBEACON_MAJOR
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_IBEACON_MINOR",
                Slot.KEY_ADVERTISING_CONTENT_IBEACON_MINOR
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID",
                Slot.KEY_ADVERTISING_CONTENT_UID_NAMESPACE_ID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID",
                Slot.KEY_ADVERTISING_CONTENT_UID_INSTANCE_ID
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_URL_URL",
                Slot.KEY_ADVERTISING_CONTENT_URL_URL
            )
            convertedJson = convertedJson.replace(
                "KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM",
                Slot.KEY_ADVERTISING_CONTENT_CUSTOM_CUSTOM
            )
            return convertedJson
        }

        @JavascriptInterface
        fun onError(string: String) {
            Timber.e(string)
        }
    }

    companion object {
        const val HTML_FILE_PATH =
            "file:///android_asset/BeaconConfiguration/views/slot/Slot.html"

        const val FRAGMENT_TAG_SLOT = "com.aconno.beaconapp.FRAGMENT_SLOT"
        private const val EXTRA_BEACON_SLOT_POSITION = "com.aconno.beaconapp.BEACON_SLOT_POSITION"

        @JvmStatic
        fun newInstance(slotPosition: Int) =
            BeaconSlotHtmlFragment().apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_BEACON_SLOT_POSITION, slotPosition)
                }
            }

    }
}