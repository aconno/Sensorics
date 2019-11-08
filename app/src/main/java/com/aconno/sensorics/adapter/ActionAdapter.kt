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
import kotlinx.android.synthetic.main.item_action.view.*
import java.util.*

class ActionAdapter(
    private val actions: MutableList<Action>,
    private val clickListener: ItemClickListener<Action>
) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
        notifyDataSetChanged()
    }

    fun getActions() : List<Action>{
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
        notifyItemRangeChanged(offset,newActions.size)
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(action: Action) {
            if (!action.active) {
                val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.context.resources.getColor(R.color.inactive_action_background_color, view.context.theme)
                } else {

                    view.context.resources.getColor(R.color.inactive_action_background_color)
                }
                view.constraint_layout_action.setBackgroundColor(color)
            } else {
                view.constraint_layout_action.setBackgroundColor(Color.TRANSPARENT) // TODO: If default changed change it
            }
            view.text_name.text = action.name
            view.text_mac_address.text = action.device.macAddress
            view.text_condition.text = action.condition.toString(view.context)
            view.text_outcome.text = action.outcome.toString()
            view.setOnClickListener { clickListener.onItemClick(action) }
        }
    }
}
