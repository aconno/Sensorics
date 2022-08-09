package com.aconno.sensorics.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.aconno.sensorics.R

class PasswordDialog {
    companion object {
        fun create(
            forceKeyboardAppearing:Boolean = false,
            context: Context,
            action: OnPasswordDialogAction
        ): AlertDialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.dialog_password, null as ViewGroup?)
            builder.setView(view)
            val etPassword = view.findViewById<EditText>(R.id.et_password)
            builder.setPositiveButton(R.string.confirm) { dialog, _ ->
                action.onPasswordEntered(etPassword?.text.toString())
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.cancel) { dialog, _ ->
                action.onDialogCancelled()
                dialog.dismiss()
            }
            builder.setCancelable(false)
            builder.setOnCancelListener { action.onDialogCancelled() }
            return builder.create().apply {
                if (forceKeyboardAppearing) {
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                }
            }
        }
    }

    interface OnPasswordDialogAction {
        fun onPasswordEntered(password: String)
        fun onDialogCancelled()
    }
}