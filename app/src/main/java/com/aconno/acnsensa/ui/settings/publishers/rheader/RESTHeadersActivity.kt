package com.aconno.acnsensa.ui.settings.publishers.rheader

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.LongItemClickListener
import com.aconno.acnsensa.model.RESTHeaderModel
import kotlinx.android.synthetic.main.activity_rest_headers.*


class RESTHeadersActivity : AppCompatActivity(),
    AddRESTHeaderDialog.OnFragmentInteractionListener,
    LongItemClickListener<RESTHeaderModel> {

    private lateinit var headers: ArrayList<RESTHeaderModel>
    private lateinit var rvAdapter: RESTHeadersAdapter
    private var onItemClickListener: ItemClickListenerWithPos<RESTHeaderModel>
    private var selectedItem: RESTHeaderModel? = null

    var dialogClickListener: DialogInterface.OnClickListener =
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

    init {
        onItemClickListener = object :
            ItemClickListenerWithPos<RESTHeaderModel> {
            override fun onItemClick(position: Int, item: RESTHeaderModel?) {
                AddRESTHeaderDialog.newInstance(
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

        initView()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.rest_headers_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        rvAdapter = RESTHeadersAdapter(
            headers,
            onItemClickListener, this
        )
        with(recyclerView) {
            layoutManager = android.support.v7.widget.LinearLayoutManager(context)
            adapter = rvAdapter

            val dividerItemDecoration = android.support.v7.widget.DividerItemDecoration(
                recyclerView.context,
                (layoutManager as LinearLayoutManager).orientation
            )
            this.addItemDecoration(dividerItemDecoration)
        }

        button_addHeader.setOnClickListener {
            AddRESTHeaderDialog.newInstance(
                null,
                -1
            )
                .show(supportFragmentManager, null)
        }

    }

    override fun onLongClick(param: RESTHeaderModel) {
        selectedItem = param
        val builder = AlertDialog.Builder(this)

        builder.setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(getString(R.string.yes), dialogClickListener)
            .setNegativeButton(getString(R.string.no), dialogClickListener)
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
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deleteSelectedItem() {
        selectedItem?.let {
            val index = headers.indexOf(selectedItem!!)
            headers.remove(selectedItem!!)
            rvAdapter.notifyItemRemoved(index)

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
        if (position == -1) {
            headers.add(
                RESTHeaderModel(
                    0L, 0L, key, value
                )
            )
            rvAdapter.notifyItemInserted(rvAdapter.itemCount)
        } else {
            headers[position] =
                    RESTHeaderModel(
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

        fun start(activity: AppCompatActivity, headers: ArrayList<RESTHeaderModel>) {
            val intent = Intent(activity, RESTHeadersActivity::class.java)
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