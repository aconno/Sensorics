package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Action
import com.aconno.acnsensa.model.toString
import kotlinx.android.synthetic.main.item_action.view.*

/**
 * @author aconno
 */
class ActionAdapter(
    private val actions: MutableList<Action>,
    private val clickListener: ItemClickListener<Action>
) : RecyclerView.Adapter<ActionAdapter.ViewHolder>() {

    fun setActions(actions: List<Action>) {
        this.actions.clear()
        this.actions.addAll(actions)
        notifyDataSetChanged()
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
            view.action_name.text = action.name
            view.text_mac_address.text = action.deviceMacAddress
            view.action_condition.text = action.condition.toString(view.context)
            view.setOnClickListener { clickListener.onItemClick(action) }
        }
    }
}
