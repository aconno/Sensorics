package com.aconno.sensorics.ui.settings.virtualscanningsources

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.aconno.sensorics.R
import com.aconno.sensorics.model.BaseVirtualScanningSourceModel
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import com.aconno.sensorics.model.mapper.MqttVirtualScanningSourceModelDataMapper
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.base.BaseFragment
import com.aconno.sensorics.viewmodel.VirtualScanningSourceListViewModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_publish_list.*
import kotlinx.android.synthetic.main.fragment_virtual_scanning_sources.*
import javax.inject.Inject

class VirtualScanningSourceListFragment : BaseFragment() {
    @Inject
    lateinit var sourcesListViewModel: VirtualScanningSourceListViewModel

    private lateinit var sourcesAdapter: VirtualScanningSourcesAdapter

    @Inject
    lateinit var mqttSourceModelDataMapper: MqttVirtualScanningSourceModelDataMapper

    private var listener: OnListFragmentClickListener? = null

    private var snackbar: Snackbar? = null

    private var sourcesList: MutableList<BaseVirtualScanningSourceModel> = mutableListOf()

    private var selectedItem: BaseVirtualScanningSourceModel? = null

    private val checkedChangeListener: VirtualScanningSourcesAdapter.OnCheckedChangeListener = object :
            VirtualScanningSourcesAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(checked: Boolean, position: Int) {
            val item = sourcesList[position]
            item.enabled = checked
            addDisposable(sourcesListViewModel.update(item))
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_virtual_scanning_sources, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sourcesAdapter = VirtualScanningSourcesAdapter(sourcesList,
                object : VirtualScanningSourcesAdapter.OnListItemClickListener {
                    override fun onListItemClick(item: BaseVirtualScanningSourceModel?) {
                        listener?.onListFragmentClick(item)
                    }
                })

        virtual_scanning_sources_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sourcesAdapter

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

                    val sourceModel = sourcesAdapter.getSourceModel(position)

                    sourcesAdapter.removeSourceModel(position)

                    snackbar = Snackbar.make(
                            virtual_scanning_sources_fragment_container,
                            "${sourceModel.name} removed!",
                            Snackbar.LENGTH_LONG
                    ).apply {
                        setAction(getString(R.string.undo)) {
                            sourcesAdapter.addSourceModelAtPosition(sourceModel,position)
                        }

                        addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                if (event == DISMISS_EVENT_TIMEOUT
                                        || event == DISMISS_EVENT_CONSECUTIVE
                                        || event == DISMISS_EVENT_SWIPE
                                        || event == DISMISS_EVENT_MANUAL
                                ) {
                                    selectedItem = sourceModel
                                    deleteSelectedItem()
                                }
                            }
                        })

                        setActionTextColor(Color.YELLOW)

                        show()
                    }
                }
            }).attachToRecyclerView(virtual_scanning_sources_list)
        }

        button_add_virtual_scanning_source.setOnClickListener {
            snackbar?.dismiss()
            MqttVirtualScanningSourceActivity.start(context!!) //mqtt activity is directly started since mqtt is the only supported source for now, so there is no need to have select-source-type-activity
        }
    }

    private fun deleteSelectedItem() {
        selectedItem?.let { model ->
            when (model) {
                is MqttVirtualScanningSourceModel -> sourcesListViewModel.deleteMqttVirtulScanningSource(model)
                else -> throw IllegalArgumentException("Illegal argument provided.")
            }.also {
                addDisposable(it)
            }

            sourcesList.remove(model)

            if (sourcesList.isEmpty()) {
                no_virtual_scanning_sources?.visibility = View.VISIBLE
            }

            selectedItem = null
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
        sourcesListViewModel.getAllVirtualScanningSources()
                .filter { it.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { sources -> initVirtualScanningSourceList(sources) }
                .also {
                    addDisposable(it)
                }
    }

    override fun onPause() {
        snackbar?.dismiss()
        sourcesAdapter.setOnCheckedChangeListener(null)
        sourcesList.clear()
        sourcesAdapter.notifyDataSetChanged()
        super.onPause()
    }

    private fun initVirtualScanningSourceList(sources: List<BaseVirtualScanningSourceModel>) {
        no_virtual_scanning_sources.visibility = View.GONE
        sourcesList.addAll(sources)
        sourcesAdapter.notifyDataSetChanged()
        sourcesAdapter.setOnCheckedChangeListener(checkedChangeListener)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = VirtualScanningSourceListFragment()
    }

    interface OnListFragmentClickListener {
        fun onListFragmentClick(item: BaseVirtualScanningSourceModel?)
    }
}