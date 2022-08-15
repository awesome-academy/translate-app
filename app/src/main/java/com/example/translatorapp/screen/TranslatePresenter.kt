package com.example.translatorapp.screen

import android.util.Log
import com.example.translatorapp.data.model.DictionaryLookup
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class TranslatePresenter(
    private val languageRepository: Repository.LanguageRepository,
    private val wordRepository: Repository.WordRepository
) : TranslateContract.Presenter {

    private var mView: TranslateContract.View? = null
    private val mapLanguage: MutableMap<String, Language> = mutableMapOf()
    val listLanguage: MutableList<Language> = mutableListOf()
    var source: Language? = null
    var target: Language? = null

    override fun getLanguage() {
        languageRepository.getLanguage(object : OnResultListener<Map<String, Language>> {
            override fun onSuccess(data: Map<String, Language>) {
                listLanguage.clear()
                mapLanguage.clear()
                mapLanguage.putAll(data)
                listLanguage.addAll(data.toList().map { it.second })
                listLanguage.sortBy { it.name }
                mView?.onGetLanguageSuccess(listLanguage)
            }
        })
    }

    override fun getTranslateSentence(text: String) {
        target?.let {
            wordRepository.translateSentence(
                text,
                source?.code,
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
                                        mView?.onTranslateSentenceComplete(res)
                                    }
                                }
                            )
                        } else {
                            mView?.onTranslateSentenceComplete(res)
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
                    mView?.onBreakSentenceComplete(data)
                }
            }
        )
    }

    override fun getTranslateWord(text: String) {
        target?.let { target ->
            source?.let {
                if (checkDictionarySupport(it.code, target.code)) {
                    dictionaryLookup(text, it.code, target.code)
                } else {
                    getTranslateSentence(text)
                }
            }
            if (source == null) {
                getAutoDictionaryLookup(text, target)
            }
        }
    }

    private fun getAutoDictionaryLookup(text: String, target: Language) {
        wordRepository.detectLang(
            text,
            object : OnResultListener<String> {
                override fun onSuccess(data: String) {
                    if (checkDictionarySupport(data, target.code)) {
                        dictionaryLookup(text, data, target.code)
                    } else {
                        getTranslateSentence(text)
                    }
                }
            }
        )
    }

    private fun checkDictionarySupport(sourceCode: String, targetCode: String): Boolean {
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

    private fun dictionaryLookup(text: String, from: String, to: String) {
        if (from != "") {
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

    private fun transliterateLookup(data: MutableList<List<Any>>, language: Language) {
        val response = data
        for (value in data[0]) {
            if (value is DictionaryLookup) {
                val mean = value.targetWord.displayText
                if (language.isTransliterate) {
                    Log.v("t111", "yes")
                    wordRepository.transliterate(
                        mean,
                        language,
                        object : OnResultListener<String> {
                            override fun onSuccess(data: String) {
                                val res = "$mean\n$data"
                                response.add(listOf(res))
                                mView?.onDictionaryLookupComplete(response)
                            }
                        }
                    )
                } else {
                    response.add(listOf(mean))
                    mView?.onDictionaryLookupComplete(response)
                }
                break
            }
        }
    }

    fun setView(view: TranslateContract.View) {
        mView = view
    }

    companion object {
        private var instance: TranslatePresenter? = null

        fun getInstance(
            languageRepository: Repository.LanguageRepository,
            wordRepository: Repository.WordRepository
        ): TranslatePresenter = synchronized(this) {
            instance ?: TranslatePresenter(languageRepository, wordRepository).also {
                instance = it
            }
        }
    }
}
