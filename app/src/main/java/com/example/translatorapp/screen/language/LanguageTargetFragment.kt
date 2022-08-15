package com.example.translatorapp.screen.language

import android.os.Bundle
import android.view.View
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.LanguageRepository
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.databinding.FragmentLanguageBinding
import com.example.translatorapp.screen.DataPresenter
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.adapter.LanguageAdapter
import com.example.translatorapp.screen.language.adapter.OnItemClickListener

private const val title = "Dịch sang"

class LanguageTargetFragment private constructor(
    private val changeData: ((Language) -> Unit)
) : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate),
    OnItemClickListener<Language> {

    private val presenter by lazy {
        DataPresenter.getInstance(
            LanguageRepository.getInstance(
                LanguageDataSource.getInstance()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LanguageAdapter().apply {
            bind.listLanguage.adapter = this
            updateData(presenter.listLanguage)
            registerListener(this@LanguageTargetFragment)
        }
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(title, R.drawable.ic_back)
        }
    }

    override fun onClick(data: Language) {
        activity?.onBackPressed()
        changeData(data)
        presenter.target = data.code
    }

    companion object {
        fun newInstance(changeData: (Language) -> Unit) = LanguageTargetFragment(changeData)
    }
}
