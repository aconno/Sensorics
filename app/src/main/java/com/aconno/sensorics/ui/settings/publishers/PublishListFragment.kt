package com.aconno.sensorics.ui.settings.publishers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToObjectsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToPublishersUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.model.BasePublishModel
import com.aconno.sensorics.model.GooglePublishModel
import com.aconno.sensorics.model.MqttPublishModel
import com.aconno.sensorics.model.RestPublishModel
import com.aconno.sensorics.model.mapper.*
import com.aconno.sensorics.ui.ShareableItemsListFragment
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.settings.publishers.selectpublish.SelectPublisherActivity
import com.aconno.sensorics.viewmodel.PublishListViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_publish_list.*
import javax.inject.Inject


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PublishListFragment.OnListFragmentClickListener] interface.
 */
class PublishListFragment : ShareableItemsListFragment<BasePublish>(), PublishRecyclerViewAdapter.OnListItemLongClickListener {

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
    lateinit var googlePublishModelDataMapper: GooglePublishModelDataMapper

    @Inject
    lateinit var restPublishDataMapper: RESTPublishDataMapper

    @Inject
    lateinit var googlePublishDataMapper: GooglePublishDataMapper

    private lateinit var publishAdapter: PublishRecyclerViewAdapter

    private var listener: OnListFragmentClickListener? = null
    private var listBasePublish: MutableList<BasePublishModel> = mutableListOf()
    private var selectedItem: BasePublishModel? = null

    override val exportedFileName: String = "backend.json"

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

                    val publishModel = publishAdapter.getPublishModel(position)

                    publishAdapter.removePublishModel(position)

                    snackbar = Snackbar.make(
                        container_fragment,
                        "${publishModel.name} removed!",
                        Snackbar.LENGTH_LONG
                    ).apply {
                        setAction(getString(R.string.undo)) {
                            publishAdapter.addPublishModelAtPosition(publishModel, position)
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
        return mapModelsToPublishers(listBasePublish)
    }

    override fun onItemsImportedFromFile(items: List<BasePublish>) {
        addModels(items)
    }

    override fun onListItemLongClick(item: BasePublishModel?) {
        item?.let {
            showExportOptionsDialog(mapModelToPublisher(it))
        }
    }

    private fun mapModelsToPublishers(models: List<BasePublishModel>): List<BasePublish> {
        return models.map {
            mapModelToPublisher(it)
        }.toList()
    }

    private fun mapModelToPublisher(model : BasePublishModel) : BasePublish {
        return when (model) {
            is GooglePublishModel -> googlePublishModelDataMapper.transform(model)
            is RestPublishModel -> restPublishModelDataMapper.transform(model)
            is MqttPublishModel -> mqttPublishModelDataMapper.toMqttPublish(model)
            else -> throw IllegalArgumentException("Invalid publish model.")
        }
    }

    private fun mapPublishersToModels(publishers: List<BasePublish>): List<BasePublishModel> {
        return publishers.map {
            when (it.type) {
                PublishType.GOOGLE -> googlePublishDataMapper.transform(it as GooglePublish)
                PublishType.REST -> restPublishDataMapper.transform(it as RestPublish)
                PublishType.MQTT -> mqttPublishModelDataMapper.toMqttPublishModel(it as MqttPublish)
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
    }

    override fun onResume() {
        super.onResume()
        publishListViewModel.getAllPublish()
            .filter { it.isNotEmpty() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { actions -> initPublishList(actions) }
            .also {
                addDisposable(it)
            }
    }

    override fun onPause() {
        snackbar?.dismiss()
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
        selectedItem?.let { model ->
            when (model) {
                is GooglePublishModel -> publishListViewModel.delete(model)
                is RestPublishModel -> publishListViewModel.delete(model)
                is MqttPublishModel -> publishListViewModel.delete(model)
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }.also {
                addDisposable(it)
            }

            listBasePublish.remove(model)

            if (listBasePublish.isEmpty()) {
                // Because activity may quit before the snackbar is finished
                empty_view?.visibility = View.VISIBLE
            }

            //Let GC collect removed instance
            selectedItem = null
        }
    }

    private fun addModels(coll: List<BasePublish>?) {
        coll?.let {
            val offset = listBasePublish.size

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
                                else -> Maybe.error(IllegalArgumentException("Invalid Publish"))
                            }.subscribeOn(Schedulers.io())
                        }
                }.observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe({list ->
                    if (offset == 0) {
                        initPublishList(list)
                    } else {
                        list.forEachIndexed { index, model ->
                            listBasePublish.add(offset + index, model)
                        }
                        publishAdapter.notifyItemRangeChanged(offset, list.size)
                    }
                }, {
                    Snackbar.make(container_fragment,
                        getString(R.string.import_error),
                        Snackbar.LENGTH_SHORT).show()
                }).also {
                    addDisposable(it)
                }
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

