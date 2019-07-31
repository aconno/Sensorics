package com.aconno.sensorics.ui.settings.publishers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.data.ReadTextUseCase
import com.aconno.sensorics.domain.interactor.data.StoreTempTextUseCase
import com.aconno.sensorics.domain.interactor.data.StoreTextUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToPublishersUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertPublishersToJsonUseCase
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.ui.settings.publishers.selectpublish.SelectPublisherActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_publish_list.*
import java.io.File
import javax.inject.Inject

private const val CODE_SHARE = 1
private const val CODE_EXPORT = 2
private const val CODE_IMPORT = 3

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishListFragment.OnListFragmentClickListener] interface.
 */
class PublishListFragment : BaseFragment(), PublishRecyclerViewAdapter.OnListItemLongClickListener {

    private lateinit var tempSharedFile: File

    private var jsonData: String? = null

    private var snackbar: Snackbar? = null

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    @Inject
    lateinit var convertPublishersToJsonUseCase: ConvertPublishersToJsonUseCase

    @Inject
    lateinit var convertJsonToPublishersUseCase: ConvertJsonToPublishersUseCase

    @Inject
    lateinit var storeTextUseCase: StoreTextUseCase

    @Inject
    lateinit var storeTempTextUseCase: StoreTempTextUseCase

    @Inject
    lateinit var readTextUseCase: ReadTextUseCase

    @Inject
    lateinit var restPublishModelDataMapper: RESTPublishModelDataMapper

    @Inject
    lateinit var mqttPublishModelDataMapper: MqttPublishModelDataMapper

    @Inject
    lateinit var googlePublishModelDataMapper: GooglePublishModelDataMapper

    @Inject
    lateinit var restPublishDataMapper: RESTPublishDataMapper

    @Inject
    lateinit var googlePublishDataMapper: GooglePublishDataMapper

    private lateinit var publishAdapter: PublishRecyclerViewAdapter

    private var listener: OnListFragmentClickListener? = null
    private var listBasePublish: MutableList<BasePublishModel> = mutableListOf()
    private var selectedItem: BasePublishModel? = null

