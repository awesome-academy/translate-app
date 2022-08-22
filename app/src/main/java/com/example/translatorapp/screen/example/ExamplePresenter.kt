package com.example.translatorapp.screen.example

import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Example
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.OnResultListener
import com.example.translatorapp.data.repository.Repository

class ExamplePresenter(
    private val exampleRepository: Repository.ExampleRepository
) : ExampleContract.Presenter {

    private var view: ExampleContract.View? = null

    override fun getExample(backTranslation: BackTranslation, source: Language, target: Language) {
        exampleRepository.getExample(
            backTranslation,
            source.code,
            target.code,
            object : OnResultListener<List<Example>> {
                override fun onSuccess(data: List<Example>) {
                    view?.onGetExampleComplete(data)
                }
            }
        )
    }

    fun setView(view: ExampleContract.View) {
        this.view = view
    }

    companion object {
        private var instance: ExamplePresenter? = null

        fun getInstance(exampleRepository: Repository.ExampleRepository) =
            synchronized(this) {
                instance ?: ExamplePresenter(exampleRepository).also { instance = it }
            }
    }
}
