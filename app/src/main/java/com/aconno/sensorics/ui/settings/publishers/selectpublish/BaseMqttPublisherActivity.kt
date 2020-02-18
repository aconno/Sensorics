package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.ui.base.BaseActivity
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mqtt_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

abstract class BaseMqttPublisherActivity<T : BasePublishModel> : BaseActivity() {

    @Inject
    lateinit var syncRepository: SyncRepository

    private var isTestingAlreadyRunning: Boolean = false

    protected abstract var progressBar : ProgressBar
    protected abstract var publishModel : T?
    protected abstract var layoutId : Int
    protected abstract var deviceSelectFrameId : Int
    protected abstract var publisherKey : String

    protected abstract fun onTestConnectionSuccess()
    protected abstract fun onTestConnectionFail(exception: Throwable?)

    //true when updating a persisted publisher, false when creating a new one
    protected abstract var updating : Boolean

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            GlobalScope.launch(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                        this@BaseMqttPublisherActivity,
                        getString(R.string.testings_started),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onConnectionSuccess() {
            GlobalScope.launch(Dispatchers.Main) {
                progressBar.visibility = View.INVISIBLE
                isTestingAlreadyRunning = false
                this@BaseMqttPublisherActivity.onTestConnectionSuccess()
                Toast.makeText(
                        this@BaseMqttPublisherActivity,
                        getString(R.string.test_succeeded),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onConnectionFail(exception: Throwable?) {
            GlobalScope.launch(Dispatchers.Main) {
                progressBar.visibility = View.INVISIBLE
                isTestingAlreadyRunning = false
                this@BaseMqttPublisherActivity.onTestConnectionFail(exception)
                Toast.makeText(
                        this@BaseMqttPublisherActivity,
                        getString(R.string.test_failed),
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        initViews()
        if (intent.hasExtra(publisherKey)) {
            publishModel =
                    intent.getParcelableExtra(publisherKey)
            setFields()
        }

        val fragment = DeviceSelectFragment.newInstance(publishModel)
        supportFragmentManager.beginTransaction()
                .replace(deviceSelectFrameId, fragment)
                .commit()
    }

    protected open fun initViews() {
        btn_info.setOnClickListener {
            createAndShowInfoDialog(R.string.publisher_info_text,R.string.publisher_info_title)
        }
    }

    private fun setFields() {
        publishModel?.let { model ->
            edit_name.setText(model.name)

            edit_interval_count.setText(
                    PublisherIntervalConverter.calculateCountFromMillis(
                            this,
                            model.timeMillis,
                            model.timeType
                    )
            )

            spinner_interval_time.setSelection(
                    resources.getStringArray(R.array.PublishIntervals).indexOf(
                            model.timeType
                    )
            )

            if (model.lastTimeMillis == 0L) {
                text_lastdatasent.visibility = View.GONE
            } else {
                text_lastdatasent.visibility = View.VISIBLE
                val str = getString(R.string.last_data_sent) + " " +
                        millisToFormattedDateString(
                                model.lastTimeMillis
                        )
                text_lastdatasent.text = str
            }

            setPublisherSpecificFields()

        }
    }

    //sets values from publish model to UI, but only values that are specific for this publisher, not values from layout_publisher_header
    protected abstract fun setPublisherSpecificFields()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (updating) {
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

    protected fun createAndShowInfoDialog(textResourceId : Int, titleResourceId : Int) {
        val view = View.inflate(this, R.layout.dialog_alert, null)
        val textView = view.findViewById<TextView>(R.id.message)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(textResourceId)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(titleResourceId)
                .setView(view)
                .setNeutralButton(
                        R.string.close
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    protected fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    protected fun isDataStringValid(): Boolean {

        val converter = DataStringConverter()

        val dataString = edit_datastring.text.toString()
        return converter.parseAndValidateDataString(dataString)
    }

    protected abstract fun toPublishModel() : T?

    //returns a Single object representing the job of saving the specified publisher
    protected abstract fun savePublisher(publishModel: BasePublishModel) : Single<Long>

    private fun addOrUpdate() {
        val publishModel = toPublishModel()
        if (publishModel != null) {
            savePublisher(publishModel)
                    .flatMapCompletable {
                        addPublishDeviceRelations(it)
                    }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            progressBar.visibility = View.INVISIBLE
                            finish()
                        }

                        override fun onSubscribe(d: Disposable) {
                            addDisposable(d)
                            progressBar.visibility = View.VISIBLE
                        }

                        override fun onError(e: Throwable) {
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                    this@BaseMqttPublisherActivity,
                                    e.message,
                                    Toast.LENGTH_SHORT
                            )
                                    .show()
                        }
                    })
        }
    }

    protected abstract fun addOrUpdateRelation(deviceId : String, publisherId : Long) : Completable
    protected abstract fun deleteRelation(deviceId : String, publisherId : Long) : Completable

    private fun addPublishDeviceRelations(publisherId: Long): Completable? {
        val fragment = supportFragmentManager.findFragmentById(deviceSelectFrameId) as DeviceSelectFragment
        val devices = fragment.getDevices()

        val setOfCompletable: MutableSet<Completable> = mutableSetOf()

        devices.forEach {
            val completable = if (it.related) {
                addOrUpdateRelation(it.macAddress,publisherId)
            } else {
                deleteRelation(it.macAddress,publisherId)
            }

            setOfCompletable.add(
                    completable
            )
        }

        return Completable.merge(setOfCompletable)
    }

    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            val publishModel = toPublishModel()

            if (publishModel == null) {
                isTestingAlreadyRunning = false
                return
            }

            testConnection(publishModel)
        }

    }

    protected abstract fun getPublisherFor(publishModel: T) : Publisher

    private fun testConnection(publishModel: T) {
        GlobalScope.launch(Dispatchers.Default) {
            val publisher = getPublisherFor(publishModel)

            testConnectionCallback.onConnectionStart()

            publisher.test(testConnectionCallback)
        }
    }



}