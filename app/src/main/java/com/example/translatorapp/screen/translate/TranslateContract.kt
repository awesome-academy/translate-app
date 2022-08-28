package com.example.translatorapp.screen.translate

import android.content.Context
import androidx.annotation.StringRes
import com.example.translatorapp.data.model.Language

class TranslateContract {

    interface View {
        fun onGetLanguageSuccess(data: List<Language>)
        fun onTranslateSentenceComplete(data: String)
        fun onBreakSentenceComplete(data: List<String>)
        fun onDictionaryLookupComplete(data: List<List<Any>>)
        fun onError(@StringRes message: Int)
    }

    interface Presenter {
        fun getLanguage()
        fun getTranslateSentence(text: String)
        fun breakSentence(text: String)
        fun getTranslateWord(text: String)
        fun writeHistory(context: Context, text: String, continueFlag: Boolean)
    }
}
