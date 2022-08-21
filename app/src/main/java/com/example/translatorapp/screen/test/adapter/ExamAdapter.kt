package com.example.translatorapp.screen.test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.databinding.TestItemBinding

class ExamAdapter : RecyclerView.Adapter<ExamAdapter.ViewHolder>() {

    private val listExam = mutableListOf<Exam>()
    private var listener: OnItemClickListener<Exam>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(TestItemBinding.inflate(layoutInflater, parent, false), listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listExam[position])
    }

    override fun getItemCount() = listExam.size

    fun updateData(list: List<Exam>) {
        listExam.clear()
        listExam.addAll(list)
        notifyDataSetChanged()
    }

    fun regisListener(listener: OnItemClickListener<Exam>) {
        this.listener = listener
    }

    class ViewHolder(
        private val viewBinding: TestItemBinding,
        private val listener: OnItemClickListener<Exam>?
    ) : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

        private var data: Exam? = null

        init {
            listener?.let {
                viewBinding.root.setOnClickListener(this)
            }
        }

        fun bindData(exam: Exam) {
            data = exam
            viewBinding.textTitleTest.text = exam.name
        }

        override fun onClick(p0: View?) {
            data?.let {
                listener?.onClick(it)
            }
        }
    }
}
