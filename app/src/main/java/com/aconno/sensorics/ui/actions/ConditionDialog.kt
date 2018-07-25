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

    private var listener: ConditionDialogListener? = null

    private var sensorType: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as ConditionDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ConditionDialogListener")
        }
    }

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

        view_title.text = sensorType

        view_less.setOnClickListener {
            view_less.isChecked = !view_less.isChecked
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun onSetClicked() {
        if ((view_less.isChecked xor view_more.isChecked) && view_value.text.isNotBlank()) {
            val condition = if (view_less.isChecked) "<" else ">"
            val value = view_value.text.toString()
            sensorType?.let {
                listener?.onSetClicked(it, condition, value)
            }
            dialog.dismiss()
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