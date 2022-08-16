package com.example.translatorapp.data.repository.source.example

import com.example.translatorapp.constant.EXAMPLE_URL
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.source.DataSource
import com.example.translatorapp.data.repository.source.api.postJson
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val TEXT = "text"
private const val TRANSLATION = "translation"
private const val EXAMPLES = "examples"
private const val SOURCE_PREFIX = "sourcePrefix"
private const val SOURCE_TERM = "sourceTerm"
private const val SOURCE_SUFFIX = "sourceSuffix"
private const val TARGET_PREFIX = "targetPrefix"
private const val TARGET_TERM = "targetTerm"
private const val TARGET_SUFFIX = "targetSuffix"
private const val KEEP_ALIVE_TIME: Long = 60

class ExampleDataSource : DataSource.ExampleDataSource {

    override fun getExample(
        backTranslation: BackTranslation,
        to: String,
        from: String,
        listener: OnResultListener<List<Example>>
    ) {
        val url = "$EXAMPLE_URL&from=$from&to=$to"
        val jObj = JSONObject()
        jObj.apply {
            put(TEXT, backTranslation.sampleWord.normalizedText)
            put(TRANSLATION, backTranslation.targetWord.normalizedText)
        }
        val jArr = JSONArray().put(jObj)
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val response = JSONArray(postJson(url, jArr.toString()))
            val listExample = mutableListOf<Example>()
            val examples = response.getJSONObject(0).getJSONArray(EXAMPLES)
            val length = examples.length() - 1
            for (index in 0..length) {
                val exampleJson = examples.getJSONObject(index)
                val example = Example(
                    sourcePrefix = exampleJson.getString(SOURCE_PREFIX),
                    sourceTerm = exampleJson.getString(SOURCE_TERM),
                    sourceSuffix = exampleJson.getString(SOURCE_SUFFIX),
                    targetPrefix = exampleJson.getString(TARGET_PREFIX),
                    targetTerm = exampleJson.getString(TARGET_TERM),
                    targetSuffix = exampleJson.getString(TARGET_SUFFIX)
                )
                listExample.add(example)
            }
            listener.onSuccess(listExample)
        }
        threadPoolExecutor.shutdown()
    }

    companion object {
        private var instance: ExampleDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: ExampleDataSource().also { instance = it }
        }
    }
}