    private val checkedChangeListener: PublishRecyclerViewAdapter.OnCheckedChangeListener = object :
        PublishRecyclerViewAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(checked: Boolean, position: Int) {
            val item = listBasePublish[position]
            item.enabled = checked
            addDisposable(publishListViewModel.update(item))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_publish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        publishAdapter = PublishRecyclerViewAdapter(
            listBasePublish,
            listener,
            this
        )

        view_publish_list.layoutManager = LinearLayoutManager(context)
        view_publish_list.adapter = publishAdapter

        view_publish_list.itemAnimator = DefaultItemAnimator()
        view_publish_list.addItemDecoration(
            DividerItemDecoration(
                context,
                (view_publish_list.layoutManager as LinearLayoutManager).orientation
            )
        )

        context?.let { context ->
            ItemTouchHelper(object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val publishModel = publishAdapter.getPublishModel(position)
                    publishAdapter.removePublishModel(position)

                    snackbar = Snackbar.make(
                        container_fragment,
                        "${publishModel.name} removed!",
                        Snackbar.LENGTH_LONG
                    ).also {
                        it.setAction(getString(R.string.undo)) {
                            publishAdapter.addPublishModelAtPosition(publishModel, position)
                        }

                        it.addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                if (event == DISMISS_EVENT_TIMEOUT
                                    || event == DISMISS_EVENT_CONSECUTIVE
                                    || event == DISMISS_EVENT_SWIPE
                                    || event == DISMISS_EVENT_MANUAL
                                ) {
                                    selectedItem = publishModel
                                    deleteSelectedItem()
                                }
                            }
                        })

                        it.setActionTextColor(Color.YELLOW)

                        it.show()
                    }
                }
            }).attachToRecyclerView(view_publish_list)
        }

        button_add_publisher.setOnClickListener {
            SelectPublisherActivity.start(context!!)
        }
    }

    override fun onListItemLongClick(item: BasePublishModel?) {
        activity?.let {
            item?.let {model ->
                val builder = AlertDialog.Builder(it)
                builder.setTitle(R.string.export)
                    .setItems(resources.getStringArray(R.array.ExportOptions))
                        { dialog, which ->
                            convertPublishersToJsonUseCase.execute(mapModelsToPublishers(listOf(model)))
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ result ->
                                    when (resources.getStringArray(R.array.ExportOptions)[which]) {
                                        getString(R.string.share_text) -> shareJSONtext(result)
                                        getString(R.string.share_file) -> shareJSONfile(result)
                                        getString(R.string.export_file) -> {
                                            jsonData = result
                                            exportJSONfile()
                                        }
                                    }
                                }, {

                                })
                        }
                builder.create()
                builder.show()
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun mapModelsToPublishers(models: List<BasePublishModel>): List<BasePublish> {
        return models.map {
            when (it) {
                is GooglePublishModel -> googlePublishModelDataMapper.transform(it)
                is RestPublishModel -> restPublishModelDataMapper.transform(it)
                is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(it)
                else -> throw IllegalArgumentException("Invalid publish model.")
            }
        }.toList()
    }

    fun mapPublishersToModels(publishers: List<BasePublish>): List<BasePublishModel> {
        return publishers.map {
            when (it.type) {
                PublishType.GOOGLE -> googlePublishDataMapper.transform(it as GooglePublish)
                PublishType.REST -> restPublishDataMapper.transform(it as RestPublish)
                PublishType.MQTT -> mqttPublishModelDataMapper.toMqttPublishModel(it as MqttPublish)
                else -> throw IllegalArgumentException("Invalid publish model.")
            }
        }.toList()
    }

    fun shareJSONtext(data: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "application/json"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.export)))
    }

    @SuppressLint("CheckResult")
    fun shareJSONfile(data: String) {
        storeTempTextUseCase.execute(data)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uriAndFile ->
                tempSharedFile = uriAndFile.second
                val sendIntent: Intent = Intent().apply {
                    type = "text/*"
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_STREAM, Uri.parse(uriAndFile.first))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.file_share_subject))
                }
                    startActivityForResult(
                    Intent.createChooser(sendIntent, getString(R.string.share_file)), CODE_SHARE
                ) }, {
                    Snackbar.make(container_fragment,
                        getString(R.string.sharing_failed),
                        Snackbar.LENGTH_SHORT).show()
            })
    }

    fun exportJSONfile() {
        val exportIntent: Intent = Intent().apply {
            type = "text/*"
            action = Intent.ACTION_CREATE_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, "backend.json")
        }
        startActivityForResult(exportIntent, CODE_EXPORT)
    }

    fun importJSONfile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, CODE_IMPORT)
    }

    fun fillFile(uri: Uri?) {
        jsonData?.let {
            uri?.toString()?.let { uriString ->
                storeTextUseCase.execute(uriString, it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        Snackbar.make(container_fragment,
                            getString(R.string.file_saved),
                            Snackbar.LENGTH_SHORT).show()
                    }.doOnError {
                        Snackbar.make(container_fragment,
                            getString(R.string.file_not_saved),
                            Snackbar.LENGTH_SHORT).show()
                    }.subscribe()
            }
        }
    }

    fun readFile(uri: Uri?) {
        uri?.toString()?.let { uriString ->
            readTextUseCase.execute(uriString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    convertJsonToPublishersUseCase.execute(result)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ publishers ->
                            addModels(publishers)
                        }, {
                            Snackbar.make(container_fragment,
                                getString(R.string.parsing_json_error),
                                Snackbar.LENGTH_SHORT).show()
                        })
                }, {
                    Snackbar.make(container_fragment,
                        getString(R.string.file_not_loaded),
                        Snackbar.LENGTH_SHORT).show()
                })

        }
    }

    /**
     * Called by PublihListActivity when an item in the actionbar menu is selected
     */
    fun resolveActionBarEvent(item: MenuItem?) {
        item?.let {
            convertPublishersToJsonUseCase.execute(mapModelsToPublishers(listBasePublish))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (it.itemId) {
                        R.id.action_import_file -> importJSONfile()
                        R.id.action_share_all -> shareJSONfile(result)
                        R.id.action_export_all -> {
                            jsonData = result
                            exportJSONfile()
                        }
                    }
                }, {

                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_SHARE -> tempSharedFile.delete()
            CODE_EXPORT -> fillFile(if (resultCode == Activity.RESULT_OK) data?.data else null)
            CODE_IMPORT -> readFile(data?.data)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentClickListener")
        }
    }

    override fun onResume() {
        super.onResume()
        val subscribe = publishListViewModel.getAllPublish()
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initPublishList(actions) }

        addDisposable(subscribe)
    }

    override fun onPause() {
        publishAdapter.setOnCheckedChangeListener(null)
        listBasePublish.clear()
        publishAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(actions: List<BasePublishModel>) {
        empty_view.visibility = View.GONE
        listBasePublish.addAll(actions)
        publishAdapter.notifyDataSetChanged()
        publishAdapter.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun deleteSelectedItem() {
        selectedItem?.let {
            when (selectedItem) {
                is GooglePublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as GooglePublishModel)
                    )
                }
                is RestPublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as RestPublishModel)
                    )
                }
                is MqttPublishModel -> {
                    addDisposable(
                        publishListViewModel.delete(selectedItem as MqttPublishModel)
                    )
                }
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }

            val index = listBasePublish.indexOf(selectedItem!!)
            listBasePublish.remove(selectedItem!!)
            publishAdapter.notifyItemRemoved(index)

            if (listBasePublish.isEmpty()) {
                empty_view.visibility = View.VISIBLE
            }
            //Let GC collect removed instance
            selectedItem = null
        }
    }

    fun addModels(coll: List<BasePublish>?) {
        coll?.let { list ->
            val offset = listBasePublish.size
            if(offset == 0) {
                initPublishList(mapPublishersToModels(list))
            } else {
                mapPublishersToModels(list).forEachIndexed { index, model ->
                    publishAdapter.addPublishModelAtPosition(model, offset + index)
                }
            }
            list.forEach { basePublish -> publishListViewModel.add(basePublish) }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublishListFragment()
    }

    /**
     * This interface must be implemented by fragments that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnListFragmentClickListener {
        fun onListFragmentClick(item: BasePublishModel?)
    }
}

