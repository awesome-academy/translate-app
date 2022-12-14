package com.example.translatorapp.data.repository.source

import android.content.Context
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Exam
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener

interface DataSource {

    interface LanguageDataSource {
        fun getLanguage(listener: OnResultListener<Map<String, Language>>)
    }

    interface WordDataSource {

        fun getTranslateSentence(
            text: String,
            from: String?,
            to: String,
            listener: OnResultListener<String>
        )

        fun getTransliterateText(
            text: String,
            language: Language,
            listener: OnResultListener<String>
        )

        fun getBreakSentence(text: String, listener: OnResultListener<List<String>>)
        fun getDetectLang(text: String, listener: OnResultListener<String>)
        fun getDictionaryLookup(
            text: String,
            from: String,
            to: String,
            listener: OnResultListener<MutableList<List<Any>>>
        )
    }

    interface ExampleDataSource {

        fun getExample(
            backTranslation: BackTranslation,
            to: String,
            from: String,
            listener: OnResultListener<List<Example>>
        )
    }

    interface HistoryDataSource {

        fun getHistory(context: Context, listener: OnResultListener<List<History>>)
        fun writeHistory(context: Context, text: String, continueFlag: Boolean)
    }

    interface ExamDataSource {

        fun getExam(context: Context, listener: OnResultListener<List<Exam>>)
    }
}
