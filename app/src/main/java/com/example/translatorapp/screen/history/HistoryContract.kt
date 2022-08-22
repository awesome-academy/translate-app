package com.example.translatorapp.screen.history

import android.content.Context
import com.example.translatorapp.data.model.History

class HistoryContract {

    interface View {
        fun onGetHistoryComplete(data: List<History>)
    }

    interface Presenter {
        fun getHistory(context: Context)
        fun writeHistory(context: Context, text: String, continueFlag: Boolean)
    }
}
