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

private const val TITLE = "Dịch sang"

class LanguageTargetFragment private constructor(
    private val listLanguage: MutableList<Language>,
    private val clearState: () -> Unit,
    private val changeData: ((Language) -> Unit)
) : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate),
    OnItemClickListener<Language> {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.detectLang.visibility = View.GONE
        LanguageAdapter().apply {
            bind.listLanguage.adapter = this
            updateData(listLanguage)
            registerListener(this@LanguageTargetFragment)
        }
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(TITLE, R.drawable.ic_back)
        }
    }

    override fun onClick(data: Language) {
        activity?.onBackPressed()
        changeData(data)
        clearState()
    }

    companion object {
        fun newInstance(
            listLanguage: MutableList<Language>,
            clearState: () -> Unit,
            changeData: (Language) -> Unit
        ) =
            LanguageTargetFragment(listLanguage, clearState, changeData)
    }
}
