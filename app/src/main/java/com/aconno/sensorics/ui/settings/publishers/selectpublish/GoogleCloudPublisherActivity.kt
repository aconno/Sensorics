package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.mapper.GooglePublishModelDataMapper
import com.aconno.sensorics.ui.base.BaseActivity
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.viewmodel.GoogleCloudPublisherViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_google_cloud_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_google.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GoogleCloudPublisherActivity : BaseActivity() {

    @Inject
    lateinit var googleViewModel: GoogleCloudPublisherViewModel

    @Inject
    lateinit var syncRepository: SyncRepository

    private var googlePublishModel: GooglePublishModel? = null
    private var isTestingAlreadyRunning: Boolean = false

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            GlobalScope.launch(Dispatchers.Main) {
                progressbar.visibility = View.VISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                    this@GoogleCloudPublisherActivity,
                    getString(R.string.testings_started),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onConnectionSuccess() {
            GlobalScope.launch(Dispatchers.Main) {
                progressbar.visibility = View.INVISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                    this@GoogleCloudPublisherActivity,
                    getString(R.string.test_succeeded),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onConnectionFail(exception: Throwable?) {
            GlobalScope.launch(Dispatchers.Main) {
                progressbar.visibility = View.INVISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                    this@GoogleCloudPublisherActivity,
                    getString(R.string.test_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_cloud_publisher)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        initViews()
        if (intent.hasExtra(GOOGLE_PUBLISHER_ACTIVITY_KEY)) {
            googlePublishModel =
                    intent.getParcelableExtra(GOOGLE_PUBLISHER_ACTIVITY_KEY)

            setFields()
        }

        val fragment = DeviceSelectFragment.newInstance(googlePublishModel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
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
            data?.data?.let {
                val path = it.toString()

                applicationContext.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                if (isFileValidPKCS8(getPrivateKeyData(path))) {
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
            createAndShowInfoDialog()
        }
    }

    private fun createAndShowInfoDialog() {
        val view = View.inflate(this, R.layout.dialog_alert, null)
        val textView = view.findViewById<TextView>(R.id.message)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(R.string.publisher_info_text)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.publisher_info_title)
            .setView(view)
            .setNeutralButton(
                R.string.close
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun setFields() {
        edit_name.setText(googlePublishModel?.name)

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                this,
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
        } else {
            if (!isDataStringValid()) {
                Toast.makeText(
                    this,
                    getString(R.string.data_string_not_valid),
                    Toast.LENGTH_SHORT
                )
                    .show()

                return null
            }
        }

        val id = if (googlePublishModel == null) 0 else googlePublishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
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
            googlePublishModel?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    private fun isDataStringValid(): Boolean {

        val converter = DataStringConverter()

        val dataString = edit_datastring.text.toString()
        return converter.parseAndValidateDataString(dataString)
    }

    private fun addRelationsToGoogle(gId: Long): Completable? {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame) as DeviceSelectFragment
        val devices = fragment.getDevices()

        val setOfCompletable: MutableSet<Completable> = mutableSetOf()

        devices.forEach {
            val completable = if (it.related) {
                googleViewModel.addOrUpdateGoogleRelation(
                    deviceId = it.macAddress,
                    googleId = gId
                )
            } else {
                googleViewModel.deleteRelationGoogle(
                    deviceId = it.macAddress,
                    googleId = gId
                )
            }

            setOfCompletable.add(
                completable
            )
        }

        return Completable.merge(setOfCompletable)
    }

    private fun isFileValidPKCS8(byteArray: ByteArray): Boolean {
        val spec = PKCS8EncodedKeySpec(byteArray)
        val keyFactory = KeyFactory.getInstance("RSA")

        try {
            keyFactory.generatePrivate(spec)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getPrivateKeyData(privateKey: String): ByteArray {
        val uri = Uri.parse(privateKey)
        val stream = contentResolver.openInputStream(uri)

        val size = stream?.available() ?: 0
        val buffer = ByteArray(size)
        stream?.read(buffer)
        stream?.close()
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
        GlobalScope.launch(Dispatchers.Default) {

            val publisher = GoogleCloudPublisher(
                applicationContext,
                GooglePublishModelDataMapper().transform(toGooglePublishModel),
                listOf(Device("TestDevice", "Name", "Mac")),
                syncRepository
            )

            testConnectionCallback.onConnectionStart()

            publisher.test(testConnectionCallback)
        }
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
