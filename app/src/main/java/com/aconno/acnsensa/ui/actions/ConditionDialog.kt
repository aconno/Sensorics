package com.aconno.acnsensa.ui.actions

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.model.toStringResource
import kotlinx.android.synthetic.main.dialog_condition.*

class ConditionDialog : DialogFragment() {

    private lateinit var listener: ConditionDialogListener

    private var sensorType: SensorTypeSingle? = null

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

        view_title.text = sensorType?.toStringResource(view.context)

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

        private const val SENSOR_TYPE_EXTRA = "sensor_type"

        fun newInstance(sensorType: SensorTypeSingle): ConditionDialog {
            val dialog = ConditionDialog()
            val args = Bundle()
            args.putString(SENSOR_TYPE_EXTRA, sensorType.name)
            dialog.arguments = args
            return dialog
        }

        private fun getSensorTypeExtra(args: Bundle): SensorTypeSingle {
            val name = args.getString(SENSOR_TYPE_EXTRA)
            return SensorTypeSingle.valueOf(name)
        }
    }
}