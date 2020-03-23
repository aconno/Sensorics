package com.aconno.sensorics.ui.settings.publishers.restheader

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
import com.aconno.sensorics.model.RestHeaderModel
import com.aconno.sensorics.ui.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rest_headers.*
import kotlinx.android.synthetic.main.fragment_action_list.*


class RestHeadersActivity : AppCompatActivity(),
    AddRestHeaderDialog.OnFragmentInteractionListener,
    LongItemClickListener<RestHeaderModel> {

    private lateinit var initialHeaders: ArrayList<RestHeaderModel>
    private lateinit var headers: ArrayList<RestHeaderModel>
    private lateinit var rvAdapter: RestHeadersAdapter
    private var onItemClickListener: ItemClickListenerWithPos<RestHeaderModel>
    private var selectedItem: RestHeaderModel? = null
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
            ItemClickListenerWithPos<RestHeaderModel> {
            override fun onItemClick(position: Int, item: RestHeaderModel?) {
                AddRestHeaderDialog.newInstance(
                    item,
                    position
                )
                    .show(supportFragmentManager, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rest_headers)
        setupActionBar()

        headers = intent.getParcelableArrayListExtra(REST_HEADERS_ACTIVITY_LIST_KEY)
        initialHeaders = ArrayList(headers)

        initView()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.rest_headers_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        if (headers.isNotEmpty()) {
            empty_view.visibility = View.GONE
        }

        rvAdapter = RestHeadersAdapter(
            headers,
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

        button_addHeader.setOnClickListener {
            AddRestHeaderDialog.newInstance(
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
                val position = viewHolder.adapterPosition
                val header = rvAdapter.getHeaderAt(position)
                rvAdapter.removeHeaderAt(position)

                snackbar = Snackbar
                    .make(empty_view, "Header ${header.key} removed!", Snackbar.LENGTH_LONG)
                snackbar?.setAction("UNDO") {
                    rvAdapter.addHeaderAtPosition(header, position)
                }

                snackbar?.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT
                            || event == DISMISS_EVENT_CONSECUTIVE
                            || event == DISMISS_EVENT_SWIPE
                            || event == DISMISS_EVENT_MANUAL
                        ) {
                            deleteItem(header)
                        }
                    }
                })
                snackbar?.setActionTextColor(Color.YELLOW)
                snackbar?.show()
            }
        }
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(recyclerView)
    }


    override fun onLongClick(param: RestHeaderModel) {
        selectedItem = param
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes), deleteDialogClickListener)
            .setNegativeButton(getString(R.string.no), deleteDialogClickListener)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.rest_headers_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
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

    override fun onBackPressed() {
        if(initialHeaders != headers) {
            AlertDialog.Builder(this).setMessage(getString(R.string.unsaved_changes_dialog_message))
                .setPositiveButton(getString(R.string.save_changes), unsavedChangesDialogClickListener)
                .setNegativeButton(getString(R.string.discard_changes),unsavedChangesDialogClickListener)
                .show()

        } else {
            super.onBackPressed()
        }
    }

    private fun deleteItem(item : RestHeaderModel) {
        val index = headers.indexOf(item)
        headers.remove(item)
        rvAdapter.notifyItemRemoved(index)

        if (headers.isEmpty()) {
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
        data.putParcelableArrayListExtra(REST_HEADERS_ACTIVITY_LIST_KEY, headers)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onFragmentInteraction(position: Int, key: String, value: String) {
        empty_view.visibility = View.GONE

        if (position == -1) {
            headers.add(
                RestHeaderModel(
                    0L, 0L, key, value
                )
            )
            rvAdapter.notifyItemInserted(rvAdapter.itemCount)
        } else {
            headers[position] =
                    RestHeaderModel(
                        headers[position].id,
                        headers[position].rId,
                        key,
                        value
                    )
            rvAdapter.notifyItemChanged(position)
        }
    }

    companion object {
        const val EDIT_HEADERS_REQUEST_CODE: Int = 10214
        const val REST_HEADERS_ACTIVITY_LIST_KEY = "REST_HEADERS_ACTIVITY_LIST_KEY"

        fun start(activity: AppCompatActivity, headers: ArrayList<RestHeaderModel>) {
            val intent = Intent(activity, RestHeadersActivity::class.java)
            intent.putParcelableArrayListExtra(
                REST_HEADERS_ACTIVITY_LIST_KEY,
                headers
            )

            activity.startActivityForResult(
                intent,
                EDIT_HEADERS_REQUEST_CODE
            )
        }
    }

}