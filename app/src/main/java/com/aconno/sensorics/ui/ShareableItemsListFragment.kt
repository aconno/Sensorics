package com.aconno.sensorics.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.interactor.data.ReadTextUseCase
import com.aconno.sensorics.domain.interactor.data.StoreTempTextUseCase
import com.aconno.sensorics.domain.interactor.data.StoreTextUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertJsonToObjectsUseCase
import com.aconno.sensorics.domain.interactor.publisher.ConvertObjectsToJsonUseCase
import com.aconno.sensorics.ui.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

abstract class ShareableItemsListFragment<T> : BaseFragment() {
    private var tempExportJSONData: String? = null
    private lateinit var tempSharedFile: File
    abstract val sharedFileNamePrefix : String
    abstract val exportedFileName : String

    @Inject
    lateinit var storeTextUseCase: StoreTextUseCase

    @Inject
    lateinit var storeTempTextUseCase: StoreTempTextUseCase

    @Inject
    lateinit var readTextUseCase: ReadTextUseCase


    private fun writeJSONToFile(uri: Uri?) {
        tempExportJSONData?.let {
            uri?.toString()?.let { uriString ->
                storeTextUseCase.execute(uriString, it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete {
                            showSnackbarMessage(getString(R.string.file_saved))
                        }.doOnError {
                            showSnackbarMessage(getString(R.string.file_not_saved))
                        }.subscribe().also {
                            addDisposable(it)
                        }
            }
        }
    }

    private fun showSnackbarMessage(message : String) {
        view?.let {
            Snackbar.make(it,
                    message,
                    Snackbar.LENGTH_SHORT).show()
        }
    }

    fun showExportOptionsDialog(sharedItem : T) {
        activity?.let {
            sharedItem?.let { item ->
                val options = resources.getStringArray(R.array.ExportOptions)

                AlertDialog.Builder(it)
                        .setTitle(R.string.export)
                        .setItems(options) { dialog, which ->
                            getConvertToJsonUseCase().execute(listOf(sharedItem))
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ result ->
                                        when (options[which]) {
                                            getString(R.string.share_text) -> shareJSONtext(result)
                                            getString(R.string.share_file) -> shareJSONfile(result)
                                            getString(R.string.export_file) -> {
                                                tempExportJSONData = result
                                                startExportJSONActivity()
                                            }
                                        }
                                    }, {
                                        showSnackbarMessage(getString(R.string.error_converting_data_to_json))
                                    }).also {
                                        addDisposable(it)
                                    }
                        }
                        .create()
                        .show()
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun shareJSONtext(data: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.export)))
    }

    abstract fun getConvertToJsonUseCase() : ConvertObjectsToJsonUseCase<T>
    abstract fun getItems() : List<T>
    open fun getItemsForExport() : List<T> {
        return getItems()
    }

    open fun resolveActionBarEvent(item: MenuItem?) {
        val itemsToExport = getItemsForExport()
        if(itemsToExport.isEmpty() && item?.itemId!=R.id.action_import_file) {
            showSnackbarMessage(
                    if(item?.itemId in arrayOf(R.id.action_export_all,R.id.action_export_selected)) {
                        getString(R.string.export_no_items)
                    } else {
                        getString(R.string.share_no_items)
                    }
            )
            return
        }
        item?.let {
            getConvertToJsonUseCase().execute(itemsToExport)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        when (it.itemId) {
                            R.id.action_import_file -> startImportJSONActivity()
                            R.id.action_share_all_as_file, R.id.action_share_selected_as_file -> shareJSONfile(result)
                            R.id.action_export_all, R.id.action_export_selected -> {
                                tempExportJSONData = result
                                startExportJSONActivity()
                            }
                            R.id.action_share_all_as_text, R.id.action_share_selected_as_text -> shareJSONtext(result)

                        }
                    }, {
                        showSnackbarMessage(getString(R.string.error_converting_data_to_json))
                    }).also {
                        addDisposable(it)
                    }
        }

    }

    @SuppressLint("CheckResult")
    private fun shareJSONfile(data: String) {
        storeTempTextUseCase.execute(data,sharedFileNamePrefix)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uriAndFile ->
                    tempSharedFile = uriAndFile.second
                    val sendIntent: Intent = Intent().apply {
                        type = "text/*"
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        putExtra(Intent.EXTRA_STREAM, Uri.parse(uriAndFile.first))
                        putExtra(Intent.EXTRA_SUBJECT, getFileShareSubject())
                    }
                    startActivityForResult(
                            Intent.createChooser(sendIntent, getString(R.string.share_file)), CODE_SHARE
                    )
                }, {
                    showSnackbarMessage(getString(R.string.sharing_failed))
                }).also {
                    addDisposable(it)
                }
    }

    protected abstract fun getFileShareSubject() : String

    private fun startExportJSONActivity() {
        val exportIntent: Intent = Intent().apply {
            type = "text/*"
            action = Intent.ACTION_CREATE_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, exportedFileName)
        }

        startActivityForResult(exportIntent, CODE_EXPORT)
    }

    private fun startImportJSONActivity() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        startActivityForResult(intent, CODE_IMPORT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_SHARE -> tempSharedFile.delete()
            CODE_EXPORT -> if (resultCode == Activity.RESULT_OK) {
                writeJSONToFile(data?.data)
            }
            CODE_IMPORT -> readFile(data?.data)
        }
    }


    abstract fun onItemsImportedFromFile(items : List<T>)

    abstract fun getConvertFromJsonUseCase() : ConvertJsonToObjectsUseCase<T>

    fun readFile(uri: Uri?) {
        uri?.toString()?.let { uriString ->
            readTextUseCase.execute(uriString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->

                        getConvertFromJsonUseCase().execute(result)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ items ->
                                    onItemsImportedFromFile(items)
                                }, {
                                    showSnackbarMessage(getString(R.string.parsing_json_error))
                                })
                    }, {
                        showSnackbarMessage(getString(R.string.file_not_loaded))
                    }).also {
                        addDisposable(it)
                    }
        }
    }

    companion object {
        private const val CODE_SHARE = 1
        private const val CODE_EXPORT = 2
        private const val CODE_IMPORT = 3
    }

}