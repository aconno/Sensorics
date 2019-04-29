package com.aconno.sensorics.ui.device_main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aconno.sensorics.BluetoothScanningService
import com.aconno.sensorics.BuildConfig
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.ui.*
import com.aconno.sensorics.ui.configure.ConfigureActivity
import com.aconno.sensorics.ui.connect.BluetoothServiceConnection
import com.aconno.sensorics.ui.connect.ConnectFragment
import com.aconno.sensorics.ui.livegraph.LiveGraphFragment
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_device_main.*
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject


@SuppressLint("SetJavaScriptEnabled")
class DeviceMainFragment : DaggerFragment(), ScanStatus,
    BluetoothServiceConnection.ConnectionCallback {

    @Inject
    lateinit var sensorReadingFlow: Flowable<List<Reading>> //TODO: Move this to the view model

    @Inject
    lateinit var filterByMacUseCase: FilterByMacUseCase

    private var sensorReadingFlowDisposable: Disposable? = null

    @Inject
    lateinit var mainResourceViewModel: MainResourceViewModel
    private var getResourceDisposable: Disposable? = null

    private lateinit var mDevice: Device
    private var webViewBundle: Bundle? = null

    private var hasSettings: Boolean = false
    private var status: Boolean = false
    private var bleScanner: BleScanner? = null
    private var connectable: Connectable? = null

    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        getParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_main, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BleScanner && context is Connectable) {
            with(context as BleScanner) {
                bleScanner = this
            }

            with(context as Connectable) {
                connectable = this
                registerConnectionCallback(this@DeviceMainFragment)
            }
        } else {
            Timber.e("Fragment context needs to implement BleScanner or Connectable Interface")
            (context as AppCompatActivity).onBackPressed()
        }
    }

    override fun onDetach() {
        connectable?.unRegisterConnectionCallback(this)
        bleScanner = null
        super.onDetach()
    }

    override fun onStatusTextChanged(stringRes: Int) {
        //No-need
    }

    override fun onHasSettings() {
        hasSettings = true
        activity?.invalidateOptionsMenu()
    }

    override fun onConnected() {
        activity?.invalidateOptionsMenu()
    }

    override fun onDisconnected() {
        activity?.invalidateOptionsMenu()
    }

    fun onServiceClosed() {
        removeConnectFragment()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        activity?.menuInflater?.inflate(R.menu.menu_readings, menu)
        setMenuItemsVisibility(menu)
    }

    private fun setMenuItemsVisibility(menu: Menu?) {
        menu?.let {
            it.findItem(R.id.action_start_usecases_activity).isVisible =
                BuildConfig.FLAVOR == DEV_BUILD_FLAVOR
            it.findItem(R.id.action_toggle_connect).isVisible = mDevice.connectable
            it.findItem(R.id.action_start_config_activity).isVisible = hasSettings
            it.findItem(R.id.action_start_logging_activity).isVisible = hasSettings

            if (connectable?.isConnectedOrConnecting() == true) {
                with(it.findItem(R.id.action_toggle_connect)) {
                    title = getString(com.aconno.sensorics.R.string.disconnect)
                    isChecked = true
                }
            } else {
                with(it.findItem(R.id.action_toggle_connect)) {
                    title = getString(com.aconno.sensorics.R.string.connect)
                    isChecked = false
                }
            }
        }
    }

    override fun setStatus(isOnline: Boolean, force: Boolean) {
        if (isOnline == status && !force) {
            return
        }

        context?.let { context ->
            if (isOnline) {
                setStatusOnline(context)
            } else {
                setStatusOffline(context)
            }
        }
    }

    private fun setStatusOffline(context: Context) {
        status = false
        txt_offline?.text = getString(R.string.offline)
        txt_offline?.setBackgroundColor(
            ContextCompat.getColor(
                context,
                android.R.color.darker_gray
            )
        )
        txt_offline?.visibility = View.VISIBLE
    }

    private fun setStatusOnline(context: Context) {
        status = true
        txt_offline?.text = getString(R.string.online)
        txt_offline?.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.online_green
            )
        )
        txt_offline?.postDelayed(
            {
                txt_offline?.visibility = View.GONE
            }, 500
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(EXTRA_STATUS, status)
        outState.putInt(EXTRA_VISIBILITY, ll_fragment?.visibility ?: View.GONE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        context?.let { context ->
            when (item.itemId) {
                R.id.action_toggle_connect -> {
                    if (item.isChecked) {
                        connectable?.disconnect()
                        item.isChecked = false
                        item.title = getString(R.string.connect)
                    } else {
                        item.isChecked = true
                        connectToBeacon()
                        connectable?.connect(mDevice)
                        item.title = getString(R.string.disconnect)
                    }
                }

                R.id.action_start_actions_activity -> {
                    ActionListActivity.start(context)
                    return true
                }
                R.id.action_start_usecases_activity -> {
                    showUseCaseFragment()
                    return true
                }
                R.id.action_start_config_activity -> {
                    activity?.let {
                        ConfigureActivity.start(it, device = mDevice)
                    }
                    return true
                }
                R.id.action_start_logging_activity -> {
                    this.view?.let {
                        Snackbar.make(
                            it,
                            "Functionality coming soon.",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    //TODO: Implement Logger functionality
                    return true
                }
                R.id.action_delete_beacon -> {
                    removeBeacon()
                    return true
                }
                R.id.action_rename_device -> {
                    renameDevice()
                    return true
                }
                else -> {
                    //Do nothing
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUseCaseFragment() {
        //If it is not visible already
        if (ll_fragment.visibility == View.GONE) {
            ll_fragment.visibility = View.VISIBLE
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(
                    R.id.fl_fragment,
                    UseCasesFragment.newInstance(
                        mDevice.macAddress,
                        mDevice.getRealName()
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showConnectFragment() {
        //If it is not visible already
        if (ll_fragment.visibility == View.GONE) {
            ll_fragment.visibility = View.VISIBLE
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                .replace(
                    R.id.fl_fragment,
                    ConnectFragment.newInstance(mDevice)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun connectToBeacon() {
        if (BluetoothScanningService.isRunning()) {
            bleScanner?.stopScan()
        }

        val fragment = childFragmentManager.fragments.find {
            it is ConnectFragment
        }

        if (fragment == null) {
            showConnectFragment()
        }
    }

    private fun removeBeacon() {
        (activity as? MainActivity2)?.removeCurrentDisplayedBeacon(mDevice.macAddress)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupWebView()

        savedInstanceState?.let {
            setStatus(it.getBoolean(EXTRA_STATUS, false), true)
            ll_fragment?.visibility = it.getInt(EXTRA_VISIBILITY)
        }

        iv_close_fragment?.setOnClickListener {
            removeUseCaseFragment()

            removeLiveGraphFragment()

            removeConnectFragment()
        }
    }

    private fun removeLiveGraphFragment() {
        childFragmentManager.fragments.find {
            it is LiveGraphFragment
        }?.let {
            it as LiveGraphFragment
        }?.let {
            ll_fragment?.postDelayed({
                ll_fragment?.visibility = View.GONE
            }, ANIM_DURATION)

            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_to_left, R.anim.exit_to_left)
                .remove(it)
                .commit()
        }
    }

    private fun removeConnectFragment() {
        childFragmentManager.fragments.find {
            it is ConnectFragment
        }?.let {
            it as ConnectFragment
        }?.let {
            ll_fragment?.postDelayed({
                ll_fragment?.visibility = View.GONE
            }, ANIM_DURATION)

            connectable?.disconnect()
            connectable?.shutDownConnectionService()

            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_to_right, R.anim.exit_to_right)
                .remove(it)
                .commit()
        }
    }

    private fun removeUseCaseFragment() {
        childFragmentManager.fragments.find {
            it is UseCasesFragment
        }?.let {
            it as UseCasesFragment
        }?.let {
            ll_fragment?.postDelayed({
                ll_fragment?.visibility = View.GONE
            }, ANIM_DURATION)

            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_to_right, R.anim.exit_to_right)
                .remove(it)
                .commit()
        }
    }

    private fun setupWebView() {
        web_view.webChromeClient = WebChromeClient()
        web_view.webViewClient = MyWebViewClient()
        web_view.addJavascriptInterface(WebViewJavaScriptInterface(), "app")
        web_view.settings.javaScriptEnabled = true

        if (webViewBundle != null) {
            web_view.restoreState(webViewBundle)
        } else {
            getResourceDisposable = mainResourceViewModel.getResourcePath(mDevice.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { resourcePath ->
                        text_error_message.visibility = View.INVISIBLE
                        web_view.loadUrl(resourcePath)
                    },
                    { throwable ->
                        text_error_message.visibility = View.VISIBLE
                        text_error_message.text = throwable.message
                    })
        }
    }

    private fun subscribeOnSensorReadings() {
        sensorReadingFlowDisposable = sensorReadingFlow
            .concatMap { filterByMacUseCase.execute(it, mDevice.macAddress).toFlowable() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { readings ->
                setStatus(true)

                val jsonValues = generateJsonArray(readings)
                setHasSettings(readings)

                web_view?.loadUrl("javascript:onSensorReadings('$jsonValues')")
            }
    }

    private fun setHasSettings(readings: List<Reading>) {
        hasSettings = readings[0].device.hasSettings
        if (hasSettings != readings[0].device.hasSettings && isVisible) {
            setMenuItemsVisibility(menu)
        }
    }

    private fun generateJsonArray(readings: List<Reading>?): String {

        val jsonObject = JSONObject()

        readings?.forEach {

            if (!it.value.toDouble().isNaN()) {
                jsonObject.put(it.name, it.value)
            }


        }

        return jsonObject.toString()
    }

    override fun onDestroyView() {
        webViewBundle = Bundle()
        web_view.saveState(webViewBundle)

        super.onDestroyView()
        getResourceDisposable?.dispose()
        sensorReadingFlowDisposable?.dispose()
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            subscribeOnSensorReadings()
        }
    }

    inner class WebViewJavaScriptInterface {

        @JavascriptInterface
        fun openLiveGraph(sensorName: String) {
            activity?.apply {
                runOnUiThread {
                    showLiveGraphFragment(sensorName)
                }
            }
        }

        @JavascriptInterface
        fun connect() {
            activity?.let {
                activity?.runOnUiThread {
                    connectToBeacon()
                    connectable?.connect(mDevice)
                    it.invalidateOptionsMenu()
                }
            }
        }
    }

    private fun showLiveGraphFragment(sensorName: String) {
        //If it is not visible
        if (ll_fragment.visibility == View.GONE) {
            ll_fragment.visibility = View.VISIBLE
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_left)
                .replace(
                    R.id.fl_fragment,
                    LiveGraphFragment.newInstance(mDevice.macAddress, sensorName)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getParams() {
        val device = Gson().fromJson(
            arguments!!.getString(KEY_DEVICE)
            , Device::class.java
        )

        mDevice = device
        Timber.i("device is $device")
    }

    private fun uldowAlertDialog(mainActivity: MainActivity2) {

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
            mainActivity
        )

        alertDialogBuilder.setTitle(resources.getString(R.string.start_scan_popup))
        alertDialogBuilder
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->

                mainActivity.startScanOperation()
                dialog.cancel()

            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->

                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun renameDevice() {
        (activity as? MainActivity2)?.showRenameDialog(mDevice.macAddress)
    }

    fun getDevice(): Device? {
        return if (::mDevice.isInitialized) mDevice else null
    }

    companion object {

        private const val KEY_DEVICE = "KEY_DEVICE"
        private const val DEV_BUILD_FLAVOR = "dev"
        private const val EXTRA_STATUS = "EXTRA_STATUS"
        private const val EXTRA_VISIBILITY = "EXTRA_VISIBILITY"
        private const val ANIM_DURATION = 700L

        fun newInstance(
            device: Device
        ): DeviceMainFragment {
            val deviceMainFragment = DeviceMainFragment()
            deviceMainFragment.arguments = Bundle().apply {
                putString(KEY_DEVICE, Gson().toJson(device))
            }
            return deviceMainFragment
        }
    }
}