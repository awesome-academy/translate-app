package com.example.translatorapp.data.repository.source.history

import android.content.Context
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository
import com.example.translatorapp.data.repository.source.DataSource

class HistoryRepository(
    private val dataSource: DataSource.HistoryDataSource
) : Repository.HistoryRepository {

    override fun getHistory(context: Context, listener: OnResultListener<List<History>>) {
        dataSource.getHistory(context, listener)
    }

    override fun writeHistory(context: Context, text: String, continueFlag: Boolean) {
        dataSource.writeHistory(context, text, continueFlag)
    }

    companion object {
        private var instance: HistoryRepository? = null

        fun getInstance(dataSource: DataSource.HistoryDataSource) = synchronized(this) {
            instance ?: HistoryRepository(dataSource).also { instance = it }
        }
    }
}
