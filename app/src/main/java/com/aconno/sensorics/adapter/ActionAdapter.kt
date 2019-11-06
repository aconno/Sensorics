package com.aconno.sensorics.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.model.toString
import com.aconno.sensorics.ui.settings.publishers.PublishRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_action.view.*

class ActionAdapter(
    private val actions: MutableList<Action>,
    private val clickListener: ItemClickListener<Action>
) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {
    var checkedChangeListener: OnCheckedChangeListener? = null

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
        notifyDataSetChanged()
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

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(action: Action) {
            view.text_name.text = action.name
            view.text_mac_address.text = action.device.macAddress
            view.text_condition.text = action.condition.toString(view.context)
            view.text_outcome.text = action.outcome.toString()
            view.action_switch.isChecked = action.active
            view.setOnClickListener { clickListener.onItemClick(action) }

            view.action_switch.setOnCheckedChangeListener { _, isChecked ->
                checkedChangeListener?.onCheckedChange(isChecked, action)
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(checked: Boolean, action: Action)
    }
}
