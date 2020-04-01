package com.aconno.sensorics.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.actions.Action
import kotlinx.android.synthetic.main.item_action.view.*
import java.util.*
import kotlin.collections.HashMap

class ActionAdapter(
    private val actions: MutableList<Action>,
    private val clickListener: ItemClickListener<Action>,
    private val longClickListener: OnListItemLongClickListener?,
    private val itemSelectedListener: OnListItemSelectedListener? = null
) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {
    var checkedChangeListener: OnCheckedChangeListener? = null
    var itemSelectionEnabled = false
        private set
    private var itemSelectedMap: MutableMap<Long, Boolean> = HashMap()//maps item id to current item selection state

    fun enableItemSelection(initiallySelectedItem: Action? = null) {
        itemSelectionEnabled = true
        itemSelectedMap.clear()
        initiallySelectedItem?.let {
            itemSelectedMap[it.id] = true
        }
        notifyDataSetChanged()
    }

    fun disableItemSelection() {
        itemSelectionEnabled = false
        itemSelectedMap.clear()
        notifyDataSetChanged()
    }

    fun getNumberOfSelectedItems(): Int = itemSelectedMap.count { entry -> entry.value }

    fun getSelectedItems(): List<Action> = actions.filter { itemSelectedMap[it.id] == true }

    fun setItemsAsSelected(items: List<Action>) {
        for (item in items) {
            onItemSelectionStateChanged(true, item)
        }
        notifyDataSetChanged()
    }

    private fun onItemSelectionStateChanged(selected: Boolean, item: Action) {
        itemSelectedMap[item.id] = selected
        itemSelectedListener?.apply {
            if (selected) onListItemSelected(item)
            else onListItemDeselected(item)
        }
    }

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
        notifyDataSetChanged()
    }

    fun getActions(): List<Action> {
        return Collections.unmodifiableList(actions)
    }

    fun getAction(position: Int): Action {
        return actions[position]
    }

    fun addActionAtPosition(action: Action, position: Int) {
        actions.add(position, action)
        notifyItemInserted(position)
    }

    fun removeAction(position: Int) {
        actions.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return actions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    fun appendActions(newActions: List<Action>) {
        val offset = actions.size
        actions.addAll(newActions)
        notifyItemRangeChanged(offset, newActions.size)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(action: Action) {
            val selectionButton = itemView.findViewById<CheckBox>(R.id.item_selected)
            with(selectionButton) {
                visibility = if (itemSelectionEnabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                isChecked = itemSelectedMap[action.id] ?: false
            }


            view.text_name.text = action.name
            view.text_mac_address.text = action.device.macAddress
            view.text_condition.text = action.condition.toStringRepresentation()
            view.text_outcome.text = action.outcome.toString()
            view.action_switch.isChecked = action.active

            view.action_switch.setOnCheckedChangeListener { _, isChecked ->
                checkedChangeListener?.onCheckedChange(isChecked, action)
            }
            view.setOnClickListener {
                if (itemSelectionEnabled) {
                    selectionButton.isChecked = !selectionButton.isChecked
                    onItemSelectionStateChanged(selectionButton.isChecked, action)
                } else {
                    clickListener.onItemClick(action)
                }
            }

            selectionButton.setOnClickListener {
                onItemSelectionStateChanged(selectionButton.isChecked, action)
            }

            view.setOnLongClickListener {
                longClickListener?.onListItemLongClick(action)
                true
            }
            view.tag = action.id
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, action: Action)
    }


    interface OnListItemLongClickListener {
        fun onListItemLongClick(item: Action)
    }

    interface OnListItemSelectedListener {
        fun onListItemSelected(item: Action)
        fun onListItemDeselected(item: Action)
    }

}
