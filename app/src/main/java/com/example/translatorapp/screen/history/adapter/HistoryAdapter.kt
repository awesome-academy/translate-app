package com.example.translatorapp.screen.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.base.OnItemLongClickListener
import com.example.translatorapp.data.model.History
import com.example.translatorapp.databinding.HistoryItemBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val listHistory = mutableListOf<History>()
    private var clickListener: OnItemClickListener<History>? = null
    private var longClickListener: OnItemLongClickListener<History>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            HistoryItemBinding.inflate(layoutInflater, parent, false),
            clickListener,
            longClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listHistory[position])
    }

    override fun getItemCount() = listHistory.size

    fun updateData(list: List<History>) {
        listHistory.clear()
        listHistory.addAll(list)
        notifyDataSetChanged()
    }

    fun removeData(history: History) {
        listHistory.remove(history)
        notifyDataSetChanged()
    }

    fun registerClickListener(
        clickListener: OnItemClickListener<History>,
        longClickListener: OnItemLongClickListener<History>
    ) {
        this.clickListener = clickListener
        this.longClickListener = longClickListener
    }

    class ViewHolder(
        private val viewBinding: HistoryItemBinding,
        private val clickListener: OnItemClickListener<History>?,
        private val longClickListener: OnItemLongClickListener<History>?
    ) : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener, View.OnLongClickListener {

        private var history: History? = null

        init {
            viewBinding.root.setOnClickListener(this)
            viewBinding.root.setOnLongClickListener(this)
        }

        fun bindData(history: History) {
            viewBinding.textMean.text = history.meanWord
            viewBinding.textWord.text = history.sourceWord
            this.history = history
        }

        override fun onClick(p0: View?) {
            history?.let {
                clickListener?.onClick(it)
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            history?.let {
                longClickListener?.onLongClick(it)
            }
            return true
        }
    }
}
