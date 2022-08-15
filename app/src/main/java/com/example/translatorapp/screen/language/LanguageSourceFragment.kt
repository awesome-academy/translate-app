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

private const val TITLE = "Dịch từ"

class LanguageSourceFragment private constructor(
    private val listLanguage: MutableList<Language>,
    private val clearState: () -> Unit,
    private val changeData: ((Language?, String?) -> Unit)
) : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate),
    OnItemClickListener<Language> {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LanguageAdapter().apply {
            bind.listLanguage.adapter = this
            updateData(listLanguage)
            registerListener(this@LanguageSourceFragment)
        }
        addListener()
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(TITLE, R.drawable.ic_back)
        }
    }

    override fun onClick(data: Language) {
        activity?.onBackPressed()
        changeData(data, null)
        clearState()
    }

    private fun addListener() {
        bind.detectLang.setOnClickListener {
            activity?.onBackPressed()
            changeData(null, bind.detectLang.text.toString())
        }
    }

    companion object {
        fun newInstance(
            listLanguage: MutableList<Language>,
            clearState: () -> Unit,
            changeData: (Language?, String?) -> Unit
        ) =
            LanguageSourceFragment(listLanguage, clearState, changeData)
    }
}
