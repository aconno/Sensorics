package com.aconno.sensorics.ui.settings.publishers.resthttpgetparams

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.adapter.LongItemClickListener
import com.aconno.sensorics.model.RestHttpGetParamModel
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.aconno.sensorics.ui.settings.publishers.restheader.ItemClickListenerWithPos
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_rest_httpgetparams.*
import kotlinx.android.synthetic.main.activity_rest_httpgetparams.empty_view
import kotlinx.android.synthetic.main.activity_rest_httpgetparams.recyclerView
import kotlinx.android.synthetic.main.activity_rest_httpgetparams.toolbar


class RestHttpGetParamsActivity : AppCompatActivity(),
    AddRestHttpGetParamDialog.OnFragmentInteractionListener,
    LongItemClickListener<RestHttpGetParamModel> {

    private lateinit var initialHttpGetParams: ArrayList<RestHttpGetParamModel>
    private lateinit var httpgetParams: ArrayList<RestHttpGetParamModel>
    private lateinit var rvAdapter: RestHttpGetParamsAdapter
    private var onItemClickListener: ItemClickListenerWithPos<RestHttpGetParamModel>
    private var selectedItem: RestHttpGetParamModel? = null
    private var snackbar: Snackbar? = null

    private var deleteDialogClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    deleteSelectedItem()
                    dialog.dismiss()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

    private var unsavedChangesDialogClickListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    finishActivityWithResult()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        }

    init {
        onItemClickListener = object :
            ItemClickListenerWithPos<RestHttpGetParamModel> {
            override fun onItemClick(position: Int, item: RestHttpGetParamModel?) {
                AddRestHttpGetParamDialog.newInstance(
                    item,
                    position
                )
                    .show(supportFragmentManager, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_httpgetparams)
        setupActionBar()

        val httpgetParamsExtra = intent.getParcelableArrayExtra(REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY)
        httpgetParams = ArrayList(httpgetParamsExtra?.toList()?.map { it as RestHttpGetParamModel } ?: emptyList())
        initialHttpGetParams = ArrayList(httpgetParams)

        initView()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.rest_http_get_params_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        if (httpgetParams.isNotEmpty()) {
            empty_view.visibility = View.GONE
        }

        rvAdapter = RestHttpGetParamsAdapter(
            httpgetParams,
            onItemClickListener, this
        )
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                (layoutManager as LinearLayoutManager).orientation
            )
            this.addItemDecoration(dividerItemDecoration)
        }

        button_addHttpGetParam.setOnClickListener {
            AddRestHttpGetParamDialog.newInstance(
                null,
                -1
            )
                .show(supportFragmentManager, null)
        }

        initSwipeToDelete()
    }

    private fun initSwipeToDelete() {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(this) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val parameter = rvAdapter.getParameterAt(position)
                rvAdapter.removeParameterAt(position)

                snackbar = Snackbar
                    .make(empty_view, "Parameter ${parameter.key} removed!", Snackbar.LENGTH_LONG)
                snackbar?.setAction("UNDO") {
                    rvAdapter.addParameterAtPosition(parameter, position)
                }

                snackbar?.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT
                            || event == DISMISS_EVENT_CONSECUTIVE
                            || event == DISMISS_EVENT_SWIPE
                            || event == DISMISS_EVENT_MANUAL
                        ) {
                            deleteItem(parameter)
                        }
                    }
                })
                snackbar?.setActionTextColor(Color.YELLOW)
                snackbar?.show()
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
    }

    override fun onBackPressed() {
        if(initialHttpGetParams != httpgetParams) {
            AlertDialog.Builder(this).setMessage(getString(R.string.unsaved_changes_dialog_message))
                .setPositiveButton(getString(R.string.save_changes), unsavedChangesDialogClickListener)
                .setNegativeButton(getString(R.string.discard_changes),unsavedChangesDialogClickListener)
                .show()

        } else {
            super.onBackPressed()
        }
    }

    override fun onLongClick(param: RestHttpGetParamModel) {
        selectedItem = param
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes), deleteDialogClickListener)
            .setNegativeButton(getString(R.string.no), deleteDialogClickListener)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.rest_headers_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_publish_done -> {
                finishActivityWithResult()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteItem(item : RestHttpGetParamModel) {
        val index = httpgetParams.indexOf(item)
        httpgetParams.remove(item)
        rvAdapter.notifyItemRemoved(index)

        if (httpgetParams.isEmpty()) {
            empty_view.visibility = View.VISIBLE
        }
    }

    private fun deleteSelectedItem() {
        selectedItem?.let {
            deleteItem(it)
            //Let GC collect removed instance
            selectedItem = null
        }
    }

    private fun finishActivityWithResult() {
        val data = Intent()
        data.putParcelableArrayListExtra(REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY, httpgetParams)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onFragmentInteraction(position: Int, key: String, value: String) {
        empty_view.visibility = View.GONE

        if (position == -1) {
            httpgetParams.add(
                RestHttpGetParamModel(
                    0L, 0L, key, value
                )
            )
            rvAdapter.notifyItemInserted(rvAdapter.itemCount)
        } else {
            httpgetParams[position] =
                    RestHttpGetParamModel(
                        httpgetParams[position].id,
                        httpgetParams[position].rId,
                        key,
                        value
                    )
            rvAdapter.notifyItemChanged(position)
        }
    }

    companion object {
        const val EDIT_HTTPGET_PARAMS_REQUEST_CODE: Int = 10218
        const val REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY = "REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY"

        fun start(activity: AppCompatActivity, httpGetParams: MutableList<RestHttpGetParamModel>) {
            val intent = Intent(activity, RestHttpGetParamsActivity::class.java)
            intent.putExtra(
                REST_HTTPGET_PARAMS_ACTIVITY_LIST_KEY,
                httpGetParams.toTypedArray()
            )

            activity.startActivityForResult(
                intent,
                EDIT_HTTPGET_PARAMS_REQUEST_CODE
            )
        }
    }

}