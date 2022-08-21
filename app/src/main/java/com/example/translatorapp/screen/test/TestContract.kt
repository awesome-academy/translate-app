package com.example.translatorapp.screen.test

import android.content.Context
import com.example.translatorapp.data.model.Exam

class TestContract {

    interface View {
        fun onGetQuestionComplete(data: List<Exam>)
    }

    interface Presenter {
        fun getExam(context: Context)
    }
}
