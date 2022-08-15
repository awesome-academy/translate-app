package com.example.translatorapp.screen

import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.LanguageRepository
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class DataPresenter(
    private val repository: Repository.LanguageRepository
) : DataContract.Presenter {

    private var mView: DataContract.View? = null
    val listLanguage: MutableList<Language> = mutableListOf()
    var source: String? = null
    var target: String? = null

    override fun getLanguage() {
        repository.getLanguage(object : OnResultListener<List<Language>> {
            override fun onSuccess(data: List<Language>) {
                listLanguage.clear()
                listLanguage.addAll(data)
                mView?.onGetLanguageSuccess(data)
            }
        })
    }

    fun setView(view: DataContract.View) {
        mView = view
    }

    companion object {
        private var instance: DataPresenter? = null

        fun getInstance(repository: LanguageRepository): DataPresenter = synchronized(this) {
            instance ?: DataPresenter(repository).also { instance = it }
        }
    }
}
