package com.example.translatorapp.screen.history

import android.content.Context
import com.example.translatorapp.base.BasePresenter
import com.example.translatorapp.data.model.History
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class HistoryPresenter(
    private val repository: Repository.HistoryRepository
) : HistoryContract.Presenter, BasePresenter<HistoryContract.View> {

    private var view: HistoryContract.View? = null
    val listHistory = mutableListOf<History>()

    override fun getHistory(context: Context) {
        repository.getHistory(
            context,
            object : OnResultListener<List<History>> {
                override fun onSuccess(data: List<History>) {
                    listHistory.clear()
                    listHistory.addAll(data)
                    view?.onGetHistoryComplete(data)
                }

                override fun onError(message: Int) {
                    view?.onError(message)
                }
            }
        )
    }

    override fun writeHistory(context: Context, text: String, continueFlag: Boolean) {
        repository.writeHistory(context, text, continueFlag)
    }

    override fun onStart() {
        // No-op
    }

    override fun onStop() {
        // No-op
    }
    override fun setView(view: HistoryContract.View) {
        this.view = view
    }

    companion object {
        private var instance: HistoryPresenter? = null

        fun getInstance(repository: Repository.HistoryRepository) = synchronized(this) {
            instance ?: HistoryPresenter(repository).also { instance = it }
        }
    }
}
