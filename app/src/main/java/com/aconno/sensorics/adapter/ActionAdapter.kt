package com.aconno.sensorics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.actions.Action
import kotlinx.android.synthetic.main.item_action.view.*

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
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_action, parent, false
        )
        return ViewHolder(view)
    }

    override fun getKeyForItem(item: Action): Long {
        return item.id
    }

    inner class ViewHolder(
        val view: View
    ) : SelectableRecyclerViewAdapter<Action, Long, ViewHolder>.ViewHolder(view) {
        override fun bind(item: Action) {
            with(itemView.cb_item_selected) {
                visibility = if (isItemSelectionEnabled) View.VISIBLE else View.GONE
                isChecked = isItemSelected(item)
            }


            view.text_name.text = item.name
            view.text_mac_address.text = item.device.macAddress
            view.text_condition.text = item.condition.toStringRepresentation()
            view.text_outcome.text = item.outcome.toString()
            view.action_switch.isChecked = item.active

            view.action_switch.setOnClickListener {
                checkedChangeListener?.onCheckedChange(item, view.action_switch.isChecked)
            }

            view.setOnClickListener {
                if (isItemSelectionEnabled) {
                    itemView.cb_item_selected.isChecked = !itemView.cb_item_selected.isChecked
                    setSelected(item, itemView.cb_item_selected.isChecked)
                } else {
                    onItemClick(item)
                }
            }

            itemView.cb_item_selected.setOnClickListener { _ ->
                setSelected(item, itemView.cb_item_selected.isChecked)
            }

            view.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            view.tag = item.id
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(action: Action, checked: Boolean)
    }
}
