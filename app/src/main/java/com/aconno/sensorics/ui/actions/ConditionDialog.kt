package com.aconno.sensorics.ui.actions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.dialog_condition.*

class ConditionDialog : DialogFragment() {

    private var listener: ConditionDialogListener? = null

    private var sensorType: String? = null

    override fun onAttach(context: Context) {
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
        removeTitleSpacing()
        return inflater.inflate(R.layout.dialog_condition, container)
    }

    private fun removeTitleSpacing() {
        // Required for Lollipop (maybe others too) devices
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorType = arguments?.let { getSensorTypeExtra(it) }

        view_title.text = sensorType

        view_less.setOnClickListener {
            view_less.isChecked = !view_less.isChecked
            view_equal.isChecked = false
            view_more.isChecked = false
        }

        view_equal.setOnClickListener {
            view_less.isChecked = false
            view_equal.isChecked = !view_equal.isChecked
            view_more.isChecked = false
        }

        view_more.setOnClickListener {
            view_less.isChecked = false
            view_equal.isChecked = false
            view_more.isChecked = !view_more.isChecked
        }

        button_cancel.setOnClickListener {
            dialog?.cancel()
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
        when {
            view_less.isChecked -> "<"
            view_equal.isChecked -> "="
            view_more.isChecked -> ">"
            else -> null
        }?.takeIf { view_value.text.isNotBlank() }?.let { condition ->
            val value = view_value.text.toString()
            sensorType?.let {
                listener?.onSetClicked(it, condition, value)
            }
            dialog?.dismiss()
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

        private fun getSensorTypeExtra(args: Bundle): String? {
            return args.getString(TYPE_EXTRA)
        }
    }
}