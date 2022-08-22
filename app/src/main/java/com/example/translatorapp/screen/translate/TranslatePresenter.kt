package com.example.translatorapp.screen.translate

import android.content.Context
import com.example.translatorapp.data.model.DictionaryLookup
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class TranslatePresenter(
    private val languageRepository: Repository.LanguageRepository,
    private val wordRepository: Repository.WordRepository,
    private val historyRepository: Repository.HistoryRepository
) : TranslateContract.Presenter {

    private var view: TranslateContract.View? = null
    private val dictionary by lazy { Dictionary() }
    val mapLanguage: MutableMap<String, Language> = mutableMapOf()
    val listLanguage: MutableList<Language> = mutableListOf()
    var sourceLang: Language? = null
    var targetLang: Language? = null
    var sourceLangTemp: Language? = null

    override fun getLanguage() {
        languageRepository.getLanguage(object : OnResultListener<Map<String, Language>> {
            override fun onSuccess(data: Map<String, Language>) {
                synchronized(this) {
                    listLanguage.clear()
                    mapLanguage.clear()
                    mapLanguage.putAll(data)
                    listLanguage.addAll(data.toList().map { it.second })
                    listLanguage.sortBy { it.name }
                    view?.onGetLanguageSuccess(listLanguage)
                }
            }
        })
    }

    override fun getTranslateSentence(text: String) {
        targetLang?.let {
            wordRepository.translateSentence(
                text,
                sourceLang?.code,
                it.code,
                object : OnResultListener<String> {
                    override fun onSuccess(data: String) {
                        var res = data
                        if (it.isTransliterate) {
                            wordRepository.transliterate(
                                data,
                                it,
                                object : OnResultListener<String> {
                                    override fun onSuccess(data: String) {
                                        res = "$res\n$data"
                                        view?.onTranslateSentenceComplete(res)
                                    }
                                }
                            )
                        } else {
                            view?.onTranslateSentenceComplete(res)
                        }
                    }
                }
            )
        }
    }

    override fun breakSentence(text: String) {
        wordRepository.breakSentence(
            text,
            object : OnResultListener<List<String>> {
                override fun onSuccess(data: List<String>) {
                    view?.onBreakSentenceComplete(data)
                }
            }
        )
    }

    override fun getTranslateWord(text: String) {
        targetLang?.let { target ->
            sourceLang?.let {
                if (dictionary.checkDictionarySupport(it.code, target.code)) {
                    dictionary.dictionaryLookup(text, it.code, target.code)
                } else {
                    getTranslateSentence(text)
                }
            }
            if (sourceLang == null) {
                dictionary.getAutoDictionaryLookup(text, target)
            }
        }
    }

    override fun writeHistory(context: Context, text: String, continueFlag: Boolean) {
        historyRepository.writeHistory(context, text, continueFlag)
    }

    private fun transliterateLookup(data: MutableList<List<Any>>, language: Language) {
        val response = data
        for (value in data[0]) {
            if (value is DictionaryLookup) {
                val mean = value.targetWord.displayText
                if (language.isTransliterate) {
                    wordRepository.transliterate(
                        mean,
                        language,
                        object : OnResultListener<String> {
                            override fun onSuccess(data: String) {
                                val res = "$mean\n$data"
                                response.add(listOf(res))
                                view?.onDictionaryLookupComplete(response)
                            }
                        }
                    )
                } else {
                    response.add(listOf(mean))
                    view?.onDictionaryLookupComplete(response)
                }
                break
            }
        }
    }

    fun getTranslate(text: String) {
        if (text.contains(" ")) {
            getTranslateSentence(text)
            breakSentence(text)
        } else {
            getTranslateWord(text)
        }
    }

    fun setView(view: TranslateContract.View) {
        this.view = view
    }

    private inner class Dictionary {

        fun getAutoDictionaryLookup(text: String, target: Language) {
            wordRepository.detectLang(
                text,
                object : OnResultListener<String> {
                    override fun onSuccess(data: String) {
                        sourceLangTemp = mapLanguage[data]
                        if (checkDictionarySupport(data, target.code)) {
                            dictionaryLookup(text, data, target.code)
                        } else {
                            getTranslateSentence(text)
                        }
                    }
                }
            )
        }

        fun checkDictionarySupport(sourceCode: String, targetCode: String): Boolean {
            val language = mapLanguage[sourceCode]
            if (language?.isSupportDictionary == true) {
                val list = language.dictionaryScript
                for (value in list) {
                    if (value == targetCode) {
                        return true
                    }
                }
            }
            return false
        }

        fun dictionaryLookup(text: String, from: String, to: String) {
            if (from.isNotEmpty()) {
                wordRepository.dictionaryLookup(
                    text,
                    from,
                    to,
                    object : OnResultListener<MutableList<List<Any>>> {
                        override fun onSuccess(data: MutableList<List<Any>>) {
                            mapLanguage[to]?.let {
                                transliterateLookup(data, it)
                            }
                        }
                    }
                )
            }
        }
    }

    companion object {
        private var instance: TranslatePresenter? = null

        fun getInstance(
            languageRepository: Repository.LanguageRepository,
            wordRepository: Repository.WordRepository,
            historyRepository: Repository.HistoryRepository
        ): TranslatePresenter = synchronized(this) {
            instance ?: TranslatePresenter(
                languageRepository,
                wordRepository,
                historyRepository
            ).also {
                instance = it
            }
        }
    }
}
