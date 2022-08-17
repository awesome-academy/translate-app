package com.example.translatorapp.data.repository.source

import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
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
}
