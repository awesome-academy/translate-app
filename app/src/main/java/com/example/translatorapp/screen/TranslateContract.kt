package com.example.translatorapp.screen

import com.example.translatorapp.data.model.Language

class TranslateContract {

    interface View {
        fun onGetLanguageSuccess(data: List<Language>)
        fun onTranslateSentenceComplete(data: String)
        fun onBreakSentenceComplete(data: List<String>)
        fun onDictionaryLookupComplete(data: List<List<Any>>)
    }

    interface Presenter {
        fun getLanguage()
        fun getTranslateSentence(text: String)
        fun breakSentence(text: String)
        fun getTranslateWord(text: String)
    }
}
