package com.example.translatorapp.data.repository.source.question

import android.content.Context
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.data.repository.source.DataSource

class ExamRepository(
    private val examDataSource: DataSource.ExamDataSource
) : Repository.ExamRepository {

    override fun getExam(context: Context, listener: OnResultListener<List<Exam>>) {
        examDataSource.getExam(context, listener)
    }

    companion object {
        private var instance: ExamRepository? = null

        fun getInstance(examDataSource: DataSource.ExamDataSource) = synchronized(this) {
            instance ?: ExamRepository(examDataSource).also { instance = it }
        }
    }
}
