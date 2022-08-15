package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.databinding.FragmentSentenceBinding
import com.example.translatorapp.screen.translate.adapter.WordAdapter

class SentenceFragment(
    private val list: List<String>,
    private val visibleButton: () -> Unit
) : BaseFragment<FragmentSentenceBinding>(FragmentSentenceBinding::inflate) {

    override fun changeToolbar() {
        (parentFragment as TranslateFragment).changeIconToolbar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = WordAdapter()
        adapter.updateData(list)
        bind.sentenceBreak.adapter = adapter
        bind.sentenceBreak.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() {
        visibleButton()
        (parentFragment as TranslateFragment).changeToolbar()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(list: List<String>, visibleButton: () -> Unit) =
            SentenceFragment(list, visibleButton)
    }
}
