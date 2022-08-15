package com.example.translatorapp.data.repository.source.language

import com.example.translatorapp.constant.LANGUAGE_URL
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.source.DataSource
import com.example.translatorapp.data.repository.source.api.getJson
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TRANSLATION = "translation"
private const val TRANSLATIONS = "translations"
private const val NAME = "name"
private const val NATIVE_NAME = "nativeName"
private const val TRANSLITERATION = "transliteration"
private const val DICTIONARY = "dictionary"
private const val SCRIPT = "scripts"
private const val CODE = "code"
private const val TO_SCRIPT = "toScripts"
private const val KEEP_ALIVE_TIME: Long = 60

class LanguageDataSource : DataSource.LanguageDataSource {

    override fun getLanguage(listener: OnResultListener<Map<String, Language>>) {
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONObject(getJson(LANGUAGE_URL))
            val map = mutableMapOf<String, Language>()
            addLanguage(map, response.getJSONObject(TRANSLATION))
            addTransliteration(map, response.getJSONObject(TRANSLITERATION))
            addDictionary(map, response.getJSONObject(DICTIONARY))
            listener.onSuccess(map)
        }
        threadPoolExecutor.shutdown()
    }

    private fun addLanguage(map: MutableMap<String, Language>, translation: JSONObject) {
        for (key: String in translation.keys()) {
            val languageJson = translation.getJSONObject(key)
            val language = Language(
                code = key,
                name = languageJson.getString(NAME),
                nativeName = languageJson.getString(
                    NATIVE_NAME
                ),
                isTransliterate = false,
                transliterateScript = null,
                dictionaryScript = mutableListOf(),
                isSupportDictionary = false
            )
            map[key] = language
        }
    }

    private fun addTransliteration(map: MutableMap<String, Language>, transliteration: JSONObject) {
        for (key: String in transliteration.keys()) {
            if (map.containsKey(key)) {
                map[key]?.isTransliterate = true
                val scriptArr = transliteration.getJSONObject(key).getJSONArray(SCRIPT)
                val scriptJson = scriptArr.getJSONObject(0)
                map[key]?.addTransliterate(
                    scriptJson.getString(CODE),
                    scriptJson.getJSONArray(
                        TO_SCRIPT
                    ).getJSONObject(0).getString(CODE)
                )
            }
        }
    }

    private fun addDictionary(map: MutableMap<String, Language>, dictionary: JSONObject) {
        for (key: String in dictionary.keys()) {
            if (map.containsKey(key)) {
                map[key]?.supportDictionary(true)
                val trans = dictionary.getJSONObject(key).getJSONArray(TRANSLATIONS)
                val length = trans.length() - 1
                for (index in 0..length) {
                    map[key]?.addDictionary(trans.getJSONObject(index).getString(CODE))
                }
            }
        }
    }

    companion object {
        private var instance: LanguageDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: LanguageDataSource().also { instance = it }
        }
    }
}
