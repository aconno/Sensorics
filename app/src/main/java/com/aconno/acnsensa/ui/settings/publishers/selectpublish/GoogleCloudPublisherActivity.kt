package com.aconno.acnsensa.ui.settings.publishers.selectpublish

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.gcloudpublisher.DaggerGoogleCloudPublisherComponent
import com.aconno.acnsensa.dagger.gcloudpublisher.GoogleCloudPublisherComponent
import com.aconno.acnsensa.dagger.gcloudpublisher.GoogleCloudPublisherModule
import com.aconno.acnsensa.data.converter.PublisherIntervalConverter
import com.aconno.acnsensa.data.publisher.GoogleCloudPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.DeviceRelationModel
import com.aconno.acnsensa.model.GooglePublishModel
import com.aconno.acnsensa.model.mapper.GooglePublishModelDataMapper
import com.aconno.acnsensa.ui.base.BaseActivity
import com.aconno.acnsensa.viewmodel.GoogleCloudPublisherViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_google_cloud_publisher.*
import kotlinx.android.synthetic.main.layout_google.*
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GoogleCloudPublisherActivity : BaseActivity() {

    @Inject
    lateinit var googleViewModel: GoogleCloudPublisherViewModel

    private var googlePublishModel: GooglePublishModel? = null
    private lateinit var deviceList: List<DeviceRelationModel>
    private var isTestingAlreadyRunning: Boolean = false

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@GoogleCloudPublisherActivity,
                getString(R.string.test_succeeded),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionSuccess() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@GoogleCloudPublisherActivity,
                getString(R.string.test_failed),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionFail() {
            progressbar.visibility = View.VISIBLE
        }
    }

    private val googleCloudPublisherComponent: GoogleCloudPublisherComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerGoogleCloudPublisherComponent
            .builder()
            .appComponent(acnSensaApplication?.appComponent)
            .googleCloudPublisherModule(GoogleCloudPublisherModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_cloud_publisher)
        googleCloudPublisherComponent.inject(this)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        initViews()
        if (intent.hasExtra(GOOGLE_PUBLISHER_ACTIVITY_KEY)) {
            googlePublishModel =
                    intent.getParcelableExtra(GOOGLE_PUBLISHER_ACTIVITY_KEY)

            setFields()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (googlePublishModel != null) {
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
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
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

    private fun initViews() {
        edit_privatekey.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            startActivityForResult(
                intent,
                PICKFILE_REQUEST_CODE
            )

        }

        btn_info.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setTitle(R.string.publisher_info_title)
                .setMessage(R.string.publisher_info_text)
                .setNeutralButton(
                    R.string.okey
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        //Load Devices
        val subscribe = googleViewModel.getAllDevices()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                deviceList = it!!
                addDevices(deviceList)

                if (googlePublishModel != null) {
                    addDisposable(
                        googleViewModel.getDevicesThatConnectedWithGooglePublish(googlePublishModel!!.id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Consumer {
                                updateDeviceList(it)
                            })
                    )
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
        switchView.isChecked = it.related

        return inflatedView
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


    private fun setFields() {
        edit_name.setText(googlePublishModel?.name)

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                googlePublishModel!!.timeMillis,
                googlePublishModel!!.timeType
            )
        )

        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                googlePublishModel?.timeType
            )
        )

        if (googlePublishModel!!.lastTimeMillis == 0L) {
            text_lastdatasent.visibility = View.GONE
        } else {
            text_lastdatasent.visibility = View.VISIBLE
            val str = getString(R.string.last_data_sent) + " " +
                    millisToFormattedDateString(
                        googlePublishModel!!.lastTimeMillis
                    )
            text_lastdatasent.text = str
        }

        edit_projectid.setText(googlePublishModel!!.projectId)
        edit_region.setText(googlePublishModel!!.region)
        edit_deviceregistry.setText(googlePublishModel!!.deviceRegistry)
        edit_device.setText(googlePublishModel!!.device)
        edit_privatekey.text = googlePublishModel!!.privateKey

        edit_datastring.setText(googlePublishModel?.dataString)
    }

    private fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    private fun addOrUpdate() {
        val googlePublishModel = toGooglePublishModel()
        if (googlePublishModel != null) {
            googleViewModel.save(googlePublishModel)
                .flatMapCompletable {
                    addRelationsToGoogle(it)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        progressbar.visibility = View.INVISIBLE
                        finish()
                    }

                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                        progressbar.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable) {
                        progressbar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@GoogleCloudPublisherActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
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
        val datastring = edit_datastring.text.toString()

        if (googleViewModel.checkFieldsAreEmpty(
                name,
                projectId,
                region,
                deviceRegistry,
                device,
                privateKey,
                timeType,
                timeCount,
                datastring
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (googlePublishModel == null) 0 else googlePublishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(timeCount, timeType)
        val lastTimeMillis =
            if (googlePublishModel == null) 0 else googlePublishModel!!.lastTimeMillis
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
            lastTimeMillis,
            datastring
        )
    }

    private fun addRelationsToGoogle(gId: Long): Completable {
        val count = layout_devices.childCount

        val setOfCompletable: MutableSet<Completable> = mutableSetOf()

        for (i in 0..(count - 1)) {
            val deviceRelationModel = deviceList[i]

            val isChecked =
                layout_devices.getChildAt(i).findViewById<Switch>(R.id.switch_device).isChecked

            if (isChecked) {
                setOfCompletable.add(
                    googleViewModel.addOrUpdateGoogleRelation(
                        deviceId = deviceRelationModel.macAddress,
                        googleId = gId
                    )
                )
            } else {
                setOfCompletable.add(
                    googleViewModel.deleteRelationGoogle(
                        deviceId = deviceRelationModel.macAddress,
                        googleId = gId
                    )
                )
            }
        }
        return Completable.merge(setOfCompletable)
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

    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true
            Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT).show()
            val toGooglePublishModel = toGooglePublishModel()

            if (toGooglePublishModel == null) {
                isTestingAlreadyRunning = false
                return
            }

            testGoogleConnection(toGooglePublishModel)
        }
    }

    private fun testGoogleConnection(toGooglePublishModel: GooglePublishModel) {
        val publisher = GoogleCloudPublisher(
            applicationContext,
            GooglePublishModelDataMapper().transform(toGooglePublishModel),
            listOf(Device("TestDevice", "Mac"))
        )

        testConnectionCallback.onConnectionStart()
        publisher.test(testConnectionCallback)
    }

    companion object {
        //This is used for the file selector intent
        const val PICKFILE_REQUEST_CODE: Int = 10213
        private const val GOOGLE_PUBLISHER_ACTIVITY_KEY = "GOOGLE_PUBLISHER_ACTIVITY_KEY"

        fun start(context: Context, googlePublishModel: GooglePublishModel? = null) {
            val intent = Intent(context, GoogleCloudPublisherActivity::class.java)

            googlePublishModel?.let {
                intent.putExtra(
                    GOOGLE_PUBLISHER_ACTIVITY_KEY,
                    googlePublishModel
                )
            }

            context.startActivity(intent)
        }
    }
}
