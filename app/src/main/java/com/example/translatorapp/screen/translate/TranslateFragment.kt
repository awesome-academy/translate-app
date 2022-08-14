package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.LanguageRepository
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.databinding.FragmentTranslateBinding
import com.example.translatorapp.screen.DataContract
import com.example.translatorapp.screen.DataPresenter
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.language.LanguageSourceFragment
import com.example.translatorapp.screen.language.LanguageTargetFragment
import com.example.translatorapp.util.addFragment

private const val title = "Translate App"

class TranslateFragment :
    BaseFragment<FragmentTranslateBinding>(FragmentTranslateBinding::inflate), DataContract.View {

    private val presenter by lazy {
        DataPresenter.getInstance(
            LanguageRepository.getInstance(
                LanguageDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(false)
            it.changeToolbar(title, R.drawable.ic_menu)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentTranslateBinding.inflate(layoutInflater)
        addListener()
        presenter.apply {
            setView(this@TranslateFragment)
            getLanguage()
        }
        return bind.root
    }

    override fun onGetLanguageSuccess(data: List<Language>) {
        activity?.runOnUiThread {
            bind.btnFrom.text = data[0].nativeName
            bind.btnTo.text = data[1].nativeName
        }
    }

    private fun addListener() {
        bind.btnFrom.setOnClickListener {
            val sourceFragment = LanguageSourceFragment.newInstance { data, text ->
                data?.let {
                    bind.btnFrom.text = it.nativeName
                }
                text?.let {
                    bind.btnFrom.text = it
                }
            }
            addFragment(
                fragment = sourceFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        bind.btnTo.setOnClickListener {
            val targetFragment = LanguageTargetFragment.newInstance { data ->
                bind.btnTo.text = data.nativeName
            }
            addFragment(
                fragment = targetFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }
    }

    companion object {
        fun newInstance() = TranslateFragment()
    }
}
