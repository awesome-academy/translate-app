package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
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
import com.example.translatorapp.util.addFragment

class WordFragment :
    BaseFragment<FragmentWordBinding>(FragmentWordBinding::inflate),
    OnItemClickListener<BackTranslation> {

    private var list: List<List<Any>> = emptyList()
    private var clearState: () -> Unit = {}
    private var sourceLang: Language? = null
    private var targetLang: Language? = null

    override fun changeToolbar() {
        parentFragment?.let {
            if (it.activity is MainActivity) {
                (it.activity as MainActivity).apply {
                    enableView(true)
                    changeToolbar(
                        getString(R.string.title_app),
                        R.drawable.ic_back
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        parentFragment?.let {
            addFragment(
                fragment = ExampleFragment.newInstance(data, sourceLang, targetLang),
                addToBackStack = true,
                container = (it.activity as MainActivity).findLayoutContainer(),
                manager = it.parentFragmentManager
            )
        }
    }

    companion object {
        fun newInstance(
            sourceLang: Language?,
            targetLang: Language?,
            list: List<List<Any>>,
            clearState: () -> Unit
        ) =
            WordFragment().apply {
                this.list = list
                this.clearState = clearState
                this.sourceLang = sourceLang
                this.targetLang = targetLang
            }
    }
}
