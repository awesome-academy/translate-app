package com.example.translatorapp.screen.test

import android.content.Context
import com.example.translatorapp.base.BasePresenter
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class TestPresenter(
    private val repository: Repository.ExamRepository
) : TestContract.Presenter, BasePresenter<TestContract.View> {

    private var view: TestContract.View? = null

    override fun getExam(context: Context) {
        repository.getExam(
            context,
            object : OnResultListener<List<Exam>> {
                override fun onSuccess(data: List<Exam>) {
                    view?.onGetQuestionComplete(data)
                }

                override fun onError(message: Int) {
                    view?.onError(message)
                }
            }
        )
    }

    override fun onStart() {
        // No-op
    }

    override fun onStop() {
        // No-op
    }

    override fun setView(view: TestContract.View) {
        this.view = view
    }

    companion object {
        private var instance: TestPresenter? = null

        fun getInstance(examRepository: Repository.ExamRepository) =
            synchronized(this) {
                instance ?: TestPresenter(examRepository).also { instance = it }
            }
    }
}
