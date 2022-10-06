package com.aconno.sensorics.ui.settings.publishers.selectpublish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.aconno.sensorics.PublisherIntervalConverter
import com.aconno.sensorics.R
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.data.publisher.RestPublisher
import com.aconno.sensorics.databinding.ActivityRestPublisherBinding
import com.aconno.sensorics.databinding.LayoutDatastringBinding
import com.aconno.sensorics.databinding.LayoutPublisherHeaderBinding
import com.aconno.sensorics.databinding.LayoutRestBinding
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.model.RestHeaderModel
import com.aconno.sensorics.model.RestHttpGetParamModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.RESTHeaderModelMapper
import com.aconno.sensorics.model.mapper.RESTHttpGetParamModelMapper
import com.aconno.sensorics.model.mapper.RESTPublishModelDataMapper
import com.aconno.sensorics.ui.settings.publishers.restheader.RestHeadersActivity
import com.aconno.sensorics.ui.settings.publishers.resthttpgetparams.RestHttpGetParamsActivity
import com.aconno.sensorics.viewmodel.PublisherViewModel
import com.aconno.sensorics.viewmodel.RestPublisherViewModel
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class RestPublisherActivity : BasePublisherActivity<RestPublishModel>() {

    private lateinit var binding: ActivityRestPublisherBinding
    private lateinit var layoutDatastringBinding: LayoutDatastringBinding
    private lateinit var layoutPublisherHeaderBinding: LayoutPublisherHeaderBinding
    private lateinit var layoutRestBinding: LayoutRestBinding

    @Inject
    lateinit var restPublisherViewModel: RestPublisherViewModel

    override val viewModel: PublisherViewModel<RestPublishModel>
        get() = restPublisherViewModel

    private var restHeaderList: MutableList<RestHeaderModel> = mutableListOf()
    private var restHttpGetParamList: MutableList<RestHttpGetParamModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityRestPublisherBinding.inflate(layoutInflater)
        layoutDatastringBinding = LayoutDatastringBinding.inflate(layoutInflater)
        layoutPublisherHeaderBinding = LayoutPublisherHeaderBinding.inflate(layoutInflater)
        layoutRestBinding = LayoutRestBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        super.onCreate(savedInstanceState)
    }


    /**
     * This method is called after @Intent.ACTION_OPEN_DOCUMENT result is returned.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let { intentData ->
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == RestHeadersActivity.EDIT_HEADERS_REQUEST_CODE) {
                    restHeaderList.clear()
                    intentData.getParcelableArrayListExtra<RestHeaderModel>(
                        RestHeadersActivity.REST_HEADERS_ACTIVITY_LIST_KEY
                    )?.toList()?.let {
                        restHeaderList.addAll(it)
                    }
                    updateHeaderText()
                } else if (requestCode == RestHttpGetParamsActivity.EDIT_HTTPGET_PARAMS_REQUEST_CODE) {
                    restHttpGetParamList.clear()
                    intentData.getParcelableArrayListExtra<RestHttpGetParamModel>(
                        RestHttpGetParamsActivity.REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY
                    )?.toList()?.let {
                        restHttpGetParamList.addAll(it)
                    }
                    updateHttpGetParamText()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun initViews() {
        super.initViews()

        layoutRestBinding.spinnerMethods.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    when (position) {
                        0 -> layoutDatastringBinding.viewSwitcher.displayedChild = 1
                        1, 2 -> layoutDatastringBinding.viewSwitcher.displayedChild = 0
                    }
                }
            }

        layoutRestBinding.textHttpHeaders.setOnClickListener {
            RestHeadersActivity.start(this, restHeaderList)
        }

        layoutDatastringBinding.textHttpGetParams.setOnClickListener {
            RestHttpGetParamsActivity.start(this, restHttpGetParamList)
        }

        updateHeaderText()
        updateHttpGetParamText()
    }

    override fun setFields(model: RestPublishModel) {
        super.setFields(model)

        layoutRestBinding.editUrl.setText(model.url)
        val selection = when (model.method) {
            "GET" -> 0
            "POST" -> 1
            else -> 2
        }
        layoutRestBinding.spinnerMethods.setSelection(selection)

        addDisposable(
            restPublisherViewModel.getRESTHeadersById(model.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    restHeaderList = ArrayList(it)
                    updateHeaderText()
                }
        )

        addDisposable(
            restPublisherViewModel.getRESTHttpGetParamsById(model.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    restHttpGetParamList = ArrayList(it)
                    updateHttpGetParamText()
                }
        )
    }

    private fun updateHeaderText() {
        layoutRestBinding.textHttpHeaders.text = getString(R.string.headers, restHeaderList.size)
    }

    private fun updateHttpGetParamText() {
        layoutDatastringBinding.textHttpGetParams.text =
            getString(R.string.http_get_params, restHttpGetParamList.size)
    }

    override fun processViewModelSaveId(id: Long, model: RestPublishModel): Completable {
        return if (model.method == "GET") {
            Completable.concatArray(
                addRelations(id),
                addHeadersToREST(id),
                addHTTPGetParamsToREST(id)
            )
        } else {
            Completable.concatArray(
                addRelations(id),
                addHeadersToREST(id)
            )
        }
    }

    private fun addHeadersToREST(it: Long): Completable {
        return restPublisherViewModel.addRESTHeader(
            restHeaderList, it
        )
    }

    private fun addHTTPGetParamsToREST(it: Long): Completable {
        return restPublisherViewModel.addRESTHttpGetParams(
            restHttpGetParamList, it
        )
    }

    override fun toPublishModel(): RestPublishModel? {
        val name = layoutPublisherHeaderBinding.editName.text.toString().trim()
        val url = layoutRestBinding.editUrl.text.toString().trim()
        val method = layoutRestBinding.spinnerMethods.selectedItem.toString()
        val timeType = layoutPublisherHeaderBinding.spinnerIntervalTime.selectedItem.toString()
        val timeCount = layoutPublisherHeaderBinding.editIntervalCount.text.toString()
        val dataString = layoutDatastringBinding.editDatastring.text.toString()
        if (viewModel.checkFieldsAreEmpty(
                name,
                url,
                method,
                timeType,
                timeCount
            )
        ) {
            Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT).show()
            return null
        } else {
            val isNotOk = if (method == "GET") {
                restHttpGetParamList.isEmpty()
            } else {
                dataString.isBlank()
            }

            if (isNotOk) {
                Toast.makeText(this, getString(R.string.please_fill_blanks), Toast.LENGTH_SHORT)
                    .show()
                return null
            } else if (!isDataStringValid()) {
                if (method == "GET") {
                    Toast.makeText(
                        this,
                        getString(R.string.http_get_params_not_valid),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.data_string_not_valid),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                return null
            }
        }

        val id = model?.id ?: 0
        val timeMillis = PublisherIntervalConverter.calculateMillis(this, timeCount, timeType)
        val lastTimeMillis = model?.lastTimeMillis ?: 0
        return RestPublishModel(
            id,
            name,
            url,
            method,
            model?.enabled ?: true,
            timeType,
            timeMillis,
            lastTimeMillis,
            dataString
        )
    }

    override fun isDataStringValid(): Boolean {
        return DataStringConverter().let { converter ->
            if (model?.method == "GET") {
                converter.parseAndValidateDataString(Gson().toJson(restHttpGetParamList))
            } else {
                converter.parseAndValidateDataString(layoutDatastringBinding.editDatastring.text.toString())
            }
        }
    }

    override fun getPublisherForModel(model: RestPublishModel): Publisher<*> {
        return RestPublisher(
            RESTPublishModelDataMapper().transform(model),
            listOf(Device("TestDevice", "Name", "Mac")),
            RESTHeaderModelMapper().toRESTHeaderList(restHeaderList),
            RESTHttpGetParamModelMapper().toRESTHttpGetParamList(restHttpGetParamList),
            syncRepository
        )
    }

    companion object {
        fun start(context: Context, id: Long? = null) {
            val intent = Intent(context, RestPublisherActivity::class.java)

            id?.let {
                intent.putExtra(
                    PUBLISHER_ID_KEY,
                    id
                )
            }

            context.startActivity(intent)
        }
    }
}
