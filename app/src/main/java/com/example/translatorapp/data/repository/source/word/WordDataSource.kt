package com.example.translatorapp.data.repository.source.word

import com.example.translatorapp.constant.BREAK_URL
import com.example.translatorapp.constant.DETECT_URL
import com.example.translatorapp.constant.LOOKUP_URL
import com.example.translatorapp.constant.TRANSLATE_URL
import com.example.translatorapp.constant.TRANSLITERATE_URL
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.DictionaryLookup
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.model.Word
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.source.DataSource
import com.example.translatorapp.data.repository.source.api.postJson
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val SENT_LENGTH = "sentLen"
private const val TEXT_TITLE = "text"
private const val LANGUAGE = "language"
private const val POS_TAG = "posTag"
private const val NORMALIZED_SOURCE = "normalizedSource"
private const val DISPLAY_SOURCE = "displaySource"
private const val NORMALIZED_TARGET = "normalizedTarget"
private const val DISPLAY_TARGET = "displayTarget"
private const val BACK_TRANSLATIONS = "backTranslations"
private const val NORMALIZED_TEXT = "normalizedText"
private const val DISPLAY_TEXT = "displayText"
private const val NUM_EXAMPLES = "numExamples"
private const val TRANSLATIONS = "translations"
private const val KEEP_ALIVE_TIME: Long = 60

class WordDataSource : DataSource.WordDataSource {

    override fun getTranslateSentence(
        text: String,
        from: String?,
        to: String,
        listener: OnResultListener<String>
    ) {
        var url = "$TRANSLATE_URL&to=$to"
        if (from != null) {
            url = "$url&from=$from"
        }
        val jArr = JSONArray()
        jArr.put(JSONObject().put(TEXT_TITLE, text))
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(url, jArr.toString()))
            val translations = response.getJSONObject(0).getJSONArray(TRANSLATIONS)
            val translateText = translations.getJSONObject(0).getString(TEXT_TITLE)
            listener.onSuccess(translateText)
        }
        threadPoolExecutor.shutdown()
    }

    override fun getTransliterateText(
        text: String,
        language: Language,
        listener: OnResultListener<String>
    ) {
        val url =
            "$TRANSLITERATE_URL&language=${language.code}&fromScript=" +
                "${language.transliterateScript?.first}&toScript=" +
                "${language.transliterateScript?.second}"
        val jArr = JSONArray().put(JSONObject().put(TEXT_TITLE, text))
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(url, jArr.toString()))
            listener.onSuccess(response.getJSONObject(0).getString(TEXT_TITLE))
        }
        threadPoolExecutor.shutdown()
    }

    override fun getBreakSentence(text: String, listener: OnResultListener<List<String>>) {
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        val jArr = JSONArray().put(JSONObject().put(TEXT_TITLE, text))
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(BREAK_URL, jArr.toString()))
            val senLengths = response.getJSONObject(0).getJSONArray(SENT_LENGTH)
            val length = senLengths.length() - 1
            val list = mutableListOf<String>()
            var index = 0
            for (value: Int in 0..length) {
                val indx = senLengths.getInt(value)
                list.add(text.substring(index, index + indx))
                index += indx
            }
            listener.onSuccess(list)
        }
        threadPoolExecutor.shutdown()
    }

    override fun getDetectLang(text: String, listener: OnResultListener<String>) {
        val jArr = JSONArray().put(JSONObject().put(TEXT_TITLE, text))
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(DETECT_URL, jArr.toString()))
            listener.onSuccess(response.getJSONObject(0).getString(LANGUAGE))
        }
        threadPoolExecutor.shutdown()
    }

    override fun getDictionaryLookup(
        text: String,
        from: String,
        to: String,
        listener: OnResultListener<MutableList<List<Any>>>
    ) {
        val url = "$LOOKUP_URL&to=$to&from=$from"
        val jArr = JSONArray().put(JSONObject().put(TEXT_TITLE, text))
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(url, jArr.toString())).getJSONObject(0)
            val translations = response.getJSONArray(TRANSLATIONS)
            val sourceWord = Word(
                response.getString(NORMALIZED_SOURCE),
                response.getString(
                    DISPLAY_SOURCE
                )
            )
            val length = translations.length() - 1
            val listDicLookup = mutableListOf<DictionaryLookup>()
            val listBackTranslation = mutableListOf<BackTranslation>()
            for (index in 0..length) {
                val tran = translations.getJSONObject(index)
                val targetWord = Word(
                    tran.getString(NORMALIZED_TARGET),
                    tran.getString(
                        DISPLAY_TARGET
                    )
                )
                listDicLookup.add(DictionaryLookup(sourceWord, targetWord, tran.getString(POS_TAG)))
                val backTranslation = tran.getJSONArray(BACK_TRANSLATIONS)
                listBackTranslation.addAll(getBackTranslation(backTranslation, targetWord))
            }
            listDicLookup.sortBy { it.posTag }
            val listMean = mutableListOf<Any>()
            for (value in listDicLookup) {
                if (value.posTag !in listMean) {
                    listMean.add(value.posTag)
                }
                listMean.add(value)
            }
            listener.onSuccess(mutableListOf(listMean, listBackTranslation))
        }
        threadPoolExecutor.shutdown()
    }

    private fun getBackTranslation(
        backTranslations: JSONArray,
        target: Word
    ): MutableList<BackTranslation> {
        val listBackTranslation = mutableListOf<BackTranslation>()
        val length = backTranslations.length() - 1
        for (index in 0..length) {
            val backTran = backTranslations.getJSONObject(index)
            val sampleWord = Word(
                backTran.getString(NORMALIZED_TEXT),
                backTran.getString(
                    DISPLAY_TEXT
                )
            )
            var haveExample = false
            if (backTran.getString(NUM_EXAMPLES).toInt() > 0) {
                haveExample = true
            }
            val back = BackTranslation(sampleWord, target, haveExample)
            listBackTranslation.add(back)
        }
        return listBackTranslation
    }

    companion object {
        private var instance: WordDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: WordDataSource().also { instance = it }
        }
    }
}
