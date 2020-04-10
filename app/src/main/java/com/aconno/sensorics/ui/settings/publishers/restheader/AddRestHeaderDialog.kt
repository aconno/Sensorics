package com.aconno.sensorics.ui.settings.publishers.restheader

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.aconno.sensorics.R
import com.aconno.sensorics.model.RestHeaderModel
import kotlinx.android.synthetic.main.fragment_add_restheader.view.*


class AddRestHeaderDialog : DialogFragment() {
    private lateinit var listener: RestHeaderDialogInteractionListener

    private var restHeaderModel: RestHeaderModel? = null
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            if (args.containsKey(ADD_REST_HEADER_DIALOG_KEY)
                    && args.containsKey(ADD_REST_HEADER_DIALOG_POS_KEY)) {
                restHeaderModel = args.getParcelable(ADD_REST_HEADER_DIALOG_KEY)
                position = args.getInt(ADD_REST_HEADER_DIALOG_POS_KEY)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_restheader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        view.edit_key.setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.header_keys)
        ))

        restHeaderModel?.let { model ->
            view.edit_key.setText(model.key)
            view.edit_value.setText(model.value)
        }

        view.add_button.setOnClickListener {
            val key = view.edit_key.text.toString()
            val value = view.edit_value.text.toString()

            if (listOf(key, value).all { it.isNotBlank() }) {
                listener.onDialogInteraction(position, key, value)
                this.dismiss()
            } else {
                Toast.makeText(context, R.string.values_cannot_empty, Toast.LENGTH_SHORT).show()
            }
        }

        view.close_button.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RestHeaderDialogInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    interface RestHeaderDialogInteractionListener {
        fun onDialogInteraction(position: Int, key: String, value: String)
    }

    companion object {
        private const val ADD_REST_HEADER_DIALOG_KEY = "ADD_REST_HEADER_DIALOG_KEY"
        private const val ADD_REST_HEADER_DIALOG_POS_KEY = "ADD_REST_HEADER_DIALOG_POS_KEY"

        @JvmStatic
        fun newInstance(restHeaderModel: RestHeaderModel?, position: Int): AddRestHeaderDialog {
            return AddRestHeaderDialog().apply {
                restHeaderModel?.let { model ->
                    arguments = Bundle().apply {
                        putParcelable(ADD_REST_HEADER_DIALOG_KEY, model)
                        putInt(ADD_REST_HEADER_DIALOG_POS_KEY, position)
                    }
                }
            }
        }
    }
}
