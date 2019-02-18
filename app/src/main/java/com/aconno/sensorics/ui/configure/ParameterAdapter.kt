package com.aconno.sensorics.ui.configure

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SeekBar
import com.aconno.bluetooth.beacon.Beacon
import com.aconno.bluetooth.beacon.Parameter
import com.aconno.bluetooth.beacon.ValueConverter
import com.aconno.sensorics.R
import kotlinx.android.synthetic.main.parameter_boolean.view.*
import kotlinx.android.synthetic.main.parameter_enum.view.*
import kotlinx.android.synthetic.main.parameter_name.view.*
import kotlinx.android.synthetic.main.parameter_number.view.*
import kotlinx.android.synthetic.main.parameter_number_decimal.view.*
import kotlinx.android.synthetic.main.parameter_text.view.*
import timber.log.Timber

class ParameterAdapter(val beacon: Beacon) : RecyclerView.Adapter<ParameterAdapter.ListItem>() {
    val TYPE_GROUP = 0
    val TYPE_PARAMETER_BOOLEAN = 1
    val TYPE_PARAMETER_TEXT = 2
    val TYPE_PARAMETER_NUMBER = 3
    val TYPE_PARAMETER_NUMBER_DECIMAL = 4
    val TYPE_PARAMETER_ENUM = 5

    val parameterList: List<Parameter> = beacon.parameters.flatMap { x -> x.value }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        return when (viewType) {
            TYPE_PARAMETER_BOOLEAN -> ParameterItemBoolean(parent)
            TYPE_PARAMETER_TEXT -> ParameterItemText(parent)
            TYPE_PARAMETER_NUMBER -> ParameterItemNumber(parent)
            TYPE_PARAMETER_NUMBER_DECIMAL -> ParameterItemNumberDecimal(parent)
            TYPE_PARAMETER_ENUM -> ParameterItemEnum(parent)
            else -> throw IllegalArgumentException("This shouldn't happen!")
        }
    }


    override fun onBindViewHolder(holder: ListItem, position: Int) {
        holder.bind(parameterList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (parameterList[position].type) {
            ValueConverter.BOOLEAN -> TYPE_PARAMETER_BOOLEAN
            ValueConverter.UTF8STRING -> TYPE_PARAMETER_TEXT
            ValueConverter.BYTE, ValueConverter.SINT8, ValueConverter.UINT8, ValueConverter.SINT16, ValueConverter.UINT16, ValueConverter.SINT32, ValueConverter.UINT32 -> TYPE_PARAMETER_NUMBER
            ValueConverter.FLOAT -> TYPE_PARAMETER_NUMBER_DECIMAL
            ValueConverter.ENUM -> TYPE_PARAMETER_ENUM
            ValueConverter.TIME -> TODO()
            ValueConverter.MAC_ADDRESS -> TODO()
        }
    }


    override fun getItemCount(): Int = beacon.parameters.map { x -> x.value.size }.sum()

    abstract class ListItem(open val parent: ViewGroup, open val resource: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(resource, parent, false)
        ) {
        abstract val type: Int
        abstract fun bind(parameter: Parameter)
    }

//    inner class GroupItem(override var view: View) : ListItem(view) {
//        override fun bind(parameter: Parameter) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override val type: Int = TYPE_GROUP
//    }

    abstract inner class ParameterItem(override val parent: ViewGroup, override val resource: Int) :
        ListItem(parent, resource) {
        override val type: Int = -1

        override fun bind(parameter: Parameter) {
            itemView.tv_parameter_name.text = parameter.name
        }
    }

    inner class ParameterItemBoolean(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.parameter_boolean
    ), CompoundButton.OnCheckedChangeListener {
        override fun bind(parameter: Parameter) {
            super.bind(parameter)
            itemView.sw_parameter.isEnabled = parameter.writable
            itemView.sw_parameter.isChecked = parameter.value as Boolean
            itemView.sw_parameter.setOnCheckedChangeListener(this)
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            parameterList[adapterPosition].value = isChecked
        }
    }

    inner class ParameterItemNumber(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.parameter_number
    ), TextWatcher, SeekBar.OnSeekBarChangeListener {

        override fun bind(parameter: Parameter) {
            super.bind(parameter)
            itemView.et_parameter_number.isEnabled = parameter.writable
            itemView.et_parameter_number.setText(parameter.value.toString())
            itemView.et_parameter_number.addTextChangedListener(this)

            itemView.sb_parameter_number.apply {
                isEnabled = parameter.writable
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    min = parameter.min
                    max = parameter.max
                } else {
                    max = parameter.max - parameter.min
                }
                progress = (parameter.value as Number).toInt()
                setOnSeekBarChangeListener(this@ParameterItemNumber)
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString().isEmpty()) return

            parameterList[adapterPosition].let {
                it.value = it.type.converter.fromString(s.toString())!!
                itemView.sb_parameter_number.progress =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        it.value as Number
                    } else {
                        (it.value as Number).toLong() - it.min
                    }.toInt()
            }
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!fromUser) return
            parameterList[adapterPosition].let { parameter ->
                val realProgress: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progress
                } else {
                    progress + parameter.min
                }

                itemView.et_parameter_number.setText(realProgress.toString())

                parameter.value = parameter.type.converter.fromString(realProgress.toString())!!
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    inner class ParameterItemNumberDecimal(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.parameter_number_decimal
    ), TextWatcher {
        override fun bind(parameter: Parameter) {
            super.bind(parameter)
            itemView.et_parameter_number_decimal.isEnabled = parameter.writable
            itemView.et_parameter_number_decimal.setText(parameter.value.toString())
            itemView.et_parameter_number_decimal.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString().isEmpty()) return
            parameterList[adapterPosition].let {
                it.value = it.type.converter.fromString(s.toString())!!
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    inner class ParameterItemText(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.parameter_text
    ), TextWatcher {
        override fun bind(parameter: Parameter) {
            super.bind(parameter)
            itemView.et_parameter_text.isEnabled = parameter.writable
            itemView.et_parameter_text.setText(parameter.value as String)
            itemView.et_parameter_text.addTextChangedListener(this)
        }

        override fun afterTextChanged(s: Editable) {
            if (s.toString().isEmpty()) return
            parameterList[adapterPosition].let {
                it.value = it.type.converter.fromString(s.toString())!!
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    inner class ParameterItemEnum(override val parent: ViewGroup) : ParameterItem(
        parent, R.layout.parameter_enum
    ), AdapterView.OnItemSelectedListener {

        override fun bind(parameter: Parameter) {
            super.bind(parameter)
            itemView.sp_parameter.isEnabled = parameter.writable
            itemView.sp_parameter.adapter = ArrayAdapter<String>(
                itemView.context,
                android.R.layout.simple_spinner_item,
                parameter.choices!!
            )

            if (parameter.value is Long) {
                itemView.sp_parameter.setSelection((parameter.value as Long).toInt())
                Timber.e("Position = ${(parameter.value as Long).toInt()}")

            } else {
                itemView.sp_parameter.setSelection(parameter.value as Int)
                Timber.e("Position = ${parameter.value as Int}")
            }

            itemView.sp_parameter.onItemSelectedListener = this
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Timber.e("Item $position selected!")
            parameterList[adapterPosition].value = position
        }
    }
}