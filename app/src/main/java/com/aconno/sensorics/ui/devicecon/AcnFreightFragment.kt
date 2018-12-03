package com.aconno.sensorics.ui.devicecon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.*
import com.aconno.sensorics.BluetoothConnectService
import com.aconno.sensorics.R
import com.aconno.sensorics.device.bluetooth.BluetoothGattCallback
import com.aconno.sensorics.domain.format.ConnectionCharacteristicsFinder
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.getRealName
import com.aconno.sensorics.toHexByte
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.base.BaseFragment
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_acnfreight.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class AcnFreightFragment : BaseFragment() {

    @Inject
    lateinit var connectionCharacteristicsFinder: ConnectionCharacteristicsFinder

    private var connectResultDisposable: Disposable? = null

    private lateinit var mDevice: Device
    private var serviceConnect: BluetoothConnectService? = null

    private var isServicesDiscovered = false

    private val writeCommandQueue: Queue<WriteCommand> = ArrayDeque<WriteCommand>()
    private var latestColor = Color.RED

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.d("Disconnected")
            connectResultDisposable?.dispose()
            serviceConnect = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serviceConnect = (service as BluetoothConnectService.LocalBinder).getService()
            Timber.d("Connected")

            connectResultDisposable = serviceConnect!!.getConnectResults()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!isAdded) {
                        return@subscribe
                    }

                    Timber.d(it.action)
                    val text: String

                    when {
                        it.action == BluetoothGattCallback.ACTION_GATT_DEVICE_NOT_FOUND -> {
                            text = getString(R.string.device_not_found)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTING -> {
                            text = getString(R.string.connecting)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CONNECTED -> {
                            text = getString(R.string.connected)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_SERVICES_DISCOVERED -> {
                            isServicesDiscovered = true
                            progressbar?.visibility = View.INVISIBLE
                            enableToggleViews()
                            text = getString(R.string.discovered)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_DISCONNECTED -> {
                            isServicesDiscovered = false
                            progressbar?.visibility = View.INVISIBLE
                            disableToggleViews()
                            text = getString(R.string.disconnected)

                            serviceConnect?.close()
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_ERROR -> {
                            isServicesDiscovered = false
                            text = getString(R.string.error)
                        }
                        it.action == BluetoothGattCallback.ACTION_GATT_CHAR_WRITE -> {
                            writeCommandQueue.poll()
                            writeCharacteristics(writeCommandQueue.peek())
                            text = getString(R.string.connected)
                        }
                        else -> {
                            return@subscribe
                        }
                    }

                    lbl_status?.text = getString(R.string.status_txt, text)
                }

            serviceConnect?.connect(mDevice.macAddress)
            progressbar?.visibility = View.VISIBLE
        }
    }

    private fun disableToggleViews() {
        btn_buzzer.isEnabled = false
        btn_color_picker.isEnabled = false
    }

    private fun enableToggleViews() {
        btn_buzzer.isEnabled = true
        btn_color_picker.isEnabled = true
    }

    private fun writeCharacteristics(cmd: WriteCommand?) {
        cmd?.let {
            serviceConnect!!.writeCharacteristic(
                it.serviceUUID,
                it.charUUID,
                it.type,
                it.value
            )
        }
    }

    private fun addWriteCommand(serviceUUID: UUID, charUUID: UUID, type: String, value: ByteArray) {
        val writeCommand = WriteCommand(serviceUUID, charUUID, type, value)
        writeCommandQueue.add(writeCommand)
        writeCharacteristics(writeCommandQueue.peek())
    }

    override fun onResume() {
        super.onResume()
        val mainActivity: MainActivity? = context as MainActivity
        mainActivity?.supportActionBar?.title = mDevice.getRealName()
        mainActivity?.supportActionBar?.subtitle = mDevice.macAddress
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        getParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_acnfreight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gattServiceIntent = Intent(
            context, BluetoothConnectService::class.java
        )

        disableToggleViews()

        btn_color_picker.setOnClickListener {
            createColorPickerDialog()
        }
        btn_buzzer.setOnClickListener {
            it.isSelected = !it.isSelected
            toggleBuzzerCharacteristic(it.isSelected)
        }

        Timber.d("Bind Service")
        context!!.bindService(
            gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE
        )
    }

    private fun toggleBuzzerCharacteristic(turnOn: Boolean) {
        val deviceWrite = mDevice.connectionWriteList!![0]
        val turnOnIndex = if (turnOn) 0 else 1

        addWriteCommand(
            UUID.fromString(deviceWrite.serviceUUID),
            UUID.fromString(deviceWrite.characteristicUUID),
            deviceWrite.values[turnOnIndex].type,
            byteArrayOf(deviceWrite.values[turnOnIndex].value.toHexByte())
        )
    }

    private fun createColorPickerDialog() {
        ColorPickerDialogBuilder
            .with(context)
            .setTitle("Choose color")
            .initialColor(latestColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
            .density(12)
            .setPositiveButton(
                "ok"
            ) { _, selectedColor, _ ->
                latestColor = selectedColor
                btn_color_picker.setBackgroundColor(selectedColor)
                writeColorCharacteristic(selectedColor)
            }
            .setNegativeButton(
                "cancel"
            ) { _, _ -> }
            .build()
            .show()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.clear()
        activity?.menuInflater?.inflate(R.menu.menu_fragment_connect, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_toggle_connect ->
                if (item.isChecked) {
                    item.isChecked = false

                    //stop
                    serviceConnect?.disconnect()
                    item.title = getString(R.string.connect)
                    true
                } else {
                    item.isChecked = true

                    //start
                    progressbar?.visibility = View.VISIBLE
                    serviceConnect?.connect(mDevice.macAddress)
                    item.title = getString(R.string.disconnect)
                    true
                }
            else -> {
                false
            }
        }
    }

    private fun writeColorCharacteristic(color: Int) {
        val hex = Integer.toHexString(color)

        val red = "0x${hex.subSequence(2, 4)}".toHexByte()
        val green = "0x${hex.subSequence(4, 6)}".toHexByte()
        val blue = "0x${hex.subSequence(6, 8)}".toHexByte()

        var deviceWrite = mDevice.connectionWriteList!![1]


        addWriteCommand(
            UUID.fromString(deviceWrite.serviceUUID),
            UUID.fromString(deviceWrite.characteristicUUID),
            deviceWrite.values[1].type,
            byteArrayOf(red)
        )

        deviceWrite = mDevice.connectionWriteList!![2]


        addWriteCommand(
            UUID.fromString(deviceWrite.serviceUUID),
            UUID.fromString(deviceWrite.characteristicUUID),
            deviceWrite.values[1].type,
            byteArrayOf(green)
        )

        deviceWrite = mDevice.connectionWriteList!![3]

        addWriteCommand(
            UUID.fromString(deviceWrite.serviceUUID),
            UUID.fromString(deviceWrite.characteristicUUID),
            deviceWrite.values[1].type,
            byteArrayOf(blue)
        )
    }

    private fun getParams() {
        val device = Gson().fromJson(
            arguments!!.getString(KEY_DEVICE)
            , Device::class.java
        )

        mDevice = connectionCharacteristicsFinder.addCharacteristicsToDevice(device)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Destroy")
        connectResultDisposable?.dispose()
        context?.unbindService(serviceConnection)
        serviceConnect = null
    }

    companion object {
        private const val KEY_DEVICE = "KEY_DEVICE"

        fun newInstance(device: Device): AcnFreightFragment {

            val bundle = Bundle()
            bundle.putString(KEY_DEVICE, Gson().toJson(device))

            val fragment = AcnFreightFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}