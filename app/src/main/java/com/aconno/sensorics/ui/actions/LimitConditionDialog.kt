package com.aconno.sensorics.ui.actions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.DialogConditionBinding
import com.aconno.sensorics.domain.ifttt.LimitCondition

class LimitConditionDialog(
    context: Context,
    private val readingType: String,
    private val listener: LimitConditionDialogListener
) : AlertDialog(context) {

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTitle(readingType)

        val binding = DialogConditionBinding.inflate(layoutInflater, null, false)

        setView(binding.root)
        addContentButtonsOnClickListeners(binding)
        addActionButtonsOnClickListeners(binding)

        super.onCreate(savedInstanceState)
    }

    private fun addActionButtonsOnClickListeners(binding: DialogConditionBinding) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(R.string.condition_dialog_set)
        ) { _, _ -> }
        setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                respondWithConditionIfValid(binding)
            }
        }
        setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(R.string.cancel)
        ) { dialog, _ -> dialog.cancel() }
    }

    private fun addContentButtonsOnClickListeners(binding: DialogConditionBinding) {
        val views = listOf(
            binding.viewLess,
            binding.viewEqual,
            binding.viewMore,
            binding.viewChanged
        )

        views.forEach { view ->
            view.setOnClickListener {
                views.forEach {
                    it.isChecked = if (view == it) {
                        !view.isChecked
                    } else {
                        false
                    }
                    binding.viewValue.visibility = if (view == binding.viewChanged) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
    }

    private fun respondWithConditionIfValid(binding: DialogConditionBinding) {
        val conditionOperator = getSelectedOperator(binding)
        val conditionLimit = getLimitParameter(binding)

        if (conditionOperator != null && conditionLimit != null) {
            val limitCondition = LimitCondition(readingType, conditionOperator, conditionLimit)
            listener.applyLimitCondition(limitCondition)
            dismiss()
        }
    }

    private fun getSelectedOperator(binding: DialogConditionBinding) = when {
        binding.viewLess.isChecked -> LimitCondition.LimitOperator.LESS_THAN
        binding.viewEqual.isChecked -> LimitCondition.LimitOperator.EQUAL_TO
        binding.viewMore.isChecked -> LimitCondition.LimitOperator.MORE_THAN
        binding.viewChanged.isChecked -> LimitCondition.LimitOperator.CHANGED
        else -> null
    }

    private fun getLimitParameter(binding: DialogConditionBinding): Float? {
        val parameter = binding.viewValue.text.toString()

        return if (parameter.isBlank()) {
            if (!binding.viewChanged.isChecked) {
                null
            } else {
                0f
            }
        } else {
            try {
                parameter.toFloat()
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}