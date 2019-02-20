package com.aconno.sensorics.ui.logs

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import com.aconno.sensorics.R

internal class LoggingAdapter : RecyclerView.Adapter<LoggingAdapter.ViewHolder>() {

    private val logList = arrayListOf<Pair<String, LoggingLevel>>()
    private val deletedLogs = arrayListOf<Pair<String, LoggingLevel>>()
    private var onSelectionChangedListener: OnSelectionChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent,
                false) as LogTextView
        val loggingLevel = getLoggingLevel(viewType)
        return ViewHolder(view, loggingLevel)
    }

    override fun getItemViewType(position: Int): Int {
        return logList[position].second.code
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = logList[position].first
        val textColor = getTextColor(holder.loggingLevel)
        holder.textView.setTextColor(ContextCompat.getColor(holder.textView.context, textColor))
        holder.textView.setOnSelectionChangedListener(object : LogTextView.OnSelectionChangedListener {
            override fun onSelectionChanged() {
                onSelectionChangedListener?.onSelectionChanged()
            }
        })
    }

    fun addLog(log: String, loggingLevel: LoggingLevel) {
        logList.add(Pair(log, loggingLevel))
        notifyItemInserted(logList.size - 1)
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

    @ColorRes
    private fun getTextColor(loggingLevel: LoggingLevel): Int {
        return when (loggingLevel) {
            LoggingLevel.INFO -> R.color.logging_info
            LoggingLevel.ERROR -> R.color.logging_error
            LoggingLevel.WARNING -> R.color.logging_warning
        }
    }

    private fun getLoggingLevel(loggingLevel: Int): LoggingLevel {
        return when (loggingLevel) {
            LoggingLevel.INFO.code -> LoggingLevel.INFO
            LoggingLevel.WARNING.code -> LoggingLevel.WARNING
            LoggingLevel.ERROR.code -> LoggingLevel.ERROR
            else -> LoggingLevel.INFO
        }
    }

    internal class ViewHolder(val textView: LogTextView, val loggingLevel: LoggingLevel) :
            RecyclerView.ViewHolder(textView)

    internal interface OnSelectionChangedListener {
        fun onSelectionChanged()
    }

    internal enum class LoggingLevel(val code: Int) {
        INFO(1),
        ERROR(2),
        WARNING(3)
    }
}