package com.example.translatorapp.data.repository.source.history

import android.content.Context
import com.example.translatorapp.constant.Constant
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.source.DataSource
import java.io.File
import java.io.FileWriter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class HistoryDataSource : DataSource.HistoryDataSource {

    private val listLine = mutableListOf<String>()

    override fun getHistory(context: Context, listener: OnResultListener<List<History>>) {
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val file = getFile(context)
            listener.onSuccess(readHistory(file))
        }
        threadPoolExecutor.shutdown()
    }

    override fun writeHistory(context: Context, text: String, continueFlag: Boolean) {
        val threadPoolExecutor = ThreadPoolExecutor(
            1, 1, KEEP_ALIVE_TIME, TimeUnit.SECONDS, LinkedBlockingQueue()
        )
        threadPoolExecutor.execute {
            val file = getFile(context)
            if (listLine.isEmpty()) {
                readHistory(file)
            }
            if (continueFlag) {
                if (!listLine.contains(text.split("\n")[0])) {
                    listLine.add(text.split("\n")[0])
                    writeToFile(file, text, continueFlag)
                }
            } else {
                listLine.clear()
                writeToFile(file, text, continueFlag)
            }
        }
        threadPoolExecutor.shutdown()
    }

    private fun writeToFile(file: File, text: String, continueFlag: Boolean) {
        val writer = FileWriter(file, continueFlag)
        writer.append(text)
        writer.flush()
        writer.close()
    }

    private fun readHistory(file: File): List<History> {
        val listHistory = mutableListOf<History>()
        listLine.clear()
        file.useLines { lines -> lines.forEach { listLine.add(it) } }
        listLine.forEach { item ->
            val splitItem = item.split("\t")
            if (splitItem.size == LIST_SIZE) {
                listHistory.add(
                    History(
                        splitItem[SOURCE_CODE_INDEX],
                        splitItem[TARGET_CODE_INDEX],
                        splitItem[SOURCE_WORD_INDEX],
                        splitItem[MEAN_WORD_INDEX]
                    )
                )
            }
        }
        return listHistory
    }

    private fun getFile(context: Context): File {
        val myDir = File(context.filesDir, Constant.FILE_NAME)
        if (!myDir.exists()) {
            myDir.createNewFile()
        }
        return myDir
    }

    companion object {
        private const val LIST_SIZE = 4
        private const val SOURCE_CODE_INDEX = 0
        private const val TARGET_CODE_INDEX = 1
        private const val SOURCE_WORD_INDEX = 2
        private const val MEAN_WORD_INDEX = 3
        private const val KEEP_ALIVE_TIME: Long = 60
        private var instance: HistoryDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: HistoryDataSource().also { instance = it }
        }
    }
}
