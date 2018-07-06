package com.aconno.sensorics.ui.actions

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.dialog_condition.*

class ConditionDialog : DialogFragment() {

    private lateinit var listener: ConditionDialogListener

    private var sensorType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_condition, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorType = arguments?.let { getSensorTypeExtra(it) }

        view_title.text = sensorType?.toString()

        view_less.setOnClickListener {
            view_less.isChecked = !view_less.isChecked
        }

        view_equal.setOnClickListener {
            view_equal.isChecked = !view_equal.isChecked
        }

        view_more.setOnClickListener {
            view_more.isChecked = !view_more.isChecked
        }

        button_cancel.setOnClickListener {
            dialog.cancel()
        }

        button_set.setOnClickListener {
            onSetClicked()
        }
    }

    private fun onSetClicked() {
        val condition = getCondition()
        val value = view_value.text.toString()
        sensorType?.let {
            listener.onSetClicked(it, condition, value)
        }
        dialog.dismiss()
    }

    private fun getCondition(): String {
        //TODO: Implement this better
        if (view_less.isChecked) {
            return "<"
        }
        if (view_more.isChecked) {
            return ">"
        }
        return "="
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as ConditionDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement " + ConditionDialogListener::class.toString())
        }
    }

    companion object {

        private const val TYPE_EXTRA = "type_extra"

        fun newInstance(readingType: String): ConditionDialog {
            val dialog = ConditionDialog()
            val args = Bundle()
            args.putString(TYPE_EXTRA, readingType)
            dialog.arguments = args
            return dialog
        }

        private fun getSensorTypeExtra(args: Bundle): String {
            return args.getString(TYPE_EXTRA)
        }
    }
}