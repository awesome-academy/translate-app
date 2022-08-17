package com.example.translatorapp.screen.example.adapter

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.databinding.ExampleItemBinding

class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {

    private val listExample = mutableListOf<Example>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(ExampleItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listExample[position])
    }

    override fun getItemCount() = listExample.size

    fun updateData(list: List<Example>) {
        listExample.clear()
        listExample.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val viewBinding: ExampleItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bindData(example: Example) {
            val sourceSpan =
                SpannableString(example.sourcePrefix + example.sourceTerm + example.sourceSuffix)
            val targetSpan =
                SpannableString(example.targetPrefix + example.targetTerm + example.targetSuffix)
            val style = StyleSpan(Typeface.BOLD)
            sourceSpan.setSpan(
                style,
                example.sourcePrefix.length,
                example.sourcePrefix.length + example.sourceTerm.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            targetSpan.setSpan(
                style,
                example.targetPrefix.length,
                example.targetPrefix.length + example.targetTerm.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            viewBinding.textSource.text = sourceSpan
            viewBinding.textTarget.text = targetSpan
        }
    }
}
