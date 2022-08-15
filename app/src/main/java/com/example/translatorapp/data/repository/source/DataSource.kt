package com.example.translatorapp.data.repository.source

import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener

interface DataSource {

    interface LanguageDataSource {
        fun getLanguage(listener: OnResultListener<List<Language>>)
    }
}
