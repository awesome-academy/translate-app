package com.example.translatorapp.screen.translate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.R
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.databinding.SentenceItemBinding

class WordAdapter : RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    private val list = mutableListOf<Any>()
    private var listener: OnItemClickListener<BackTranslation>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = SentenceItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(viewBinding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }

    override fun getItemCount() = list.size

    fun updateData(list: List<Any>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun registerListener(listener: OnItemClickListener<BackTranslation>) {
        this.listener = listener
    }

    class ViewHolder(
        private val viewBinding: SentenceItemBinding,
        private val listener: OnItemClickListener<BackTranslation>?
    ) : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

        private var data: BackTranslation? = null

        init {
            listener?.let {
                viewBinding.root.setOnClickListener(this)
            }
        }

        override fun onClick(p0: View?) {
            data?.let {
                listener?.onClick(it)
            }
        }

        fun bindData(obj: Any, position: Int) {
            if (obj is String) {
                viewBinding.textShowWord.text = obj
            } else if (obj is BackTranslation) {
                data = obj
                viewBinding.textShowWord.text = obj.sampleWord.displayText
            }
            viewBinding.textShowPosition.text =
                viewBinding.root.context.getString(R.string.position, position + 1)
        }
    }
}
