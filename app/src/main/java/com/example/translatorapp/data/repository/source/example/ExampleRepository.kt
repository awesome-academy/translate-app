package com.example.translatorapp.data.repository.source.example

import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.data.repository.source.DataSource

class ExampleRepository(
    private val exampleDataSource: DataSource.ExampleDataSource
) : Repository.ExampleRepository {

    override fun getExample(
        backTranslation: BackTranslation,
        from: String,
        to: String,
        listener: OnResultListener<List<Example>>
    ) {
        exampleDataSource.getExample(backTranslation, to, from, listener)
    }

    companion object {
        private var instance: ExampleRepository? = null

        fun getInstance(exampleDataSource: DataSource.ExampleDataSource) = synchronized(this) {
            instance ?: ExampleRepository(exampleDataSource).also { instance = it }
        }
    }
}
