package com.example.translatorapp.data.repository

import com.example.translatorapp.data.model.Language

interface Repository {

    interface LanguageRepository {
        fun getLanguage(listener: OnResultListener<List<Language>>)
    }
}
