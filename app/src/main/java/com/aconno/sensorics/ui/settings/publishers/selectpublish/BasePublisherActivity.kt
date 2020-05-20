package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.AlertDialog
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
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.ui.base.BaseActivity
import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import com.aconno.sensorics.viewmodel.PublisherViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_google_cloud_publisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

abstract class BasePublisherActivity<M> : BaseActivity() where M : BasePublishModel {
    @Inject
    lateinit var syncRepository: SyncRepository

    protected var isTestingAlreadyRunning: Boolean = false
    protected var model: M? = null
    abstract val viewModel: PublisherViewModel<M>

    protected val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            GlobalScope.launch(Dispatchers.Main) {
                progressbar.visibility = View.VISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                    this@BasePublisherActivity,
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
                    this@BasePublisherActivity,
                    getString(R.string.test_succeeded),
                    Toast.LENGTH_SHORT
                ).show()
                onTestConnectionSuccess()
            }
        }

        override fun onConnectionFail(exception: Throwable?) {
            GlobalScope.launch(Dispatchers.Main) {
                progressbar.visibility = View.INVISIBLE
                isTestingAlreadyRunning = false
                Toast.makeText(
                    this@BasePublisherActivity,
                    getString(R.string.test_failed),
                    Toast.LENGTH_SHORT
                ).show()
                onTestConnectionFail(exception)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

        if (intent.hasExtra(PUBLISHER_ID_KEY)) {
            val id: Long = intent.getLongExtra(PUBLISHER_ID_KEY, -1)
            addDisposable(viewModel.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ model ->
                    this@BasePublisherActivity.model = model
                    setFields(model)
                    initializeDeviceSelectFragment()
                }, {
                    initializeDeviceSelectFragment()
                }))
        } else {
            initializeDeviceSelectFragment()
        }
    }

    open fun initViews() {
        btn_info.setOnClickListener {
            createAndShowInfoDialog(R.string.publisher_info_title, R.string.publisher_info_text)
        }
    }

    open fun setFields(model: M) {
        edit_name.setText(model.name)
        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                model.timeType
            )
        )

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                this,
                model.timeMillis,
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

        edit_datastring.setText(model.dataString)
    }

    open fun processViewModelSaveId(id: Long, model: M): Completable {
        return addRelations(id)
    }

    open fun addOrUpdate() {
        toPublishModel()?.let { model ->
            viewModel.save(model)
                .flatMapCompletable { processViewModelSaveId(it, model) }
                .subscribeOn(Schedulers.io())
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
                            this@BasePublisherActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    open fun isDataStringValid(): Boolean {
        return DataStringConverter().parseAndValidateDataString(edit_datastring.text.toString())
    }

    abstract fun toPublishModel(): M?
    abstract fun getPublisherForModel(model: M): Publisher<*>
    protected open fun onTestConnectionSuccess() {}
    protected open fun onTestConnectionFail(exception: Throwable?) {}


    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            val model = toPublishModel()
            if (model == null) {
                isTestingAlreadyRunning = false
            } else {
                Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT).show()
                GlobalScope.launch(Dispatchers.Default) {
                    testConnectionCallback.onConnectionStart()

                    getPublisherForModel(model).test(testConnectionCallback)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_publish_menu, menu)

        menu?.findItem(R.id.action_publish_done)?.let { item ->
            model?.let {
                item.title = getString(R.string.update)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_publish_done -> addOrUpdate()
            R.id.action_publish_test -> test()
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun addRelations(id: Long): Completable {
        val completeables: MutableSet<Completable> = mutableSetOf()

        (supportFragmentManager.findFragmentById(R.id.frame) as? DeviceSelectFragment)?.let { f ->
            val devices = f.getDevices()


            devices.map {
                if (it.related) {
                    viewModel.addOrUpdateRelation(
                        it.macAddress,
                        id
                    )
                } else {
                    viewModel.deleteRelation(
                        it.macAddress,
                        id
                    )
                }
            }.also {
                completeables.addAll(it)
            }
        }

        return Completable.merge(completeables)
    }

    private fun initializeDeviceSelectFragment() {
        val fragment = DeviceSelectFragment.newInstance(model)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

    // NOTE FOR LATER DEFAULT INFO TEXT ID IS R.string.publisher_info_text
    // DEFAULT TITLE ID IS R.string.publisher_info_title
    protected fun createAndShowInfoDialog(titleTextId: Int, infoTextId: Int) {
        val view = View.inflate(this, R.layout.dialog_alert, null)
        val textView = view.findViewById<TextView>(R.id.message)
        textView.movementMethod = LinkMovementMethod.getInstance()
        buildPlaceholderStringsInfoText().observeOn(AndroidSchedulers.mainThread()).subscribe { text ->
            textView.text = getString(infoTextId,text)

            val builder = AlertDialog.Builder(this)

            builder.setTitle(titleTextId)
                .setView(view)
                .setNeutralButton(
                    R.string.close
                ) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }.also { addDisposable(it) }


    }

    private fun buildPlaceholderStringsInfoText() : Single<String> {
        return viewModel.getAllDeviceParameterPlaceholderStrings().map { map ->
            val builder = StringBuilder()
            map.entries.sortedBy { it -> it.key }.forEach {
                val deviceName = it.key
                val params = it.value
                builder.append("$deviceName: ")
                builder.append(params.joinToString(", "))

                if(params.isEmpty()) {
                    builder.append("-")
                }

                builder.append("\n")
            }

            builder.toString()
        }
    }

    protected fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    companion object {
        const val PUBLISHER_ID_KEY = "PUBLISHER_ID_KEY"
    }
}