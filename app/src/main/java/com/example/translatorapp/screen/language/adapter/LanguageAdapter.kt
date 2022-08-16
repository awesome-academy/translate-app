package com.example.translatorapp.screen.language.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.databinding.LanguageItemBinding

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private val listLanguage = mutableListOf<Language>()
    private var clickListener: OnItemClickListener<Language>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(LanguageItemBinding.inflate(inflater, parent, false), clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listLanguage[position])
    }

    override fun getItemCount() = listLanguage.size

    fun updateData(languages: List<Language>) {
        listLanguage.clear()
        listLanguage.addAll(languages)
        notifyDataSetChanged()
    }

    fun registerListener(listener: OnItemClickListener<Language>) {
        clickListener = listener
    }

    class ViewHolder(
        private val viewBinding: LanguageItemBinding,
        private val listener: OnItemClickListener<Language>?
    ) : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

        private var data: Language? = null

        init {
            viewBinding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            data?.let {
                listener?.onClick(it)
            }
        }

        fun bindData(language: Language) {
            language.let {
                data = language
                viewBinding.textName.text = it.getFullName()
            }
        }
    }
}
