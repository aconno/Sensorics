package com.aconno.acnsensa.ui.settings.rheader

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aconno.acnsensa.R
import com.aconno.acnsensa.model.RESTHeaderModel
import com.aconno.acnsensa.adapter.LongItemClickListener


class RESTHeadersAdapter(
    private val list: List<RESTHeaderModel>,
    private val onItemClickListener: ItemClickListenerWithPos<RESTHeaderModel>,
    private val mLongItemClickListener: LongItemClickListener<RESTHeaderModel>?
) : RecyclerView.Adapter<RESTHeadersAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val mTextView: TextView = mView.findViewById(android.R.id.text1)
    }

    private val onClickListener = View.OnClickListener {
        if (it.tag != null) {
            val model = (it.tag as Item).model
            val position = (it.tag as Item).position
            onItemClickListener.onItemClick(position, model)
        } else {
            onItemClickListener.onItemClick(-1, null)
        }
    }

    private val mOnLongClickListener = View.OnLongClickListener {
        val item = it.tag as Item
        mLongItemClickListener?.onLongClick(item.model)
        true
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.simple_list_item, parent, false)

        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]

        holder.mTextView.text = item.key

        with(holder.itemView) {
            tag = Item(item, position)
            setOnClickListener(onClickListener)
            setOnLongClickListener(mOnLongClickListener)
        }
    }

    inner class Item(val model: RESTHeaderModel, val position: Int)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 0 else 1
    }
}