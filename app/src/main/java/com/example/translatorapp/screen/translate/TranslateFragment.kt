package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.data.repository.LanguageRepository
import com.example.translatorapp.data.repository.WordRepository
import com.example.translatorapp.data.repository.source.language.LanguageDataSource
import com.example.translatorapp.data.repository.source.word.WordDataSource
import com.example.translatorapp.databinding.FragmentTranslateBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.TranslateContract
import com.example.translatorapp.screen.TranslatePresenter
import com.example.translatorapp.screen.language.LanguageSourceFragment
import com.example.translatorapp.screen.language.LanguageTargetFragment
import com.example.translatorapp.util.addFragment

private const val TITLE = "Translate App"

class TranslateFragment :
    BaseFragment<FragmentTranslateBinding>(FragmentTranslateBinding::inflate),
    TranslateContract.View {

    private val presenter by lazy {
        TranslatePresenter.getInstance(
            LanguageRepository.getInstance(
                LanguageDataSource.getInstance()
            ),
            WordRepository.getInstance(
                WordDataSource.getInstance()
            )
        )
    }

    override fun changeToolbar() {
        (activity as MainActivity).let {
            it.enableView(false)
            it.changeToolbar(TITLE, R.drawable.ic_menu)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListener()
        presenter.apply {
            setView(this@TranslateFragment)
            getLanguage()
        }
    }

    override fun onGetLanguageSuccess(data: List<Language>) {
        activity?.runOnUiThread {
            presenter.source = data[0]
            presenter.target = data[1]
            bind.btnFrom.text = data[0].nativeName
            bind.btnTo.text = data[1].nativeName
        }
    }

    override fun onTranslateSentenceComplete(data: String) {
        activity?.runOnUiThread {
            bind.output.setText(data)
        }
    }

    override fun onBreakSentenceComplete(data: List<String>) {
        activity?.runOnUiThread {
            bind.translate.visibility = View.GONE
            val fragment = SentenceFragment.newInstance(data) {
                bind.translate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = fragment,
                addToBackStack = true,
                container = bind.supLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    override fun onDictionaryLookupComplete(data: List<List<Any>>) {
        activity?.runOnUiThread {
            bind.translate.visibility = View.GONE
            bind.output.setText(data[2][0].toString())
            val res = listOf(data[0], data[1])
            val wordFragment = WordFragment.newInstance(res) {
                bind.translate.visibility = View.VISIBLE
            }
            addFragment(
                fragment = wordFragment,
                addToBackStack = true,
                container = bind.supLayoutContainer.id,
                manager = childFragmentManager
            )
        }
    }

    private fun addListener() {
        bind.btnFrom.setOnClickListener {
            val sourceFragment =
                LanguageSourceFragment.newInstance(
                    presenter.listLanguage,
                    { clearState() }
                ) { data, text ->
                    data?.let {
                        bind.btnFrom.text = it.nativeName
                        presenter.source = it
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
            val targetFragment = LanguageTargetFragment.newInstance(
                presenter.listLanguage,
                { clearState() }
            ) { data ->
                bind.btnTo.text = data.nativeName
                presenter.target = data
            }
            addFragment(
                fragment = targetFragment,
                addToBackStack = true,
                container = (activity as MainActivity).findLayoutContainer(),
                manager = parentFragmentManager
            )
        }

        bind.translate.setOnClickListener {
            translate(bind.input.text.toString().trim())
        }
    }

    private fun translate(text: String) {
        if (text.contains(" ")) {
            presenter.getTranslateSentence(text)
            presenter.breakSentence(text)
        } else {
            presenter.getTranslateWord(text)
        }
    }

    private fun clearState() {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
        }
        bind.output.setText("")
    }

    fun changeIconToolbar() {
        (activity as MainActivity).let {
            it.enableView(true)
            it.changeToolbar(TITLE, R.drawable.ic_back)
        }
    }

    companion object {
        fun newInstance() = TranslateFragment()
    }
}
