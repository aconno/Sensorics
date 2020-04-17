package com.aconno.sensorics.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class SelectableRecyclerViewAdapter<T, K, VH>(
    protected val internalItems: MutableList<T>,
    private val itemSelectedListener: ItemSelectedListener<T>? = null,
    private val clickListener: ItemClickListener<T>? = null,
    private val longClickListener: ItemLongClickListener<T>? = null
) : RecyclerView.Adapter<VH>() where VH : SelectableRecyclerViewAdapter<T, K, VH>.ViewHolder {
    private var itemSelectedMap: MutableMap<K, Boolean> = mutableMapOf()
    var isItemSelectionEnabled = false
        private set

    abstract fun getKeyForItem(item: T): K

    fun setItems(items: List<T>) {
        this.internalItems.clear()
        this.internalItems.addAll(items)
        notifyDataSetChanged()
    }

    fun addItems(items: List<T>) {
        val offset = this.internalItems.size
        this.internalItems.addAll(items)
        notifyItemRangeChanged(offset, items.size)
    }

    fun getItems(): List<T> {
        return Collections.unmodifiableList(this.internalItems)
    }

    override fun getItemCount(): Int {
        return this.internalItems.size
    }

    fun getItem(position: Int): T {
        return this.internalItems[position]
    }

    fun addItemAtPosition(item: T, position: Int) {
        this.internalItems.add(position, item)
        notifyItemInserted(position)
    }

    fun removeItemAtPosition(position: Int) {
        this.internalItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    fun getNumberOfSelectedItems(): Int {
        return itemSelectedMap.filter { entry -> entry.value }.size
    }

    fun getSelectedItems(): List<T> {
        return internalItems.filter {
            itemSelectedMap[getKeyForItem(it)] ?: false
        }
    }

    fun enableItemSelection(initiallySelectedItem: T? = null) {
        isItemSelectionEnabled = true
        itemSelectedMap.clear()
        initiallySelectedItem?.let {
            itemSelectedMap[getKeyForItem(it)] = true
        }
        notifyDataSetChanged()
    }

    fun disableItemSelection() {
        isItemSelectionEnabled = false
        itemSelectedMap.clear()
        notifyDataSetChanged()
    }

    fun isItemSelected(item: T): Boolean {
        return itemSelectedMap[getKeyForItem(item)] ?: false
    }

    fun setItemsAsSelected(items: Collection<T>) {
        items.forEach { item ->
            onItemSelectionStateChanged(item, true)
        }
    }

    private fun onItemSelectionStateChanged(item: T, selected: Boolean) {
        itemSelectedMap[getKeyForItem(item)] = selected

//        notifyItemChanged(internalItems.indexOf(item))
        itemSelectedListener?.apply {
            itemSelectedListener.onListItemSelectionStateChanged(item, selected)
        }
        notifyDataSetChanged()
    }


    interface ItemSelectedListener<in T> {
        fun onListItemSelectionStateChanged(item: T, state: Boolean)
    }

    interface ItemClickListener<in T> {
        fun onItemClick(item: T)
    }

    interface ItemLongClickListener<in T> {
        fun onItemLongClick(item: T)
    }

    abstract inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)

        fun setSelected(item: T, selected: Boolean) {
            onItemSelectionStateChanged(item, selected)
        }

        fun onItemClick(item: T) {
            clickListener?.onItemClick(item)
        }

        fun onItemLongClick(item: T) {
            longClickListener?.onItemLongClick(item)
        }
    }
}