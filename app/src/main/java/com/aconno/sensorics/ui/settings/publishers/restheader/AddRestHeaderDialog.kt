package com.aconno.sensorics.ui.settings.publishers.restheader

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.aconno.sensorics.R
import com.aconno.sensorics.model.RestHeaderModel


class AddRestHeaderDialog : DialogFragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var keyText: AutoCompleteTextView? = null
    private var valueText: EditText? = null

    private var restHeaderModel: RestHeaderModel? = null
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ADD_REST_HEADER_DIALOG_KEY) &&
                it.containsKey(ADD_REST_HEADER_DIALOG_POS_KEY)
            ) {
                restHeaderModel = it.getParcelable(ADD_REST_HEADER_DIALOG_KEY)
                position = it.getInt(ADD_REST_HEADER_DIALOG_POS_KEY)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        removeTitleSpacing()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_restheader, container, false)

        keyText = view.findViewById(R.id.edit_key)
        valueText = view.findViewById(R.id.edit_value)

        val adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.header_keys)
        )

        keyText?.setAdapter(adapter)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        if (restHeaderModel != null) {
            this.keyText?.setText(restHeaderModel!!.key)
            this.valueText?.setText(restHeaderModel!!.value)
        }

        val addButton = view.findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {

            val key = keyText!!.text.toString()
            val value = valueText!!.text.toString()

            if (isNotEmpty(key, value)) {
                listener?.onFragmentInteraction(
                    position,
                    key,
                    value
                )

                this.dismiss()
            } else {
                Toast.makeText(context, getString(R.string.values_cannot_empty), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val closeButton = view.findViewById<Button>(R.id.close_button)
        closeButton.setOnClickListener {
            this.dismiss()
        }
        return view
    }

    private fun removeTitleSpacing() {
        // Required for Lollipop (maybe others too) devices
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun isNotEmpty(vararg array: String): Boolean {
        array.forEach { item ->
            item.trim().let {
                if (it.isBlank()) {
                    return false
                }
            }
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(position: Int, key: String, value: String)
    }

    companion object {
        private const val ADD_REST_HEADER_DIALOG_KEY = "ADD_REST_HEADER_DIALOG_KEY"
        private const val ADD_REST_HEADER_DIALOG_POS_KEY = "ADD_REST_HEADER_DIALOG_POS_KEY"

        @JvmStatic
        fun newInstance(restHeaderModel: RestHeaderModel?, position: Int): AddRestHeaderDialog {
            val fragment = AddRestHeaderDialog()

            restHeaderModel?.let {
                fragment.arguments = Bundle().apply {
                    putParcelable(ADD_REST_HEADER_DIALOG_KEY, it)
                    putInt(ADD_REST_HEADER_DIALOG_POS_KEY, position)
                }
            }

            return fragment
        }
    }

}
