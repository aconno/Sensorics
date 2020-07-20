package com.aconno.sensorics.adapter

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aconno.sensorics.R

class BuyBeaconsOptionsAdapter(val options : List<BuyOption>, val clickListener : OptionClickListener) : RecyclerView.Adapter<BuyBeaconsOptionsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buy_option,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val buyOption = options[position]
        holder.logo.setImageResource(buyOption.logo)
        holder.optionName.text = buyOption.optionName

        holder.itemView.setOnClickListener {
            clickListener.onBuyOptionClicked(buyOption)
        }
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val logo : ImageView = itemView.findViewById(R.id.buy_option_logo)
        val optionName : TextView = itemView.findViewById(R.id.buy_option_name)
    }

    class BuyOption(val logo : Int, val optionName : String, val optionUrl : String)

    interface OptionClickListener {
        fun onBuyOptionClicked(buyOption: BuyOption)
    }
}