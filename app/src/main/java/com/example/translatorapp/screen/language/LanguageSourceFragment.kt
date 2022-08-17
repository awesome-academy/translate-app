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

class LanguageSourceFragment :
    BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate),
    OnItemClickListener<Language> {

    private var listLanguage: List<Language> = emptyList()
    private var clearState: () -> Unit = {}
    private var changeData: ((Language?, String?) -> Unit) = { _, _ -> }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LanguageAdapter().apply {
            bind.recyclerListLanguage.adapter = this
            updateData(listLanguage)
            registerListener(this@LanguageSourceFragment)
        }
        addListener()
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(getString(R.string.title_language_source), R.drawable.ic_back)
        }
    }

    override fun onClick(data: Language) {
        activity?.onBackPressed()
        changeData(data, null)
        clearState()
    }

    private fun addListener() {
        bind.textDetectLang.setOnClickListener {
            activity?.onBackPressed()
            changeData(null, bind.textDetectLang.text.toString())
        }
    }

    companion object {
        fun newInstance(
            listLanguage: List<Language>,
            clearState: () -> Unit,
            changeData: (Language?, String?) -> Unit
        ) =
            LanguageSourceFragment().apply {
                this.listLanguage = listLanguage
                this.clearState = clearState
                this.changeData = changeData
            }
    }
}
