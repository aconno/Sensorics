package com.aconno.sensorics.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.databinding.ItemActionBinding
import com.aconno.sensorics.domain.actions.Action

class ActionAdapter(
    actions: MutableList<Action>,
    itemSelectedListener: ItemSelectedListener<Action>?,
    clickListener: ItemClickListener<Action>?,
    longClickListener: ItemLongClickListener<Action>?
) : SelectableRecyclerViewAdapter<Action, Long, ActionAdapter.ViewHolder>(
    actions, itemSelectedListener, clickListener, longClickListener
) {

    var checkedChangeListener: OnCheckedChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getKeyForItem(item: Action): Long {
        return item.id
    }

    inner class ViewHolder(
        val binding: ItemActionBinding
    ) : SelectableRecyclerViewAdapter<Action, Long, ViewHolder>.ViewHolder(binding.root) {

        override fun bind(item: Action) {
            with(binding.cbItemSelected) {
                visibility = if (isItemSelectionEnabled) View.VISIBLE else View.GONE
                isChecked = isItemSelected(item)
            }

            binding.textName.text = item.name
            binding.textMacAddress.text = item.device.macAddress
            binding.textCondition.text = item.condition.toStringRepresentation()
            binding.textOutcome.text = item.outcome.toString()
            binding.actionSwitch.isChecked = item.active

            binding.actionSwitch.setOnClickListener {
                checkedChangeListener?.onCheckedChange(item, binding.actionSwitch.isChecked)
            }

            binding.root.setOnClickListener {
                if (isItemSelectionEnabled) {
                    binding.cbItemSelected.isChecked = !binding.cbItemSelected.isChecked
                    setSelected(item, binding.cbItemSelected.isChecked)
                } else {
                    onItemClick(item)
                }
            }

            binding.cbItemSelected.setOnClickListener { _ ->
                setSelected(item, binding.cbItemSelected.isChecked)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            binding.root.tag = item.id
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(action: Action, checked: Boolean)
    }
}
