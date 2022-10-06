package com.aconno.sensorics.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.action
import com.aconno.sensorics.adapter.ActionAdapter
import com.aconno.sensorics.adapter.ItemClickListener
import com.aconno.sensorics.adapter.SelectableRecyclerViewAdapter
import com.aconno.sensorics.databinding.FragmentActionListBinding
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.DeleteActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetAllActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToActionsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToObjectsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.ui.actions.ActionDetailsActivity
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author aconno
 */


class ActionListFragment : ShareableItemsListFragment<Action>(), ItemClickListener<Action>,
    SelectableRecyclerViewAdapter.ItemSelectedListener<Action>,
    SelectableRecyclerViewAdapter.ItemLongClickListener<Action>,
    SelectableRecyclerViewAdapter.ItemClickListener<Action> {

    private lateinit var actionAdapter: ActionAdapter
    private var snackbar: Snackbar? = null
    override val sharedFileNamePrefix = "actions"
    override val exportedFileName: String = "actions.json"

    private var binding: FragmentActionListBinding? = null

    private var selectionStateListener: ItemSelectionStateListener? = null

    @Inject
    lateinit var getAllActionsUseCase: GetAllActionsUseCase

    @Inject
    lateinit var deleteActionUseCase: DeleteActionUseCase

    @Inject
    lateinit var saveActionUseCase: AddActionUseCase

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var addActionUseCase: AddActionUseCase

    @Inject
    lateinit var convertActionsToJsonUseCase: ConvertObjectsToJsonUseCase<Action>

    @Inject
    lateinit var convertJsonToActionsUseCase: ConvertJsonToActionsUseCase

    private var savedInstanceStateSelectedItems: LongArray? = null

    private val checkedChangeListener: ActionAdapter.OnCheckedChangeListener = object :
        ActionAdapter.OnCheckedChangeListener {
        override fun onCheckedChange(action: Action, checked: Boolean) {
            action.active = checked
            saveActionUseCase.execute(action).subscribeOn(Schedulers.io()).subscribe().also {
                disposables.add(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActionListBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemSelectionStateListener) {
            selectionStateListener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionAdapter = ActionAdapter(mutableListOf(), this, this, this)
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(ITEM_SELECTION_ENABLED_KEY)) {
                enableItemSelection()
                savedInstanceStateSelectedItems =
                    savedInstanceState.getLongArray(SELECTED_ITEMS_KEY)
            }

        }


        binding?.actionList?.apply {
            adapter = actionAdapter
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        context?.let { context ->
            val swipeToDeleteCallback = object : SwipeToDeleteCallback(context) {

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.bindingAdapterPosition
                    val action = actionAdapter.getItem(position)
                    actionAdapter.removeItemAtPosition(position)

                    snackbar = binding?.containerFragment?.let {
                        Snackbar.make(
                            it,
                            String.format(
                                getString(R.string.action_removed),
                                action.name
                            ), //"${action.name} removed!",
                            Snackbar.LENGTH_LONG
                        ).apply {
                            this.action(R.string.undo) {
                                actionAdapter.addItemAtPosition(action, position)
                            }
                            this.addCallback(object : Snackbar.Callback() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    if (event == DISMISS_EVENT_TIMEOUT
                                        || event == DISMISS_EVENT_CONSECUTIVE
                                        || event == DISMISS_EVENT_SWIPE
                                        || event == DISMISS_EVENT_MANUAL
                                    ) {
                                        deleteActionUseCase.execute(action)
                                            .subscribeOn(Schedulers.io())
                                            .subscribe()
                                    }
                                }
                            })
                            this.setActionTextColor(Color.YELLOW)
                            this.show()
                        }
                    }
                }
            }
            ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(binding?.actionList)
        }

        binding?.addActionButton?.setOnClickListener {
            snackbar?.dismiss()
            exitItemSelectionState()
            startAddActionActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }

    override fun onItemLongClick(item: Action) {
        if (!actionAdapter.isItemSelectionEnabled) {
            enableItemSelection(item)
        }
    }

    private fun enableItemSelection(initiallySelectedItem: Action? = null) {
        actionAdapter.enableItemSelection(initiallySelectedItem)
        selectionStateListener?.onSelectedItemsCountChanged(actionAdapter.getNumberOfSelectedItems())
        selectionStateListener?.onItemSelectionStateEntered()
    }

    override fun onListItemSelectionStateChanged(item: Action, state: Boolean) {
        selectionStateListener?.onSelectedItemsCountChanged(actionAdapter.getNumberOfSelectedItems())
    }

    override fun resolveActionBarEvent(item: MenuItem?) {
        when (item?.itemId) {
            android.R.id.home -> exitItemSelectionState()
            R.id.action_select_all -> selectAllItems()
            R.id.remove_selected -> showRemoveSelectedActionsDialog()
            else -> super.resolveActionBarEvent(item)
        }
    }

    private fun showRemoveSelectedActionsDialog() {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.remove_actions_title))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                removeSelectedActions()
                exitItemSelectionState()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setCancelable(true)
            .setMessage(getString(R.string.remove_actions_confirmation))
            .show()
    }

    private fun removeSelectedActions() {
        val actions = actionAdapter.getSelectedItems()
        Completable.merge(actions.map { deleteActionUseCase.execute(it) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                reloadActions()
            }
            .also { disposables.add(it) }

        val snackbarMessage =
            if (actions.size == 1) {
                getString(R.string.one_action_removed, actions[0].name)
            } else {
                getString(R.string.multiple_actions_removed, actions.size)
            }
        binding?.containerFragment?.let {
            Snackbar.make(it, snackbarMessage, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        exitItemSelectionState()

    }

    private fun selectAllItems() {
        actionAdapter.setItemsAsSelected(actionAdapter.getItems())
    }

    override fun getFileShareSubject(): String {
        return getString(R.string.actions_file_share_subject)
    }

    override fun getConvertFromJsonUseCase(): ConvertJsonToObjectsUseCase<Action> {
        return convertJsonToActionsUseCase
    }

    override fun getConvertToJsonUseCase(): ConvertObjectsToJsonUseCase<Action> {
        return convertActionsToJsonUseCase
    }

    override fun getItems(): List<Action> {
        return actionAdapter.getItems()
    }

    override fun getItemsForExport(): List<Action> {
        if (actionAdapter.isItemSelectionEnabled) {
            return actionAdapter.getSelectedItems()
        }
        return getItems()
    }

    override fun onItemsImportedFromFile(items: List<Action>) {
        addActions(items)
    }

    private fun addActions(actions: List<Action>) {
        var imported = 0
        var failed = 0
        val actionsToAddToAdapter: MutableList<Action> = mutableListOf()

        if (actions.isEmpty()) { //in case of user tries to import an empty file
            binding?.containerFragment?.let {
                Snackbar.make(
                    it,
                    getString(R.string.empty_import_file),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        val updateAdapterLazily =
            { //updates the adapter only when all actions have been (successfully or unsuccessfully) added to db
                if (actions.size == failed + imported) {
                    addActionsToActionAdapter(actionsToAddToAdapter)

                    binding?.let {
                        Snackbar.make(
                            it.containerFragment,
                            when (imported) {
                                0 -> getString(R.string.import_error)
                                1 -> if (failed == 0) getString(R.string.import_one_action_success)
                                else getString(
                                    R.string.action_import_partial_success,
                                    imported,
                                    actions.size
                                )
                                else -> if (failed == 0) getString(
                                    R.string.import_multiple_actions_success,
                                    imported
                                )
                                else getString(
                                    R.string.action_import_partial_success,
                                    imported,
                                    actions.size
                                )
                            },
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        actions.forEach { action ->
            addActionUseCase.execute(action)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    action.id = it
                    action
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    actionsToAddToAdapter.add(it)
                    imported++
                    updateAdapterLazily()
                }, {
                    failed++
                    updateAdapterLazily()
                })
        }

    }


    private fun addActionsToActionAdapter(actions: List<Action>) {
        if (actionAdapter.getItems().isEmpty()) {
            initActionList(actions, savedInstanceStateSelectedItems)
        } else {
            actionAdapter.addItems(actions)
        }
    }

    private fun startAddActionActivity() {
        context?.let { ActionDetailsActivity.start(it) }
    }

    override fun onResume() {
        super.onResume()

        reloadActions()
    }

    private fun reloadActions() {
        addDisposable(
            getAllActionsUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { actions ->
                    initActionList(
                        actions, savedInstanceStateSelectedItems
                    )
                }
        )
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
        actionAdapter.checkedChangeListener = null
    }

    private fun initActionList(actions: List<Action>, selectedItems: LongArray?) {
        actionAdapter.setItems(actions)
        if (selectedItems != null && actionAdapter.isItemSelectionEnabled) {
            actionAdapter.setItemsAsSelected(
                actionAdapter.getItems().filter { it.id in selectedItems })
        }

        if (actions.isEmpty()) {
            binding?.actionListEmptyView?.visibility = View.VISIBLE
        } else {
            binding?.actionListEmptyView?.visibility = View.INVISIBLE
        }

        actionAdapter.checkedChangeListener = checkedChangeListener
    }

    override fun onItemClick(item: Action) {
        snackbar?.dismiss()
        context?.let { ActionDetailsActivity.start(it, item.id) }
    }


    fun onBackButtonPressed(): Boolean { //returns true if it has handled the back button press
        if (actionAdapter.isItemSelectionEnabled) {
            exitItemSelectionState()
            return true
        }
        return false
    }

    private fun exitItemSelectionState() {
        if (actionAdapter.isItemSelectionEnabled) {
            actionAdapter.disableItemSelection()
            selectionStateListener?.onItemSelectionStateExited()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(ITEM_SELECTION_ENABLED_KEY, actionAdapter.isItemSelectionEnabled)
        if (actionAdapter.isItemSelectionEnabled) {
            outState.putLongArray(
                SELECTED_ITEMS_KEY,
                actionAdapter.getSelectedItems().map { it.id }.toLongArray()
            )
        }
    }

    interface ItemSelectionStateListener {
        fun onItemSelectionStateEntered()
        fun onItemSelectionStateExited()
        fun onSelectedItemsCountChanged(selectedItems: Int)
    }

    companion object {
        fun newInstance(): ActionListFragment {
            return ActionListFragment()
        }

        private const val ITEM_SELECTION_ENABLED_KEY = "ITEM_SELECTION_ENABLED_KEY"
        private const val SELECTED_ITEMS_KEY = "SELECTED_ITEMS_KEY"
    }
}
