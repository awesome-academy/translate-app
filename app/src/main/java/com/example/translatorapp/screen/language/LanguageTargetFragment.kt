package com.example.translatorapp.screen.language

import android.os.Bundle
import android.view.View
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.databinding.FragmentLanguageBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.adapter.LanguageAdapter

class LanguageTargetFragment :
    BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate),
    OnItemClickListener<Language> {

    private var listLanguage: List<Language> = emptyList()
    private var clearState: () -> Unit = {}
    private var changeData: ((Language) -> Unit) = {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.textDetectLang.visibility = View.GONE
        LanguageAdapter().apply {
            bind.recyclerListLanguage.adapter = this
            updateData(listLanguage)
            registerListener(this@LanguageTargetFragment)
        }
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(getString(R.string.title_language_target), R.drawable.ic_back)
        }
    }

    override fun onClick(data: Language) {
        activity?.onBackPressed()
        changeData(data)
        clearState()
    }

    companion object {
        fun newInstance(
            listLanguage: List<Language>,
            clearState: () -> Unit,
            changeData: (Language) -> Unit
        ) =
            LanguageTargetFragment().apply {
                this.listLanguage = listLanguage
                this.clearState = clearState
                this.changeData = changeData
            }
    }
}
