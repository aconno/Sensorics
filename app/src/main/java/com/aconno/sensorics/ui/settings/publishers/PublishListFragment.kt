package com.aconno.sensorics.ui.settings.publishers

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.SelectableRecyclerViewAdapter
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToObjectsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToPublishersUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.model.*
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.ui.ShareableItemsListFragment
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.settings.publishers.selectpublish.SelectPublisherActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_publish_list.*
import timber.log.Timber
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishListFragment.OnListFragmentClickListener] interface.
 */
class PublishListFragment : ShareableItemsListFragment<BasePublish>(),
    SelectableRecyclerViewAdapter.ItemLongClickListener<BasePublishModel>,
    SelectableRecyclerViewAdapter.ItemClickListener<BasePublishModel>,
    SelectableRecyclerViewAdapter.ItemSelectedListener<BasePublishModel> {

    private var snackbar: Snackbar? = null

    @Inject
    lateinit var publishListViewModel: PublishListViewModel

    @Inject
    lateinit var convertPublishersToJsonUseCase: ConvertObjectsToJsonUseCase<BasePublish>

    @Inject
    lateinit var convertJsonToPublishersUseCase: ConvertJsonToPublishersUseCase

    @Inject
    lateinit var restPublishModelDataMapper: RESTPublishModelDataMapper

    @Inject
    lateinit var mqttPublishModelDataMapper: MqttPublishModelDataMapper

    @Inject
    lateinit var azureMqttPublishModelDataMapper: AzureMqttPublishModelDataMapper

    @Inject
    lateinit var googlePublishModelDataMapper: GooglePublishModelDataMapper

    @Inject
    lateinit var restPublishDataMapper: RESTPublishDataMapper

    @Inject
    lateinit var googlePublishDataMapper: GooglePublishDataMapper

    private lateinit var publishAdapter: PublishRecyclerViewAdapter

    private var listener: OnListFragmentClickListener? = null
    private var selectedItem: BasePublishModel? = null
    private var selectionStateListener: ItemSelectionStateListener? = null
    private var savedInstanceStateSelectedItems: Array<SelectedItem>? = null

    override val exportedFileName: String = "backend.json"


    private val checkedChangeListener: PublishRecyclerViewAdapter.OnCheckedChangeListener = object :
        PublishRecyclerViewAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(checked: Boolean, position: Int) {
            val item = publishAdapter.getItem(position)
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
            mutableListOf(),
            this,
            this,
            this
        )
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ITEM_SELECTION_ENABLED_KEY)) {
                enableItemSelection()
                savedInstanceStateSelectedItems =
                    savedInstanceState.getParcelableArray(SELECTED_ITEMS_KEY)?.map { it as SelectedItem }?.toTypedArray()
            }

        }

        view_publish_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = publishAdapter

            itemAnimator = DefaultItemAnimator()
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }

        context?.let { context ->
            ItemTouchHelper(object : SwipeToDeleteCallback(context) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    snackbar?.dismiss()

                    val position = viewHolder.adapterPosition

                    val publishModel = publishAdapter.getItem(position)

                    publishAdapter.removeItemAtPosition(position)

                    snackbar = Snackbar.make(
                        container_fragment,
                        "${publishModel.name} removed!",
                        Snackbar.LENGTH_LONG
                    ).apply {
                        setAction(getString(R.string.undo)) {
                            publishAdapter.addItemAtPosition(publishModel,position)
                        }

                        addCallback(object : Snackbar.Callback() {
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

                        setActionTextColor(Color.YELLOW)

                        show()
                    }
                }
            }).attachToRecyclerView(view_publish_list)
        }

        button_add_publisher.setOnClickListener {
            snackbar?.dismiss()
            exitItemSelectionState()
            SelectPublisherActivity.start(context!!)
        }
    }

    override val sharedFileNamePrefix: String = "backend"

    override fun getConvertFromJsonUseCase(): ConvertJsonToObjectsUseCase<BasePublish> {
        return convertJsonToPublishersUseCase
    }

    override fun getConvertToJsonUseCase(): ConvertObjectsToJsonUseCase<BasePublish> {
        return convertPublishersToJsonUseCase
    }

    override fun getFileShareSubject(): String {
        return getString(R.string.backends_file_share_subject)
    }

    override fun getItems(): List<BasePublish> {
        return mapModelsToPublishers(publishAdapter.getItems())
    }

    override fun onItemsImportedFromFile(items: List<BasePublish>) {
        addModels(items)
    }

    override fun onItemLongClick(item: BasePublishModel) {
        if (!publishAdapter.isItemSelectionEnabled) {
            enableItemSelection(item)
        }
    }

    override fun onItemClick(item: BasePublishModel) {
        listener?.onListFragmentClick(item)
    }

    private fun enableItemSelection(initiallySelectedItem: BasePublishModel? = null) {
        publishAdapter.enableItemSelection(initiallySelectedItem)
        selectionStateListener?.onSelectedItemsCountChanged(publishAdapter.getNumberOfSelectedItems())
        selectionStateListener?.onItemSelectionStateEntered()
    }

    override fun onListItemSelectionStateChanged(item: BasePublishModel, state: Boolean) {
        selectionStateListener?.onSelectedItemsCountChanged(publishAdapter.getNumberOfSelectedItems())
    }

    override fun resolveActionBarEvent(item: MenuItem?) {
        when (item?.itemId) {
            android.R.id.home -> exitItemSelectionState()
            R.id.action_select_all -> selectAllItems()
            R.id.remove_selected -> showRemoveSelectedPublishersDialog()
            else -> super.resolveActionBarEvent(item)
        }
    }

    private fun showRemoveSelectedPublishersDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.remove_publishers_title))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                removeSelectedPublishers()
                exitItemSelectionState()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(true)
            .setMessage(getString(R.string.remove_publishers_confirmation))
            .show()
    }

    private fun removeSelectedPublishers() {
        val publishers = publishAdapter.getSelectedItems()
        Completable.merge(publishers.map { publishListViewModel.delete(it) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                reloadPublishers()
            }
            .also { addDisposable(it) }

        val snackbarMessage =
            if(publishers.size == 1) {
                getString(R.string.one_publisher_removed,publishers[0].name)
            } else {
                getString(R.string.multiple_publishers_removed,publishers.size)
            }
        Snackbar.make(container_fragment,snackbarMessage,Snackbar.LENGTH_SHORT).show()
    }


    private fun selectAllItems() {
        publishAdapter.setItemsAsSelected(publishAdapter.getItems())
    }

    override fun getItemsForExport(): List<BasePublish> {
        if (publishAdapter.isItemSelectionEnabled) {
            return publishAdapter.getSelectedItems().map { mapModelToPublisher(it) }
        }
        return getItems()
    }


    private fun mapModelsToPublishers(models: List<BasePublishModel>): List<BasePublish> {
        return models.map {
            mapModelToPublisher(it)
        }.toList()
    }

    private fun mapModelToPublisher(model: BasePublishModel): BasePublish {
        return when (model) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(model)
            is RestPublishModel -> restPublishModelDataMapper.transform(model)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(model)
            is AzureMqttPublishModel -> azureMqttPublishModelDataMapper.toAzureMqttPublish(model)
            else -> throw IllegalArgumentException("Invalid publish model.")
        }
    }

    private fun mapPublishersToModels(publishers: List<BasePublish>): List<BasePublishModel> {
        return publishers.map {
            when (it.type) {
                PublishType.GOOGLE -> googlePublishDataMapper.transform(it as GooglePublish)
                PublishType.REST -> restPublishDataMapper.transform(it as RestPublish)
                PublishType.MQTT -> mqttPublishModelDataMapper.toMqttPublishModel(it as MqttPublish)
                PublishType.AZURE_MQTT -> azureMqttPublishModelDataMapper.toAzureMqttPublishModel(it as AzureMqttPublish)
                else -> throw IllegalArgumentException("Invalid publish model.")
            }
        }.toList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentClickListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentClickListener")
        }
        if (context is ItemSelectionStateListener) {
            selectionStateListener = context
        }
    }

    override fun onResume() {
        super.onResume()

        reloadPublishers()
    }

    private fun reloadPublishers() {
        publishAdapter.clear()
        publishListViewModel.getAllPublish()
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ actions ->
                initPublishList(actions)
            }, {
                Timber.e(it)
            }).also {
                addDisposable(it)
            }
    }

    override fun onStop() {
        super.onStop()
        publishAdapter.clear()
    }

    override fun onPause() {
        snackbar?.dismiss()
        publishAdapter.setOnCheckedChangeListener(null)
        publishAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initPublishList(publishers: List<BasePublishModel>) {
        empty_view.visibility = View.GONE
        publishAdapter.addItems(publishers)
        if (savedInstanceStateSelectedItems != null && publishAdapter.isItemSelectionEnabled) {
            publishAdapter.setItemsAsSelected(
                publishAdapter.getItems()
                    .filter {
                        savedInstanceStateSelectedItems!!.find { item -> item.id == it.id && item.type == it.type } != null }
            )
        }

        publishAdapter.notifyDataSetChanged()
        publishAdapter.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun deleteSelectedItem() {
        selectedItem?.let { model ->
            when (model) {
                is GooglePublishModel -> publishListViewModel.delete(model)
                is RestPublishModel -> publishListViewModel.delete(model)
                is MqttPublishModel -> publishListViewModel.delete(model)
                is AzureMqttPublishModel -> publishListViewModel.delete(model)
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }
                .subscribe()
                .also { addDisposable(it) }

            publishAdapter.removeItem(model)

            if (publishAdapter.itemCount == 0) {
                // Because activity may quit before the snackbar is finished
                empty_view?.visibility = View.VISIBLE
            }

            //Let GC collect removed instance
            selectedItem = null
        }
    }

    private fun addModels(coll: List<BasePublish>?) {
        coll?.let {
            val offset = publishAdapter.itemCount

            Flowable.fromIterable(it)
                .flatMapMaybe { publish ->
                    publishListViewModel.add(publish)
                        .subscribeOn(Schedulers.io())
                        .flatMapMaybe { id ->
                            when (publish) {
                                is GooglePublish -> {
                                    publishListViewModel.getGooglePublishModelById(id)
                                }
                                is RestPublish -> {
                                    publishListViewModel.getRestPublishModelById(id)
                                }
                                is MqttPublish -> {
                                    publishListViewModel.getMqttPublishModelById(id)
                                }
                                is AzureMqttPublish -> {
                                    publishListViewModel.getAzureMqttPublishModelById(id)
                                }
                                else -> Maybe.error(IllegalArgumentException("Invalid Publish"))
                            }.subscribeOn(Schedulers.io())
                        }
                }.observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe({ list ->
                    if (offset == 0) {
                        initPublishList(list)
                    } else {
                        list.forEachIndexed { index, model ->
                            publishAdapter.addItemAtPosition(model,offset + index)
                        }
                    }

                    Snackbar.make(container_fragment,
                        when (list.size) {
                            1 -> getString(R.string.import_one_backend_success)
                            else -> getString(R.string.import_multiple_backends_success, list.size)
                        },
                        Snackbar.LENGTH_SHORT).show()

                }, {
                    Snackbar.make(
                        container_fragment,
                        getString(R.string.import_error),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }).also {
                    addDisposable(it)
                }
        }
    }

    fun onBackButtonPressed(): Boolean { //returns true if it has handled the back button press
        if (publishAdapter.isItemSelectionEnabled) {
            exitItemSelectionState()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        exitItemSelectionState()
    }

    private fun exitItemSelectionState() {
        if (publishAdapter.isItemSelectionEnabled) {
            publishAdapter.disableItemSelection()
            selectionStateListener?.onItemSelectionStateExited()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(ITEM_SELECTION_ENABLED_KEY, publishAdapter.isItemSelectionEnabled)
        if (publishAdapter.isItemSelectionEnabled) {
            outState.putParcelableArray(
                SELECTED_ITEMS_KEY,
                publishAdapter.getSelectedItems().map { SelectedItem(it.id,it.type) }.toTypedArray()
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = PublishListFragment()

        private const val ITEM_SELECTION_ENABLED_KEY = "ITEM_SELECTION_ENABLED_KEY"
        private const val SELECTED_ITEMS_KEY = "SELECTED_ITEMS_KEY"
    }

    class SelectedItem(val id : Long, val type : PublishType) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readSerializable() as PublishType
        )

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeLong(id)
            dest?.writeSerializable(type)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SelectedItem> {
            override fun createFromParcel(parcel: Parcel): SelectedItem {
                return SelectedItem(parcel)
            }

            override fun newArray(size: Int): Array<SelectedItem?> {
                return arrayOfNulls(size)
            }
        }

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

    interface ItemSelectionStateListener {
        fun onItemSelectionStateEntered()
        fun onItemSelectionStateExited()
        fun onSelectedItemsCountChanged(selectedItems: Int)
    }
}

