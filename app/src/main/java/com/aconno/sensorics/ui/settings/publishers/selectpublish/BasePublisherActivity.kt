package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.AlertDialog
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.TypefaceSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.databinding.ActivityGoogleCloudPublisherBinding
import com.aconno.sensorics.databinding.LayoutDatastringBinding
import com.aconno.sensorics.databinding.LayoutPublisherHeaderBinding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

abstract class BasePublisherActivity<M> : BaseActivity() where M : BasePublishModel {

    private lateinit var layoutDatastringBinding: LayoutDatastringBinding
    private lateinit var layoutPublisherBinding: LayoutPublisherHeaderBinding
    private lateinit var googleCloudBinding: ActivityGoogleCloudPublisherBinding

    @Inject
    lateinit var syncRepository: SyncRepository

    protected var isTestingAlreadyRunning: Boolean = false
    protected var model: M? = null
    abstract val viewModel: PublisherViewModel<M>

    protected val testConnectionCallback = object : Publisher.TestConnectionCallback {
        override fun onConnectionStart() {
            GlobalScope.launch(Dispatchers.Main) {
                googleCloudBinding.progressbar.visibility = View.VISIBLE
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
                googleCloudBinding.progressbar.visibility = View.INVISIBLE
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
                googleCloudBinding.progressbar.visibility = View.INVISIBLE
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

        googleCloudBinding = ActivityGoogleCloudPublisherBinding.inflate(layoutInflater)
        layoutPublisherBinding = LayoutPublisherHeaderBinding.inflate(layoutInflater)
        layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)

        initViews()

        if (intent.hasExtra(PUBLISHER_ID_KEY)) {
            val id: Long = intent.getLongExtra(PUBLISHER_ID_KEY, -1)
            addDisposable(
                viewModel.getById(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ model ->
                        this@BasePublisherActivity.model = model
                        setFields(model)
                        initializeDeviceSelectFragment()
                    }, {
                        initializeDeviceSelectFragment()
                    })
            )
        } else {
            initializeDeviceSelectFragment()
        }
    }

    open fun initViews() {
        if (!this::layoutDatastringBinding.isInitialized) {
            layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)
        }

        layoutDatastringBinding.btnInfo.setOnClickListener {
            createAndShowDataStringInfoDialog()
        }
    }

    open fun setFields(model: M) {
        layoutPublisherBinding.editName.setText(model.name)
        layoutPublisherBinding.spinnerIntervalTime.setSelection(
            resources.getStringArray(R.array.PublishIntervals).indexOf(
                model.timeType
            )
        )

        layoutPublisherBinding.editIntervalCount.setText(
            PublisherIntervalConverter.calculateCountFromMillis(
                this,
                model.timeMillis,
                model.timeType
            )
        )

        if (model.lastTimeMillis == 0L) {
            layoutPublisherBinding.textLastdatasent.visibility = View.GONE
        } else {
            layoutPublisherBinding.textLastdatasent.visibility = View.VISIBLE
            val str = getString(R.string.last_data_sent) + " " +
                    millisToFormattedDateString(
                        model.lastTimeMillis
                    )
            layoutPublisherBinding.textLastdatasent.text = str
        }

        layoutDatastringBinding.editDatastring.setText(model.dataString)
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
                        googleCloudBinding.progressbar.visibility = View.INVISIBLE
                        finish()
                    }

                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                        googleCloudBinding.progressbar.visibility = View.VISIBLE
                    }

                    override fun onError(e: Throwable) {
                        googleCloudBinding.progressbar.visibility = View.INVISIBLE
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
        return DataStringConverter().parseAndValidateDataString(layoutDatastringBinding.editDatastring.text.toString())
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
                Toast.makeText(this, getString(R.string.testings_started), Toast.LENGTH_SHORT)
                    .show()
                GlobalScope.launch(Dispatchers.Default) {
                    testConnectionCallback.onConnectionStart()

                    getPublisherForModel(model).test(testConnectionCallback)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_publish_menu, menu)

        menu.findItem(R.id.action_publish_done)?.let { item ->
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

    @Suppress("SameParameterValue")
    protected fun createAndShowInfoDialog(titleTextId: Int, infoTextId: Int) {
        val view = View.inflate(this, R.layout.dialog_alert, null)
        val textView = view.findViewById<TextView>(R.id.message)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(infoTextId)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(titleTextId)
            .setView(view)
            .setNeutralButton(
                R.string.close
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun createAndShowDataStringInfoDialog() {
        val view = View.inflate(this, R.layout.data_string_info_dialog, null)

        val generalValuesTextView = view.findViewById<TextView>(R.id.general_placeholders)
        generalValuesTextView.text = buildGeneralPlaceholderStringInfoText()

        val explanationTextView = view.findViewById<TextView>(R.id.explanation)
        explanationTextView.movementMethod = LinkMovementMethod.getInstance()

        val specificValuesTextView = view.findViewById<TextView>(R.id.specific_values)

        val searchBar = view.findViewById<EditText>(R.id.search_bar)

        viewModel.getAllDeviceParameterPlaceholderStrings()
            .observeOn(AndroidSchedulers.mainThread()).subscribe { placeholdersMap ->
                specificValuesTextView.text = buildPlaceholderStringsInfoText(placeholdersMap)

                val builder = AlertDialog.Builder(this)

                builder.setTitle(R.string.publisher_info_title)
                    .setView(view)
                    .setNeutralButton(
                        R.string.close
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                searchBar.addTextChangedListener(
                    object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            specificValuesTextView.text = buildPlaceholderStringsInfoText(
                                filterPlacholdersMap(
                                    placeholdersMap,
                                    s.toString()
                                )
                            )
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    }
                )
            }.also { addDisposable(it) }
    }

    private fun filterPlacholdersMap(
        placeholdersMap: Map<String, List<String>>,
        filterString: String
    ): Map<String, List<String>> {
        return placeholdersMap.filter {
            val deviceName = it.key
            deviceName.contains(filterString, true)
        }
    }

    private fun buildGeneralPlaceholderStringInfoText(): SpannableStringBuilder {
        val placeholders = resources.getStringArray(R.array.generalDataStringPlaceholders)
        val descriptions =
            resources.getStringArray(R.array.generalDataStringPlaceholderDescriptions)

        val builder = SpannableStringBuilder()
        placeholders.forEachIndexed { index, s ->
            val placeholder = "\$${placeholders[index]}"
            builder.append(placeholder)
            builder.setSpan(
                TypefaceSpan("monospace"), builder.length - placeholder.length, builder.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(" => ${descriptions[index]}\n")
        }

        return builder
    }

    private fun buildPlaceholderStringsInfoText(placeholdersMap: Map<String, List<String>>): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        placeholdersMap.entries.sortedBy { it -> it.key }.forEach {
            val deviceName = it.key
            val params = it.value
            builder.append("$deviceName:\n")
            params.forEach { param ->
                val placeholder = "\$${param}"
                builder.append(placeholder)
                builder.setSpan(
                    TypefaceSpan("monospace"), builder.length - placeholder.length, builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.append("\n")
            }

            if (params.isEmpty()) {
                builder.append("-\n")
            }

            builder.append("\n")
        }

        return builder
    }

    private fun buildPlaceholderStringsInfoText(): Single<SpannableStringBuilder> {
        return viewModel.getAllDeviceParameterPlaceholderStrings().map { map ->
            buildPlaceholderStringsInfoText(map)
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