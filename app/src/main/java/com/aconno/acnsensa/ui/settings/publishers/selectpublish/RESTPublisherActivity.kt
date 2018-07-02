package com.aconno.acnsensa.ui.settings.publishers.selectpublish

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.restpublisher.DaggerRESTPublisherComponent
import com.aconno.acnsensa.dagger.restpublisher.RESTPublisherComponent
import com.aconno.acnsensa.dagger.restpublisher.RESTPublisherModule
import com.aconno.acnsensa.data.converter.PublisherIntervalConverter
import com.aconno.acnsensa.data.publisher.RESTPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.model.RESTHeaderModel
import com.aconno.acnsensa.model.RESTPublishModel
import com.aconno.acnsensa.model.mapper.RESTHeaderModelMapper
import com.aconno.acnsensa.model.mapper.RESTPublishModelDataMapper
import com.aconno.acnsensa.ui.base.BaseActivity
import com.aconno.acnsensa.ui.settings.publishers.DeviceSelectFragment
import com.aconno.acnsensa.ui.settings.publishers.rheader.RESTHeadersActivity
import com.aconno.acnsensa.viewmodel.RestPublisherViewModel
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_restpublisher.*
import kotlinx.android.synthetic.main.layout_datastring.*
import kotlinx.android.synthetic.main.layout_publisher_header.*
import kotlinx.android.synthetic.main.layout_rest.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RESTPublisherActivity : BaseActivity() {

    @Inject
    lateinit var restPublisherViewModel: RestPublisherViewModel

    private var restPublishModel: RESTPublishModel? = null
    private var isTestingAlreadyRunning: Boolean = false
    private var restHeaderList: ArrayList<RESTHeaderModel> = arrayListOf()

    private val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            progressbar.visibility = View.VISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@RESTPublisherActivity,
                getString(R.string.testings_started),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionSuccess() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@RESTPublisherActivity,
                getString(R.string.test_succeeded),
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onConnectionFail() {
            progressbar.visibility = View.INVISIBLE
            isTestingAlreadyRunning = false
            Toast.makeText(
                this@RESTPublisherActivity,
                getString(R.string.test_failed),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val restPublisherComponent: RESTPublisherComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication

        DaggerRESTPublisherComponent.builder().appComponent(acnSensaApplication?.appComponent)
            .rESTPublisherModule(RESTPublisherModule(this)).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restpublisher)
        restPublisherComponent.inject(this)

        setSupportActionBar(custom_toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        initViews()
        if (intent.hasExtra(REST_PUBLISHER_ACTIVITY_KEY)) {
            restPublishModel =
                    intent.getParcelableExtra(REST_PUBLISHER_ACTIVITY_KEY)

            setFields()
        }

        val fragment = DeviceSelectFragment.newInstance(restPublishModel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_publish_menu, menu)

        if (menu != null) {
            val item = menu.findItem(R.id.action_publish_done)
            if (restPublishModel != null) {
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
        if (resultCode == Activity.RESULT_OK && requestCode == RESTHeadersActivity.EDIT_HEADERS_REQUEST_CODE) {
            val list =
                data!!.getParcelableArrayListExtra<RESTHeaderModel>(RESTHeadersActivity.REST_HEADERS_ACTIVITY_LIST_KEY)
            restHeaderList.clear()
            restHeaderList.addAll(list)
            updateHeaderText()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initViews() {
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
                    0 -> layout_text_http_get.visibility = View.VISIBLE
                    1 -> layout_text_http_get.visibility = View.GONE
                }
            }
        }

        text_http_headers.setOnClickListener {
            RESTHeadersActivity.start(this, restHeaderList)
        }

        updateHeaderText()
    }

    private fun setFields() {
        edit_name.setText(restPublishModel?.name)
        spinner_interval_time.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                restPublishModel?.timeType
            )
        )

        edit_interval_count.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                restPublishModel!!.timeMillis,
                restPublishModel!!.timeType
            )
        )

        if (restPublishModel!!.lastTimeMillis == 0L) {
            text_lastdatasent.visibility = View.GONE
        } else {
            text_lastdatasent.visibility = View.VISIBLE
            val str = getString(R.string.last_data_sent) + " " +
                    millisToFormattedDateString(
                        restPublishModel!!.lastTimeMillis
                    )
            text_lastdatasent.text = str
        }

        edit_datastring.setText(restPublishModel?.dataString)

        edit_url.setText(restPublishModel!!.url)
        val selection = if (restPublishModel!!.method == "GET") 0 else 1
        spinner_methods.setSelection(selection)

        addDisposable(
            restPublisherViewModel.getRESTHeadersById(restPublishModel!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    restHeaderList = ArrayList(it)
                    updateHeaderText()
                }
        )
    }

    private fun updateHeaderText() {
        text_http_headers.text = getString(R.string.headers, restHeaderList.size)
    }

    private fun millisToFormattedDateString(millis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss,SSS", Locale.US)
        val date = Date(millis)

        return sdf.format(date)
    }

    private fun addOrUpdate() {
        val restPublishModel = toRESTPublishModel()
        if (restPublishModel != null) {
            restPublisherViewModel.save(restPublishModel)
                .flatMapCompletable { id ->
                    addRelationsToRest(id).mergeWith(addHeadersToREST(id))
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
                            this@RESTPublisherActivity,
                            e.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun addHeadersToREST(it: Long): Completable {
        return restPublisherViewModel.addRESTHeader(
            restHeaderList, it
        )
    }

    private fun addRelationsToRest(rId: Long): Completable {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame) as DeviceSelectFragment
        val devices = fragment.getDevices()

        val setOfCompletable: MutableSet<Completable> = mutableSetOf()

        devices.forEach {
            val completable = if (it.related) {
                restPublisherViewModel.addOrUpdateRestRelation(
                    deviceId = it.macAddress,
                    restId = rId
                )
            } else {
                restPublisherViewModel.deleteRelationRest(
                    deviceId = it.macAddress,
                    restId = rId
                )
            }

            setOfCompletable.add(
                completable
            )
        }

        return Completable.merge(setOfCompletable)
    }

    private fun toRESTPublishModel(): RESTPublishModel? {
        val name = edit_name.text.toString().trim()
        val url = edit_url.text.toString().trim()
        val method = spinner_methods.selectedItem.toString()
        val parameterName = layout_text_http_get.editText!!.text.toString()
        val timeType = spinner_interval_time.selectedItem.toString()
        val timeCount = edit_interval_count.text.toString()
        val datastring = edit_datastring.text.toString()

        if (restPublisherViewModel.checkFieldsAreEmpty(
                name,
                url,
                method,
                timeType,
                timeCount,
                datastring
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        if (method == "GET" && restPublisherViewModel.checkFieldsAreEmpty(parameterName)) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        }

        val id = if (restPublishModel == null) 0 else restPublishModel!!.id
        val timeMillis = PublisherIntervalConverter.calculateMillis(timeCount, timeType)
        val lastTimeMillis = if (restPublishModel == null) 0 else restPublishModel!!.lastTimeMillis
        return RESTPublishModel(
            id,
            name,
            url,
            method,
            parameterName,
            false,
            timeType,
            timeMillis,
            lastTimeMillis,
            datastring
        )
    }

    private fun test() {
        if (!isTestingAlreadyRunning) {
            isTestingAlreadyRunning = true

            Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT).show()

            val toRESTPublishModel = toRESTPublishModel()

            if (toRESTPublishModel == null) {
                isTestingAlreadyRunning = false
                return
            }

            testRESTConnection(toRESTPublishModel)
        }
    }

    private fun testRESTConnection(toRESTPublishModel: RESTPublishModel) {
        val publisher = RESTPublisher(
            RESTPublishModelDataMapper().transform(toRESTPublishModel),
            listOf(Device("TestDevice", "Mac")),
            RESTHeaderModelMapper().toRESTHeaderList(restHeaderList)
        )

        testConnectionCallback.onConnectionStart()
        publisher.test(testConnectionCallback)
    }

    companion object {
        //This is used for the file selector intent
        private const val REST_PUBLISHER_ACTIVITY_KEY = "REST_PUBLISHER_ACTIVITY_KEY"

        fun start(context: Context, restPublishModel: RESTPublishModel? = null) {
            val intent = Intent(context, RESTPublisherActivity::class.java)

            restPublishModel?.let {
                intent.putExtra(
                    REST_PUBLISHER_ACTIVITY_KEY,
                    restPublishModel
                )
            }

            context.startActivity(intent)
        }
    }
}
