package com.example.translatorapp.data.repository.source.word

import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.data.repository.source.DataSource

class WordRepository(
    private val wordDataSource: DataSource.WordDataSource
) : Repository.WordRepository {

    override fun translateSentence(
        text: String,
        from: String?,
        to: String,
        listener: OnResultListener<String>
    ) {
        wordDataSource.getTranslateSentence(text, from, to, listener)
    }

    override fun transliterate(
        text: String,
        language: Language,
        listener: OnResultListener<String>
    ) {
        wordDataSource.getTransliterateText(text, language, listener)
    }

    override fun breakSentence(text: String, listener: OnResultListener<List<String>>) {
        wordDataSource.getBreakSentence(text, listener)
    }

    override fun detectLang(text: String, listener: OnResultListener<String>) {
        wordDataSource.getDetectLang(text, listener)
    }

    override fun dictionaryLookup(
        text: String,
        from: String,
        to: String,
        listener: OnResultListener<MutableList<List<Any>>>
    ) {
        wordDataSource.getDictionaryLookup(text, from, to, listener)
    }

    companion object {
        private var instance: WordRepository? = null

        fun getInstance(wordDataSource: DataSource.WordDataSource) = synchronized(this) {
            instance ?: WordRepository(wordDataSource).also { instance = it }
        }
    }
}
