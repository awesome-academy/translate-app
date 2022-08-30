package com.example.translatorapp.screen.test

import android.content.Context
import androidx.annotation.StringRes
import com.example.translatorapp.data.model.Exam

class TestContract {

    interface View {
        fun onGetQuestionComplete(data: List<Exam>)
        fun onError(@StringRes message: Int)
    }

    interface Presenter {
        fun getExam(context: Context)
    }
}
