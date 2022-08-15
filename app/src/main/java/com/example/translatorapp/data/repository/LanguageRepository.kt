package com.example.translatorapp.data.repository

import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.source.DataSource

class LanguageRepository(
    private val languageDataSource: DataSource.LanguageDataSource
) : Repository.LanguageRepository {

    override fun getLanguage(listener: OnResultListener<Map<String, Language>>) {
        languageDataSource.getLanguage(listener)
    }

    companion object {
        private var instance: LanguageRepository? = null

        fun getInstance(languageDataSource: DataSource.LanguageDataSource) = synchronized(this) {
            instance ?: LanguageRepository(languageDataSource).also {
                instance = it
            }
        }
    }
}
