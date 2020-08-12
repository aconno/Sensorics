package com.aconno.sensorics.ui.actions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.dialog_condition.view.*

class ConditionDialog(
    context: Context,
    private val readingType: String,
    private val listener: ConditionDialogListener
) : AlertDialog(context) {

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(readingType)
        val contentView = layoutInflater.inflate(R.layout.dialog_condition, null, false)
        setView(contentView)
        addContentButtonsOnClickListeners(contentView)
        addActionButtonsOnClickListeners(contentView)
        super.onCreate(savedInstanceState)
    }

    private fun addActionButtonsOnClickListeners(contentView: View) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(R.string.condition_dialog_set)
        ) { _, _ -> }
        setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                onSetClicked(contentView)
            }
        }
        setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(R.string.cancel)
        ) { dialog, _ -> dialog.cancel() }
    }

    private fun addContentButtonsOnClickListeners(contentView: View) {
        contentView.view_less.setOnClickListener {
            contentView.view_less.isChecked = !contentView.view_less.isChecked
            contentView.view_equal.isChecked = false
            contentView.view_more.isChecked = false
        }
        contentView.view_equal.setOnClickListener {
            contentView.view_less.isChecked = false
            contentView.view_equal.isChecked = !contentView.view_equal.isChecked
            contentView.view_more.isChecked = false
        }
        contentView.view_more.setOnClickListener {
            contentView.view_less.isChecked = false
            contentView.view_equal.isChecked = false
            contentView.view_more.isChecked = !contentView.view_more.isChecked
        }
    }

    private fun onSetClicked(rootView: View) {
        val conditionOperator = when {
            rootView.view_less.isChecked -> "<"
            rootView.view_equal.isChecked -> "="
            rootView.view_more.isChecked -> ">"
            else -> null
        }
        val conditionVariable = rootView.view_value.text.toString()
        if (conditionOperator != null && conditionVariable.isNotBlank()) {
            listener.onSetClicked(readingType, conditionOperator, conditionVariable)
            dismiss()
        }
    }
}