package com.aconno.sensorics.ui.logs

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aconno.sensorics.R
import com.aconno.sensorics.model.LogModel

internal class LoggingAdapter : RecyclerView.Adapter<LoggingAdapter.ViewHolder>() {

    private val logList = arrayListOf<LogModel>()
    private val deletedLogs = arrayListOf<LogModel>()
    private var onSelectionChangedListener: OnSelectionChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent,
                false) as LogTextView
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(logList[position], onSelectionChangedListener)
    }

    fun clear() {
        deletedLogs.addAll(logList)
        logList.clear()
        notifyDataSetChanged()
    }

    fun undoClear() {
        logList.addAll(0, deletedLogs)
        deletedLogs.clear()
        notifyDataSetChanged()
    }

    fun setOnSelectionChangedListener(onSelectionChangedListener: OnSelectionChangedListener) {
        this.onSelectionChangedListener = onSelectionChangedListener
    }

    fun refreshLogs(logs: List<LogModel>) {
        logList.clear()
        logList.addAll(logs.minus(deletedLogs))
        notifyDataSetChanged()
    }

    internal class ViewHolder(private val textView: LogTextView) :
            RecyclerView.ViewHolder(textView) {
        fun bind(logModel: LogModel, onSelectionChangedListener: OnSelectionChangedListener?) {
            textView.text = logModel.formattedInfo
            textView.setTextColor(ContextCompat.getColor(itemView.context, logModel.colorResId))
            textView.setOnSelectionChangedListener(object : LogTextView.OnSelectionChangedListener {
                override fun onSelectionChanged() {
                    onSelectionChangedListener?.onSelectionChanged()
                }
            })
        }
    }

    internal interface OnSelectionChangedListener {
        fun onSelectionChanged()
    }
}