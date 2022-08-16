package com.example.translatorapp.screen.translate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.data.model.DictionaryLookup
import com.example.translatorapp.databinding.MeanItemBinding
import com.example.translatorapp.databinding.TypeItemBinding

private const val TYPE = 0
private const val MEAN = 1

class MeanAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = mutableListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE -> ViewHolderType(TypeItemBinding.inflate(layoutInflater, parent, false))
            else -> ViewHolderMean(MeanItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE -> (holder as ViewHolderType).bindData(list[position].toString())
            else -> (holder as ViewHolderMean).bindData(list[position] as DictionaryLookup)
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        if (list[position] is String) {
            return TYPE
        }
        return MEAN
    }

    fun updatedData(list: List<Any>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolderMean(
        private val meanBind: MeanItemBinding
    ) : RecyclerView.ViewHolder(meanBind.root) {

        fun bindData(lookup: DictionaryLookup) {
            meanBind.textWord.text = lookup.sourceWord.displayText
            meanBind.textMean.text = lookup.targetWord.displayText
        }
    }

    class ViewHolderType(
        private val typeBind: TypeItemBinding
    ) : RecyclerView.ViewHolder(typeBind.root) {

        fun bindData(text: String) {
            typeBind.textType.text = text
        }
    }
}
