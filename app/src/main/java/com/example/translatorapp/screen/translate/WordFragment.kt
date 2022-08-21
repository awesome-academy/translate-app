package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.base.OnItemClickListener
import com.example.translatorapp.data.model.BackTranslation
import com.example.translatorapp.data.model.Language
import com.example.translatorapp.databinding.FragmentWordBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.example.ExampleFragment
import com.example.translatorapp.screen.translate.adapter.MeanAdapter
import com.example.translatorapp.screen.translate.adapter.WordAdapter
import com.example.translatorapp.util.NetworkUtils
import com.example.translatorapp.util.addFragmentToParent

class WordFragment :
    BaseFragment<FragmentWordBinding>(FragmentWordBinding::inflate),
    OnItemClickListener<BackTranslation> {

    private var sourceWord: String? = null
    private var list: List<List<Any>> = emptyList()
    private val myActivity by lazy { parentFragment?.activity as? MainActivity }
    private var clearState: () -> Unit = {}
    private var sourceLang: Language? = null
    private var targetLang: Language? = null

    override fun changeToolbar() {
        myActivity?.apply {
            enableView(true)
            changeToolbar(
                getString(R.string.title_app),
                R.drawable.ic_back
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sourceWord?.let { binding.textMeansTitle.text = getString(R.string.translate_text, it) }
        binding.recyclerWordMeans.apply {
            val meanAdapter = MeanAdapter()
            meanAdapter.updatedData(list[0])
            adapter = meanAdapter
            isNestedScrollingEnabled = false
        }
        binding.recyclerSampleWord.apply {
            val wordAdapter = WordAdapter()
            wordAdapter.updateData(list[1])
            wordAdapter.registerListener(this@WordFragment)
            adapter = wordAdapter
            isNestedScrollingEnabled = false
        }
    }

    override fun onDestroyView() {
        clearState()
        (parentFragment as TranslateFragment).changeToolbar()
        super.onDestroyView()
    }

    override fun onClick(data: BackTranslation) {
        if (data.hasExample && sourceLang != null) {
            addExampleFragment(data)
        } else {
            Toast.makeText(context, getString(R.string.msg_no_example), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addExampleFragment(data: BackTranslation) {
        activity?.applicationContext?.let {
            if (NetworkUtils.isNetworkAvailable(it)) {
                addFragmentToParent(
                    fragment = ExampleFragment.newInstance(data, sourceLang, targetLang),
                    addToBackStack = true,
                    addToActivity = true,
                    container = myActivity?.findLayoutContainer()
                )
            } else {
                NetworkUtils.setDialogAction(it) { addExampleFragment(data) }
            }
        }
    }

    companion object {
        fun newInstance(
            sourceLang: Language?,
            targetLang: Language?,
            sourceWord: String,
            list: List<List<Any>>,
            clearState: () -> Unit
        ) =
            WordFragment().apply {
                this.sourceWord = sourceWord
                this.list = list
                this.clearState = clearState
                this.sourceLang = sourceLang
                this.targetLang = targetLang
            }
    }
}
