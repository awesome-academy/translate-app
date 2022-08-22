package com.example.translatorapp.data.repository

import android.content.Context
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.model.Language

interface Repository {

    interface LanguageRepository {
        fun getLanguage(listener: OnResultListener<Map<String, Language>>)
    }

    interface WordRepository {

        fun translateSentence(
            text: String,
            from: String?,
            to: String,
            listener: OnResultListener<String>
        )

        fun transliterate(text: String, language: Language, listener: OnResultListener<String>)
        fun breakSentence(text: String, listener: OnResultListener<List<String>>)
        fun detectLang(text: String, listener: OnResultListener<String>)
        fun dictionaryLookup(
            text: String,
            from: String,
            to: String,
            listener: OnResultListener<MutableList<List<Any>>>
        )
    }

    interface ExampleRepository {

        fun getExample(
            backTranslation: BackTranslation,
            from: String,
            to: String,
            listener: OnResultListener<List<Example>>
        )
    }

    interface HistoryRepository {

        fun getHistory(context: Context, listener: OnResultListener<List<History>>)
        fun writeHistory(context: Context, text: String, continueFlag: Boolean)
    }
}
