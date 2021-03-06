package com.aconno.sensorics.ui.actions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.ifttt.LimitCondition
import kotlinx.android.synthetic.main.dialog_condition.view.*

class LimitConditionDialog(
    context: Context,
    private val readingType: String,
    private val listener: LimitConditionDialogListener
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
                respondWithConditionIfValid(contentView)
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

    private fun respondWithConditionIfValid(rootView: View) {
        val conditionOperator = getSelectedOperator(rootView)
        val conditionLimit = getLimitParameter(rootView)
        if (conditionOperator != null && conditionLimit != null) {
            val limitCondition = LimitCondition(readingType, conditionOperator, conditionLimit)
            listener.applyLimitCondition(limitCondition)
            dismiss()
        }
    }

    private fun getSelectedOperator(rootView: View) = when {
        rootView.view_less.isChecked -> LimitCondition.LimitOperator.LESS_THAN
        rootView.view_equal.isChecked -> LimitCondition.LimitOperator.EQUAL_TO
        rootView.view_more.isChecked -> LimitCondition.LimitOperator.MORE_THAN
        else -> null
    }

    private fun getLimitParameter(rootView: View): Float? {
        val parameter = rootView.view_value.text.toString()
        if (parameter.isBlank()) return null
        return try {
            parameter.toFloat()
        } catch (e: NumberFormatException) {
            null
        }
    }
}