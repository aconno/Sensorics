package com.aconno.acnsensa.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.addpublish.AddPublishComponent
import com.aconno.acnsensa.dagger.addpublish.AddPublishModule
import com.aconno.acnsensa.dagger.addpublish.DaggerAddPublishComponent
import com.aconno.acnsensa.data.converter.PublisherIntervalConverter
import com.aconno.acnsensa.data.publisher.GoogleCloudPublisher
import com.aconno.acnsensa.data.publisher.RESTPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.BasePublishModel
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.ui.base.BaseActivity
import com.aconno.acnsensa.viewmodel.PublishViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_publish.*
import kotlinx.android.synthetic.main.layout_google.*
import kotlinx.android.synthetic.main.layout_rest.*
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AddPublishActivity : BaseActivity(), Publisher.TestConnectionCallback {


    @Inject
    lateinit var publishViewModel: PublishViewModel

    private var basePublish: BasePublishModel? = null
    private var isTestingAlreadyRunning: Boolean = false
    private lateinit var deviceList: List<DeviceRelationModel>

    private val addPublishComponent: AddPublishComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerAddPublishComponent.builder().appComponent(acnSensaApplication?.appComponent)
            .addPublishModule(AddPublishModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_publish)

        addPublishComponent.inject(this)

        val temp = intent.getParcelableExtra<BasePublishModel>(ADD_PUBLISH_ACTIVITY_KEY)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        initViews()

        when {
            temp is BasePublishModel -> {
                basePublish = temp
                setTextsWithPublishData()
            }
            temp != null -> throw IllegalArgumentException("Only classes that extend from BasePublishModel can be sent")
        }
    }

    private fun initViews() {
        edit_privatekey.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(
                intent,
                PICKFILE_REQUEST_CODE
            )

        }

        spinner_toolbar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        layout_google.visibility = View.VISIBLE
                        layout_rest.visibility = View.GONE
                    }
                    1 -> {
                        layout_rest.visibility = View.VISIBLE
                        layout_google.visibility = View.GONE
                    }
                }
            }
        }

        spinner_methods.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> text_http_get.visibility = View.VISIBLE
                    1 -> text_http_get.visibility = View.GONE
                }
            }
        }

        val subscribe = publishViewModel.getAllDevices()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                deviceList = it!!
                addDevices(deviceList)

                if (basePublish != null) {
                    if (basePublish is GooglePublishModel) {
                        addDisposable(
                            publishViewModel.getDevicesThatConnectedWithGooglePublish((basePublish as GooglePublishModel).id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(Consumer {
                                    updateDeviceList(it)
                                })
                        )
                    } else if (basePublish is RESTPublishModel) {
                        addDisposable(
                            publishViewModel.getDevicesThatConnectedWithRESTPublish((basePublish as RESTPublishModel).id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(Consumer {
                                    updateDeviceList(it)
                                })
                        )
                    }
                }
            })
        addDisposable(subscribe)
    }

    private fun addDevices(deviceList: List<DeviceRelationModel>) {
        layout_devices.removeAllViews()

        deviceList.forEach {
            layout_devices.addView(getDeviceView(it))
        }
    }

    private fun getDeviceView(it: DeviceRelationModel): View? {
        val inflatedView =
            layoutInflater.inflate(R.layout.item_device_switch, layout_devices, false)

        val nameView = inflatedView.findViewById<TextView>(R.id.name)
        val macAddressView = inflatedView.findViewById<TextView>(R.id.mac_address)
        val switchView = inflatedView.findViewById<Switch>(R.id.switch_device)

        nameView.text = it.name
        macAddressView.text = it.macAddress

        //TODO : Check if device is active for this publish
        switchView.isChecked = it.related

        return inflatedView
    }

    private fun testRESTConnection(toRESTPublishModel: RESTPublishModel?) {
        val publisher = RESTPublisher(
            RESTPublishModelDataMapper().transform(toRESTPublishModel!!),
            listOf(Device("TestDevice", "Mac"))
        )

        publisher.test(this)
    }

    private fun testGoogleConnection(toGooglePublishModel: GooglePublishModel?) {
        val publisher = GoogleCloudPublisher(
            applicationContext,
            GooglePublishModelDataMapper().transform(toGooglePublishModel!!),
            listOf(Device("TestDevice", "Mac"))
        )

        publisher.test(this)
    }


    override fun onSuccess() {
        isTestingAlreadyRunning = false
        Toast.makeText(this, getString(R.string.test_succeeded), Toast.LENGTH_SHORT).show()
    }

    override fun onFail() {
        isTestingAlreadyRunning = false
        Toast.makeText(this, getString(R.string.test_failed), Toast.LENGTH_SHORT).show()
    }

    /**
     * This method is called after @Intent.ACTION_OPEN_DOCUMENT result is returned.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICKFILE_REQUEST_CODE) {
            data?.let {
                val path = it.data.toString()

                applicationContext.contentResolver.takePersistableUriPermission(
                    data.data,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                if (isFileValidPKCS8(getPrivateKeyData(it.data.toString()))) {
                    edit_privatekey.text = path
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.not_valid_file_pkcs8),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setTextsWithPublishData() {
        edit_name.setText(basePublish?.name)
        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                basePublish?.timeType
            )
        )

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                basePublish!!.timeMillis,
                basePublish!!.timeType
            )
        )

        if (basePublish!!.lastTimeMillis == 0L) {
            text_lastdatasent.visibility = View.GONE
        } else {
            text_lastdatasent.visibility = View.VISIBLE
            val str = getString(R.string.last_data_sent) + " " +
                    millisToFormattedDateString(
                        basePublish!!.lastTimeMillis
                    )
            text_lastdatasent.text = str
        }

        if (basePublish is GooglePublishModel) {
            spinner_toolbar.setSelection(0)
            layout_google.visibility = View.VISIBLE

            val googlePublish = basePublish as GooglePublishModel

            edit_projectid.setText(googlePublish.projectId)
            edit_region.setText(googlePublish.region)
            edit_deviceregistry.setText(googlePublish.deviceRegistry)
            edit_device.setText(googlePublish.device)
            edit_privatekey.text = googlePublish.privateKey
        } else if (basePublish is RESTPublishModel) {
            spinner_toolbar.setSelection(1)
            layout_rest.visibility = View.VISIBLE

            val restPublish = basePublish as RESTPublishModel

            edit_url.setText(restPublish.url)
            val selection = if (restPublish.method == "GET") 0 else 1
            spinner_methods.setSelection(selection)
        }
    }

    private fun updateDeviceList(list: MutableList<DeviceRelationModel>) {
        if (list.size == 0) return

        deviceList.forEachIndexed { index, it ->
            list.forEach { related ->
                if (it.macAddress == related.macAddress) {
                    it.related = true
                    updateDeviceViewAt(index)
                    return@forEach
                }
            }
        }
    }

    private fun updateDeviceViewAt(index: Int) {
        val childAt = layout_devices.getChildAt(index)
        childAt.findViewById<Switch>(R.id.switch_device)
            .isChecked = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (basePublish != null) {
                item.title = getString(R.string.update)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_publish_done -> addOrUpdate()
            R.id.action_publish_test -> test()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT).show()
            if (spinner_toolbar.selectedItemPosition == 0) {
                val toGooglePublishModel = toGooglePublishModel()
                testGoogleConnection(toGooglePublishModel)
            } else if (spinner_toolbar.selectedItemPosition == 1) {
                val toRESTPublishModel = toRESTPublishModel()
                testRESTConnection(toRESTPublishModel)
            }
        }
    }

    private fun addOrUpdate() {
        val selectedItem = spinner_toolbar.selectedItemPosition

        when (selectedItem) {
            0 -> googleAddOrUpdate()
            1 -> restAddOrUpdate()
            else -> throw IllegalArgumentException("Please use registered types.")
        }

        //After save or update finish activity
        Handler()
            .postDelayed(
                {
                    finish()
                }, 1000
            )
    }

    private fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    private fun restAddOrUpdate() {
        val restPublishModel = toRESTPublishModel()
        if (restPublishModel != null) {
            addDisposable(
                publishViewModel.save(restPublishModel)
                    .subscribe(Consumer {
                        addRelationsToREST(it)
                    })
            )
        }
    }

    private fun addRelationsToREST(rId: Long) {
        val count = layout_devices.childCount

        for (i in 0..(count - 1)) {
            val deviceRelationModel = deviceList[i]

            val isChecked =
                layout_devices.getChildAt(i).findViewById<Switch>(R.id.switch_device).isChecked

            if (isChecked) {
                addDisposable(
                    publishViewModel.addOrUpdateRestRelation(
                        deviceId = deviceRelationModel.macAddress,
                        restId = rId
                    )
                )
            } else {
                addDisposable(
                    publishViewModel.deleteRelationRest(
                        deviceId = deviceRelationModel.macAddress,
                        restId = rId
                    )
                )
            }
        }
    }

    private fun toRESTPublishModel(): RESTPublishModel? {
        val name = edit_name.text.toString().trim()
        val url = edit_url.text.toString().trim()
        val method = spinner_methods.selectedItem.toString()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()

        if (publishViewModel.checkFieldsAreEmpty(
                name,
                url,
                method,
                timeType,
                timeCount
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (basePublish == null) 0 else basePublish!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (basePublish == null) 0 else basePublish!!.lastTimeMillis
        return RESTPublishModel(
            id,
            name,
            url,
            method,
            false,
            timeType,
            timeMillis,
            lastTimeMillis
        )
    }

    private fun googleAddOrUpdate() {
        val googlePublishModel = toGooglePublishModel()
        if (googlePublishModel != null) {
            addDisposable(
                publishViewModel.save(googlePublishModel)
                    .subscribe(Consumer {
                        addRelationsToGoogle(it)
                    })
            )
        }
    }

    private fun addRelationsToGoogle(gId: Long) {
        val count = layout_devices.childCount

        for (i in 0..(count - 1)) {
            val deviceRelationModel = deviceList[i]

            val isChecked =
                layout_devices.getChildAt(i).findViewById<Switch>(R.id.switch_device).isChecked

            if (isChecked) {
                addDisposable(
                    publishViewModel.addOrUpdateGoogleRelation(
                        deviceId = deviceRelationModel.macAddress,
                        googleId = gId
                    )
                )
            } else {
                addDisposable(
                    publishViewModel.deleteRelationGoogle(
                        deviceId = deviceRelationModel.macAddress,
                        googleId = gId
                    )
                )
            }
        }
    }

    private fun toGooglePublishModel(): GooglePublishModel? {
        val name = edit_name.text.toString().trim()
        val projectId = edit_projectid.text.toString().trim()
        val region = edit_region.text.toString().trim()
        val deviceRegistry = edit_deviceregistry.text.toString().trim()
        val device = edit_device.text.toString().trim()
        val privateKey = edit_privatekey.text.toString().trim()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()

        if (publishViewModel.checkFieldsAreEmpty(
                name,
                projectId,
                region,
                deviceRegistry,
                device,
                privateKey,
                timeType,
                timeCount
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (basePublish == null) 0 else basePublish!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (basePublish == null) 0 else basePublish!!.lastTimeMillis
        return GooglePublishModel(
            id,
            name,
            projectId,
            region,
            deviceRegistry,
            device,
            privateKey,
            false,
            timeType,
            timeMillis,
            lastTimeMillis
        )
    }


    private fun isFileValidPKCS8(byteArray: ByteArray): Boolean {
        val spec = PKCS8EncodedKeySpec(byteArray)
        val kf = KeyFactory.getInstance("RSA")

        try {
            kf.generatePrivate(spec)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getPrivateKeyData(privateKey: String): ByteArray {
        val uri = Uri.parse(privateKey)
        val stream = contentResolver.openInputStream(uri)

        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        return buffer
    }

    companion object {
        //This is used for the file selector intent
        const val PICKFILE_REQUEST_CODE: Int = 10213
        const val ADD_PUBLISH_ACTIVITY_KEY = "ADD_PUBLISH_ACTIVITY_KEY"

        fun start(context: Context, basePublish: BasePublishModel? = null) {
            val intent = Intent(context, AddPublishActivity::class.java)

            basePublish?.let {
                intent.putExtra(
                    ADD_PUBLISH_ACTIVITY_KEY,
                    basePublish
                )
            }

            context.startActivity(intent)
        }
    }
}
