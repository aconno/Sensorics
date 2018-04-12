package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ifttt.Action

/**
 * @author aconno
 */
class ActionAdapter(
    private val actions: MutableList<Action>,
    private val clickListener: ItemClickListener<Action>
) :
    RecyclerView.Adapter<ActionAdapter.ActionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_action, parent, false)
        return ActionHolder(view)
    }

    override fun getItemCount(): Int {
        return actions.size
    }

    override fun onBindViewHolder(holder: ActionHolder, position: Int) {
        holder.bind(actions[position])
    }

    inner class ActionHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var action: Action? = null
        private val actionName: TextView = itemView.findViewById(R.id.action_name)

        init {
            itemView.setOnClickListener { action?.let { clickListener.onItemClick(it) } }
        }

        fun bind(action: Action) {
            this.action = action
            actionName.text = action.name
        }
    }
}
