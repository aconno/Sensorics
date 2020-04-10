package com.aconno.sensorics.ui.settings.publishers.resthttpgetparams

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.aconno.sensorics.R
import com.aconno.sensorics.model.RestHttpGetParamModel
import kotlinx.android.synthetic.main.fragment_add_resthttpgetparam.view.*


class AddRestHttpGetParamDialog : DialogFragment() {
    private lateinit var listener: RestHttpGetParamDialogInteractionListener

    private var restHttpGetParamModel: RestHttpGetParamModel? = null
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            if (args.containsKey(ADD_REST_HTTPGET_PARAMS_DIALOG_KEY)
                && args.containsKey(ADD_REST_HTTPGET_PARAMS_DIALOG_POS_KEY)
            ) {
                restHttpGetParamModel = args.getParcelable(ADD_REST_HTTPGET_PARAMS_DIALOG_KEY)
                position = args.getInt(ADD_REST_HTTPGET_PARAMS_DIALOG_POS_KEY)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_resthttpgetparam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        restHttpGetParamModel?.let { model ->
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
        if (context is RestHttpGetParamDialogInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    interface RestHttpGetParamDialogInteractionListener {
        fun onDialogInteraction(position: Int, key: String, value: String)
    }

    companion object {
        private const val ADD_REST_HTTPGET_PARAMS_DIALOG_KEY = "ADD_REST_HTTPGET_PARAMS_DIALOG_KEY"
        private const val ADD_REST_HTTPGET_PARAMS_DIALOG_POS_KEY =
            "ADD_REST_HTTPGET_PARAMS_DIALOG_POS_KEY"

        @JvmStatic
        fun newInstance(
            restHttpGetParamModel: RestHttpGetParamModel?,
            position: Int
        ): AddRestHttpGetParamDialog {
            return AddRestHttpGetParamDialog().apply {
                restHttpGetParamModel?.let { model ->
                    arguments = Bundle().apply {
                        putParcelable(ADD_REST_HTTPGET_PARAMS_DIALOG_KEY, model)
                        putInt(ADD_REST_HTTPGET_PARAMS_DIALOG_POS_KEY, position)
                    }
                }
            }
        }
    }
}
