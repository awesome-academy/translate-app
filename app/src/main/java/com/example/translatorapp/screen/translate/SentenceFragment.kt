package com.example.translatorapp.screen.translate

import android.os.Bundle
import android.view.View
import com.example.translatorapp.R
import com.example.translatorapp.base.BaseFragment
import com.example.translatorapp.databinding.FragmentSentenceBinding
import com.example.translatorapp.screen.MainActivity
import com.example.translatorapp.screen.translate.adapter.WordAdapter

class SentenceFragment : BaseFragment<FragmentSentenceBinding>(FragmentSentenceBinding::inflate) {

    private var listSentence: List<String> = emptyList()
    private var clearState: () -> Unit = {}

    override fun changeToolbar() {
        parentFragment?.let {
            (it.activity as? MainActivity)?.apply {
                enableView(true)
                changeToolbar(
                    getString(R.string.title_app),
                    R.drawable.ic_back
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = WordAdapter()
        adapter.updateData(listSentence)
        binding.recyclerSentenceBreak.adapter = adapter
        binding.recyclerSentenceBreak.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() {
        clearState()
        (parentFragment as TranslateFragment).changeToolbar()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(list: List<String>, clearState: () -> Unit) =
            SentenceFragment().apply {
                this.listSentence = list
                this.clearState = clearState
            }
    }
}
